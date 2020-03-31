package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.*;

import java.util.*;
import java.util.stream.Collectors;

public class CmdLineAppBuilder {
	private final String[] args;
	private Set<String> atLeastOneRequiredOption;
	private final Map<String, Option> optionMap = new HashMap<>();
	private String jarName = "<app-name>";
	private String description = "";
	private CmdLineAppEntryPoint entryPoint;

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
		optionMap.put(option.getLongOpt(), option);
		return this;
	}

	public CmdLineAppBuilder withRequiredArg(String name, String description) {
		optionMap.put(
			name,
			Option.builder(name.substring(0, 1).toLowerCase())
				.longOpt(name)
				.hasArg().argName("ARG")
				.desc(description)
				.required()
			.build()
		);
		return this;
	}

	public CmdLineAppBuilder withArg(String name, String description) {
		optionMap.put(
			name,
			Option.builder(name.substring(0, 1).toLowerCase())
				.longOpt(name)
				.hasArg().argName("ARG")
				.desc(description)
				.build()
		);
		return this;
	}

	public CmdLineAppBuilder withFlag(String name, String description) {
		optionMap.put(
			name,
			Option.builder(name.substring(0, 1).toLowerCase())
				.longOpt(name)
				.desc(description)
			.build()
		);
		return this;
	}

	public CmdLineAppBuilder withAtLeastOneRequiredOption(String... options) {
		atLeastOneRequiredOption = new HashSet<>(Arrays.asList(options));
		return this;
	}

	public CmdLineAppBuilder withEntryPoint(CmdLineAppEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
		return this;
	}

	public CmdLineApp build() {
		try {
			withFlag("help", "show this message");
			CommandLine cmdLine = new DefaultParser().parse(options(), args);

			if (cmdLine.hasOption("h")) {
				printHelp();
				entryPoint = doNothing -> {};
			}

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
			return new CmdLineApp(entryPoint, cmdLine);
		}
		catch (ParseException e) {
			printHelp();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void printHelp() {
		new HelpFormatter()
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