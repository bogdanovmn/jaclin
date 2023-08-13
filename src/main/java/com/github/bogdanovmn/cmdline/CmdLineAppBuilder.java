package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.*;

import java.util.*;
import java.util.stream.Collectors;

public class CmdLineAppBuilder {
    private final String[] args;
    private Set<String> atLeastOneRequiredOption;
    private final Map<String, OptionMeta<?>> optionMap = new HashMap<>();
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
        optionMap.put(option.getLongOpt(), OptionMeta.string(option));
        return this;
    }

    private CmdLineAppBuilder requiredArg(String name, String shortName, String description) {
        optionMap.put(
            name,
            OptionMeta.string(
                Option.builder(shortName)
                    .longOpt(name)
                    .hasArgs().argName("ARG")
                    .desc(description)
                    .required()
                .build()
            )
        );
        return this;
    }

    public CmdLineAppBuilder withRequiredArg(String name, String shortName, String description) {
        uniqShortNames.add(shortName);
        return requiredArg(name, shortName, description);
    }

    public CmdLineAppBuilder withRequiredArg(String name, String description) {
        return requiredArg(name, uniqShortNames.produce(name), description);
    }

    private CmdLineAppBuilder arg(String name, String shortName, String description) {
        optionMap.put(
            name,
            OptionMeta.string(
                Option.builder(shortName)
                    .longOpt(name)
                    .hasArgs().argName("ARG")
                    .desc(description)
                .build()
            )
        );
        return this;
    }

    public CmdLineAppBuilder withArg(String name, String description) {
        return arg(name, uniqShortNames.produce(name), description);
    }

    public CmdLineAppBuilder withArg(String name, String shortName, String description) {
        uniqShortNames.add(shortName);
        return arg(name, shortName, description);
    }


    private CmdLineAppBuilder intArg(String name, String shortName, String description) {
        optionMap.put(
            name,
            OptionMeta.integer(
                Option.builder(shortName)
                    .longOpt(name)
                    .hasArgs().argName("ARG")
                    .desc(description)
                .build()
            )
        );
        return this;
    }

    public CmdLineAppBuilder withIntArg(String name, String description) {
        return intArg(name, uniqShortNames.produce(name), description);
    }

    public CmdLineAppBuilder withIntArg(String name, String shortName, String description) {
        uniqShortNames.add(shortName);
        return intArg(name, shortName, description);
    }



    private CmdLineAppBuilder flag(String name, String shortName, String description) {
        optionMap.put(
            name,
            OptionMeta.bool(
                Option.builder(shortName)
                    .longOpt(name)
                    .desc(description)
                .build()
            )
        );
        return this;
    }

    public CmdLineAppBuilder withFlag(String name, String description) {
        return flag(name, uniqShortNames.produce(name), description);
    }

    public CmdLineAppBuilder withFlag(String name, String shortName, String description) {
        uniqShortNames.add(shortName);
        return flag(name, shortName, description);
    }

    public <E extends Enum<E>> CmdLineAppBuilder withEnumArg(String name, String description, Class<E> type) {
        return enumArg(name, uniqShortNames.produce(name), description, type);
    }

    public <E extends Enum<E>> CmdLineAppBuilder withEnumArg(String name, String shortName, String description, Class<E> type) {
        uniqShortNames.add(shortName);
        return enumArg(name, shortName, description, type);
    }

    private <E extends Enum<E>> CmdLineAppBuilder enumArg(String name, String shortName, String description, Class<E> type) {
        optionMap.put(
            name,
            OptionMeta.<E>builder()
                .original(
                    Option.builder(shortName)
                        .longOpt(name)
                        .hasArgs().argName("ARG")
                        .desc(
                            String.format("%s%nPossible values: %s",
                                description,
                                Arrays.stream(type.getEnumConstants())
                                    .map(Enum::name)
                                    .collect(Collectors.joining(" | "))
                            )
                        )
                    .build()
                )
                .type(type)
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
        return new CmdLineApp(entryPoint, new ParsedOptions(cmdLine, optionMap));
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
        } catch (ParseException e) {
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
        helpFormatter.setLeftPadding(2);
        helpFormatter.setWidth(120);
        helpFormatter.printHelp(
            String.format("java -jar %s.jar", Optional.ofNullable(jarName).orElse("the")),
            description,
            options(),
            "",
            true
        );
    }

    private Options options() {
        Options options = new Options();
        this.optionMap.values().forEach(opt -> options.addOption(opt.getOriginal()));
        return options;
    }
}
