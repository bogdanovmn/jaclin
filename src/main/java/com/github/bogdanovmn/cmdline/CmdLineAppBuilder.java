package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CmdLineAppBuilder {
    private final String[] args;
    private final Map<String, OptionMeta<?>> optionMap = new HashMap<>();
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    private final UniqShortNameFactory uniqShortNames = new UniqShortNameFactory();
    private Set<String> atLeastOneRequiredOption;
    private MutualExclusions mutualExclusions;
    private String jarName = "<app-name>";
    private String description = "";
    private CmdLineAppEntryPoint entryPoint;
    private String currentOpt = null;

    private static final String HELP_OPT_SHORT_NAME = "h";

    public CmdLineAppBuilder(String[] args) {
        this.args = args;
    }

    private void clearCurrentOpt() {
        currentOpt = null;
    }

    private void setCurrentOpt(String optName) {
        // Before the first option definition we must add the default "--help" option
        // We can do this in Ctor but it is a bad practice
        if (optionMap.isEmpty()) {
            withHelpFlag();
        }
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

    private void withHelpFlag() {
        String name = "help";
        uniqShortNames.add(HELP_OPT_SHORT_NAME);
        optionMap.put(
            name,
            OptionMeta.bool(
                Option.builder(HELP_OPT_SHORT_NAME)
                    .longOpt(name)
                    .desc("show this message")
            )
        );
    }

    public CmdLineAppBuilder withShort(String shortName) {
        checkForCurrentOpt();
        uniqShortNames.add(shortName);
        uniqShortNames.remove(
            optionMap.get(currentOpt).getOriginal().build().getOpt()
        );
        optionMap.get(currentOpt).getOriginal().option(shortName);
        return this;
    }

    public CmdLineAppBuilder required() {
        checkForCurrentOpt();
        optionMap.get(currentOpt).getOriginal().required();
        return this;
    }

    public CmdLineAppBuilder withDefault(Object defaultValue) {
        checkForCurrentOpt();
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

    public CmdLineAppBuilder requires(String... otherOptions) {
        checkForCurrentOpt();
        dependencies.put(
            currentOpt,
            new HashSet<>(Arrays.asList(otherOptions))
        );
        return this;
    }

    private void checkForCurrentOpt() {
        if (currentOpt == null) {
            throw new IllegalStateException(
                String.format(
                    "The %s#%s() method must be apply on an option",
                        Thread.currentThread().getStackTrace()[2].getClassName(),
                        Thread.currentThread().getStackTrace()[2].getMethodName()
                )
            );
        }
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

    public CmdLineAppBuilder withMutualExclusions(Object... options) {
        clearCurrentOpt();
        mutualExclusions = MutualExclusions.of(options);
        return this;
    }

    public CmdLineAppBuilder withEntryPoint(CmdLineAppEntryPoint entryPoint) {
        clearCurrentOpt();
        this.entryPoint = entryPoint;
        return this;
    }

    public CmdLineApp build() {
        clearCurrentOpt();
        CommandLine cmdLine = parserWithHelpOption();
        atLeastOneRequiredOptionValidation(cmdLine);
        mutualExclusionsValidation(cmdLine);
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
        if (atLeastOneRequiredOption == null) {
            return;
        }
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

    private void mutualExclusionsValidation(CommandLine cmdLine) {
        if (mutualExclusions == null) {
            return;
        }
        optionMap.forEach((name, meta) -> {
            if (mutualExclusions.contains(name) && meta.getOriginal().build().isRequired()) {
                throw new IllegalStateException(
                    String.format("Mutual exclusion options can't be required, but: %s", name)
                );
            }
        });
        List<String> allRuntimeMutualExclusions = Arrays.stream(cmdLine.getOptions())
            .map(option -> mutualExclusions.optionGroup(option.getLongOpt()))
            .filter(group -> group != null)
            .map(OptionsGroup::toString)
            .sorted()
            .collect(Collectors.toList());
        if (allRuntimeMutualExclusions.size() > 1) {
            throw new IllegalStateException(
                String.format("Mutual exclusion: %s, expected only one of these", allRuntimeMutualExclusions)
            );
        }
    }

    private CommandLine parserWithHelpOption() {
        CommandLine cmdLine;
        try {
            cmdLine = new DefaultParser().parse(options(), args);
        } catch (ParseException e) {
            printHelp();
            throw new RuntimeException(e.getMessage());
        }

        if (cmdLine.hasOption(HELP_OPT_SHORT_NAME)) {
            printHelp();
            entryPoint = doNothing -> {};
        }
        return cmdLine;
    }

    private void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(
            Comparator.comparingInt(
                option -> HELP_OPT_SHORT_NAME.equals(option.getOpt())
                    ? Integer.MAX_VALUE // --help option should be in the bottom
                    : uniqShortNames.orderNumberByShortName(
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
