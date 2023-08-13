package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.*;

import java.util.*;
import java.util.stream.Collectors;

public class CmdLineAppBuilder {
	private final String[] args;
	private Set<String> atLeastOneRequiredOption;
	private final Map<String, Option> optionMap = new HashMap<>();
	private final Map<String, Set<String>> dependencies = new HashMap<>();
	private String jarName = "<app-name>";
	private String description = "";
	private CmdLineAppEntryPoint entryPoint;
	private final UniqShortNameFactory uniqShortNames = new UniqShortNameFactory();

	public CmdLineAppBuilder(String[] args) {
		this.args = args;
	}

	public CmdLineAppBuilder withJarName(String jarName) {
		this.jarName = jarName;
		return this;
	}

	public CmdLineAppBuilder withDescription(String appDescription) {
		this.description = appDescription;
		return this;
	}

	public CmdLineAppBuilder withCustomOption(Option option) {
		if (option.getLongOpt() == null) {
			throw new IllegalStateException("You should define a long option name for " + option);
		}
		uniqShortNames.add(option.getOpt());
		optionMap.put(option.getLongOpt(), option);
		return this;
	}

	private CmdLineAppBuilder withRequiredArg(String name, String shortName, String description, boolean shortNameHasToBeValidated) {
		if (shortNameHasToBeValidated) {
			uniqShortNames.add(shortName);
		}
		optionMap.put(
			name,
			Option.builder(shortName)
				.longOpt(name)
				.hasArgs().argName("ARG")
				.desc(description)
				.required()
				.build()
		);
		return this;
	}

	public CmdLineAppBuilder withRequiredArg(String name, String shortName, String description) {
		return withRequiredArg(name, shortName, description, true);
	}

	public CmdLineAppBuilder withRequiredArg(String name, String description) {
		return withRequiredArg(name, uniqShortNames.produce(name), description, false);
	}

	private CmdLineAppBuilder withArg(String name, String shortName, String description, boolean shortNameHasToBeValidated) {
		if (shortNameHasToBeValidated) {
			uniqShortNames.add(shortName);
		}
		optionMap.put(
			name,
			Option.builder(shortName)
				.longOpt(name)
				.hasArgs().argName("ARG")
				.desc(description)
				.build()
		);
		return this;
	}

	public CmdLineAppBuilder withArg(String name, String description) {
		return withArg(name, uniqShortNames.produce(name), description, false);
	}

	public CmdLineAppBuilder withArg(String name, String shortName, String description) {
		return withArg(name, shortName, description, true);
	}

	private CmdLineAppBuilder withFlag(String name, String shortName, String description, boolean shortNameHasToBeValidated) {
		if (shortNameHasToBeValidated) {
			uniqShortNames.add(shortName);
		}
		optionMap.put(
			name,
			Option.builder(shortName)
				.longOpt(name)
				.desc(description)
			.build()
		);
		return this;
	}

	public CmdLineAppBuilder withFlag(String name, String description) {
		return withFlag(name, uniqShortNames.produce(name), description, false);
	}

	public CmdLineAppBuilder withFlag(String name, String shortName, String description) {
		return withFlag(name, shortName, description, true);
	}

	public CmdLineAppBuilder withAtLeastOneRequiredOption(String... options) {
		atLeastOneRequiredOption = new HashSet<>(Arrays.asList(options));
		return this;
	}

	public CmdLineAppBuilder withEntryPoint(CmdLineAppEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
		return this;
	}

	public CmdLineAppBuilder withDependencies(String parentOption, String... childOptions) {
		dependencies.put(
			parentOption,
			new HashSet<>(Arrays.asList(childOptions))
		);
		return this;
	}

	public CmdLineApp build() {
		CommandLine cmdLine = parserWithHelpOption();
		atLeastOneRequiredOptionValidation(cmdLine);
		dependenciesValidation(cmdLine);
		return new CmdLineApp(entryPoint, cmdLine);
	}

	private void dependenciesValidation(CommandLine cmdLine) {
		if (!dependencies.isEmpty()) {
			Set<String> allOptions = Arrays.stream(cmdLine.getOptions())
				.map(Option::getLongOpt)
				.collect(Collectors.toSet());

			dependencies.forEach((parent, child) -> {
				if (allOptions.contains(parent) && !allOptions.containsAll(child)) {
					Set<String> missedOptions = new HashSet<>(child);
					missedOptions.removeAll(allOptions);
					throw new IllegalStateException(
						String.format(
							"With '%s' option you must also specify these: %s",
							parent, missedOptions
						)
					);
				}
			});
		}
	}

	private void atLeastOneRequiredOptionValidation(CommandLine cmdLine) {
		if (atLeastOneRequiredOption != null) {
			Set<String> allOptionNames = optionMap.keySet();
			if (!allOptionNames.containsAll(atLeastOneRequiredOption)) {
				Set<String> unknownOptions = new HashSet<>(atLeastOneRequiredOption);
				unknownOptions.removeAll(allOptionNames);
				throw new IllegalStateException("Unknown option: " + unknownOptions);
			}

			Set<String> allRuntimeOptionNames = Arrays.stream(cmdLine.getOptions())
				.map(Option::getLongOpt)
				.collect(Collectors.toSet());

			if (allRuntimeOptionNames.stream().noneMatch(opt -> atLeastOneRequiredOption.contains(opt))) {
				throw new IllegalStateException(
					String.format(
						"You should use at least one of these options: '%s'",
							atLeastOneRequiredOption.stream()
								.sorted()
								.collect(Collectors.joining("', '"))
					)
				);
			}
		}
	}

	private CommandLine parserWithHelpOption() {
		withFlag("help", "show this message");
		CommandLine cmdLine;
		try {
			cmdLine = new DefaultParser().parse(options(), args);
		}
		catch (ParseException e) {
			printHelp();
			throw new RuntimeException(e.getMessage());
		}

		if (cmdLine.hasOption("h")) {
			printHelp();
			entryPoint = doNothing -> {};
		}
		return cmdLine;
	}

	private void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setOptionComparator(
			Comparator.comparingInt(
				option -> uniqShortNames.orderNumberByShortName(
					option.getOpt()
				)
			)
		);
		helpFormatter
			.printHelp(
				String.format("java -jar %s.jar", Optional.ofNullable(jarName).orElse("the")),
				description,
				options(),
				"",
				true
			);
	}

	private Options options() {
		Options options = new Options();
		this.optionMap.values().forEach(options::addOption);
		return options;
	}
}