package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FlagOptionBuilder implements FlagOptionInProgressBuilder {
    private final OptionsBuilder originalBuilder;


    @Override
    public OptionsInProgressBuilder strArg(String name, String description) {
        return originalBuilder.strArg(name, description);
    }

    @Override
    public OptionsInProgressBuilder intArg(String name, String description) {
        return originalBuilder.intArg(name, description);
    }

    @Override
    public <E extends Enum<E>> OptionsInProgressBuilder enumArg(String name, String description, Class<E> type) {
        return originalBuilder.enumArg(name, description, type);
    }

    @Override
    public FlagOptionInProgressBuilder flag(String name, String description) {
        originalBuilder.flag(name, description);
        return this;
    }

    @Override
    public FlagOptionInProgressBuilder hasShortName(String shortName) {
        originalBuilder.hasShortName(shortName);
        return this;
    }

    @Override
    public FlagOptionInProgressBuilder requires(String... otherOptionNames) {
        originalBuilder.requires(otherOptionNames);
        return this;
    }

    @Override
    public OptionsStarterBuilder withRequiredOptions() {
        return originalBuilder.required();
    }

    @Override
    public OptionsStarterBuilder withOptions() {
        return originalBuilder.notRequired();
    }

    @Override
    public RestrictionsStarterBuilder withRestrictions() {
        return originalBuilder.withRestrictions();
    }

    @Override
    public Runner withEntryPoint(CLI.EntryPoint task) {
        return originalBuilder.withEntryPoint(task);
    }
}
