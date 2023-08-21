package com.github.bogdanovmn.jaclin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class OptionsBuilder implements OptionsInProgressBuilder {
    private final CLIBuilder cliBuilder;
    private final RestrictionsBuilder restrictionsBuilder;

    private final UniqShortNameFactory uniqShortNames = new UniqShortNameFactory();
    private final List<Option> options = new ArrayList<>();


    private static final String HELP_OPT_SHORT_NAME = "h";


    private boolean isRequiredMode;
    private Option.OptionBuilder currentOption;

    OptionsBuilder(CLIBuilder cliBuilder, RestrictionsBuilder restrictionsBuilder) {
        this.cliBuilder = cliBuilder;
        this.restrictionsBuilder = restrictionsBuilder;
    }

    OptionsStarterBuilder required() {
        isRequiredMode = true;
        return this;
    }

    OptionsStarterBuilder notRequired() {
        isRequiredMode = false;
        return this;
    }

    public OptionsInProgressBuilder strArg(String name, String description) {
        String shortName = initNewOption(name);
        currentOption = Option.builder()
            .name(name)
            .shortName(shortName)
            .description(description)
            .isRequired(isRequiredMode)
            .type(String.class);
        return this;
    }

    public OptionsInProgressBuilder intArg(String name, String description) {
        String shortName = initNewOption(name);
        currentOption = Option.builder()
            .name(name)
            .shortName(shortName)
            .description(description)
            .isRequired(isRequiredMode)
            .type(Integer.class);
        return this;
    }

    public <E extends Enum<E>> OptionsInProgressBuilder enumArg(String name, String description, Class<E> type) {
        String shortName = initNewOption(name);
        currentOption = Option.builder()
            .name(name)
            .shortName(shortName)
            .description(description)
            .isRequired(isRequiredMode)
            .type(type);
        return this;
    }

    public OptionsInProgressBuilder flag(String name, String description) {
        String shortName = initNewOption(name);
        currentOption = Option.builder()
            .name(name)
            .shortName(shortName)
            .description(description)
            .isRequired(false)
            .defaultValue(false)
            .type(Boolean.class);
        return this;
    }

    public OptionsInProgressBuilder hasShortName(String shortName) {
        currentOption.shortName(shortName);
        return this;
    }

    public OptionsInProgressBuilder hasDefault(Object value) {
        currentOption.defaultValue(value);
        return this;
    }

    public OptionsInProgressBuilder asList() {
        currentOption.isList(true);
        return this;
    }

    public OptionsInProgressBuilder requires(String... otherOptionNames) {
        restrictionsBuilder.withDependencies(currentOption.build().getName(), otherOptionNames);
        return this;
    }

    @Override
    public OptionsStarterBuilder withRequiredOptions() {
        return this.required();
    }

    @Override
    public OptionsStarterBuilder withOptions() {
        return this.notRequired();
    }

    @Override
    public RestrictionsStarterBuilder withRestrictions() {
        return restrictionsBuilder;
    }

    @Override
    public Runner withEntryPoint(Consumer<ParsedOptions> task) {
        return cliBuilder.withEntryPoint(task);
    }

    List<Option> build() {
        return options;
    }

    private void withHelpFlag() {
        String name = "help";
        uniqShortNames.add(HELP_OPT_SHORT_NAME);
        options.add(
            Option.builder()
                .name(name)
                .shortName(HELP_OPT_SHORT_NAME)
                .description("show this message")
                .type(Boolean.class)
            .build()
        );
    }

    private String initNewOption(String optName) {
        // Before the first option definition we must add the default "--help" option
        // We can do this in Ctor but it is a bad practice
        if (options.isEmpty()) {
            withHelpFlag();
        }
        if (options.stream().anyMatch(opt -> opt.getName().equals(optName))) {
            throw new IllegalStateException(
                String.format("Option '%s' already defined", optName)
            );
        }
        if (currentOption != null) {
            options.add(currentOption.build());
        }
        return uniqShortNames.produce(optName);
    }
}
