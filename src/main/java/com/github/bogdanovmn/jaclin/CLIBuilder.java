package com.github.bogdanovmn.jaclin;

public interface CLIBuilder {
    OptionsStarterBuilder withRequiredOptions();
    OptionsStarterBuilder withOptions();
    RestrictionsStarterBuilder withRestrictions();
    Runner withEntryPoint(CLI.EntryPoint task);
}
