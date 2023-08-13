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
    private String currentOpt = null;

    public CmdLineAppBuilder(String[] args) {
        this.args = args;
    }

    private void clearCurrentOpt() {
        currentOpt = null;
    }

    private void setCurrentOpt(String optName) {
        if (optionMap.containsKey(optName)) {
            throw new IllegalStateException(
                String.format("Option '%s' already defined", optName)
            );
        }
        currentOpt = optName;
    }

    public CmdLineAppBuilder withJarName(String jarName) {
        clearCurrentOpt();
        this.jarName = jarName;
        return this;
    }

    public CmdLineAppBuilder withDescription(String appDescription) {
        clearCurrentOpt();
        this.description = appDescription;
        return this;
    }

    public CmdLineAppBuilder withCustomOption(Option.Builder option) {
        clearCurrentOpt();
        Option prebuildOpt = option.build();
        if (prebuildOpt.getLongOpt() == null) {
            throw new IllegalStateException("You should define a long option name for " + option);
        }
        uniqShortNames.add(prebuildOpt.getOpt());
        optionMap.put(prebuildOpt.getLongOpt(), OptionMeta.string(option));
        return this;
    }

    public CmdLineAppBuilder withArg(String name, String description) {
        setCurrentOpt(name);
        optionMap.put(
            name,
            OptionMeta.string(
                Option.builder(uniqShortNames.produce(name))
                    .longOpt(name)
                    .hasArgs().argName("STR")
                    .desc(description)
            )
        );
        return this;
    }

    public CmdLineAppBuilder withIntArg(String name, String description) {
        setCurrentOpt(name);
        optionMap.put(
            name,
            OptionMeta.integer(
                Option.builder(uniqShortNames.produce(name))
                    .longOpt(name)
                    .hasArgs().argName("INT")
                    .desc(description)
            )
        );
        return this;
    }

    public CmdLineAppBuilder withFlag(String name, String description) {
        setCurrentOpt(name);
        optionMap.put(
            name,
            OptionMeta.bool(
                Option.builder(uniqShortNames.produce(name))
                    .longOpt(name)
                    .desc(description)
            )
        );
        return this;
    }

    public CmdLineAppBuilder withShort(String shortName) {
        if (currentOpt == null) {
            throw new IllegalStateException("withShort() method must be apply on an option");
        }
        uniqShortNames.add(shortName);
        uniqShortNames.remove(
            optionMap.get(currentOpt).getOriginal().build().getOpt()
        );
        optionMap.get(currentOpt).getOriginal().option(shortName);
        return this;
    }

    public CmdLineAppBuilder required() {
        if (currentOpt == null) {
            throw new IllegalStateException("required() method must be apply on an option");
        }
        optionMap.get(currentOpt).getOriginal().required();
        return this;
    }

    public CmdLineAppBuilder withDefault(Object defaultValue) {
        if (currentOpt == null) {
            throw new IllegalStateException("withDefault() method must be apply on an option");
        }
        OptionMeta meta = optionMap.get(currentOpt);
        meta.getOriginal().desc(
            String.format(
                "%s%nDefault: %s",
                    meta.getOriginal().build().getDescription(),
                    defaultValue
            )
        );
        optionMap.put(currentOpt, meta.withDefaultValue(defaultValue));
        return this;
    }

    public <E extends Enum<E>> CmdLineAppBuilder withEnumArg(String name, String description, Class<E> type) {
        setCurrentOpt(name);
        optionMap.put(
            name,
            OptionMeta.<E>builder()
                .original(
                    Option.builder(uniqShortNames.produce(name))
                        .longOpt(name)
                        .hasArgs().argName("ENUM")
                        .desc(
                            String.format("%s%nPossible values: %s",
                                description,
                                Arrays.stream(type.getEnumConstants())
                                    .map(Enum::name)
                                    .collect(Collectors.joining(" | "))
                            )
                        )
                )
                .type(type)
            .build()
        );
        return this;
    }

    public CmdLineAppBuilder withAtLeastOneRequiredOption(String... options) {
        clearCurrentOpt();
        atLeastOneRequiredOption = new HashSet<>(Arrays.asList(options));
        return this;
    }

    public CmdLineAppBuilder withEntryPoint(CmdLineAppEntryPoint entryPoint) {
        clearCurrentOpt();
        this.entryPoint = entryPoint;
        return this;
    }

    public CmdLineAppBuilder withDependencies(String parentOption, String... childOptions) {
        clearCurrentOpt();
        dependencies.put(
            parentOption,
            new HashSet<>(Arrays.asList(childOptions))
        );
        return this;
    }

    public CmdLineApp build() {
        clearCurrentOpt();
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
        this.optionMap.values().forEach(opt -> options.addOption(opt.getOriginal().build()));
        return options;
    }
}
