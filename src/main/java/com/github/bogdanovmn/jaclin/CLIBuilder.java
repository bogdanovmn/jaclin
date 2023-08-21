package com.github.bogdanovmn.jaclin;

import java.util.function.Consumer;

public interface CLIBuilder {
    OptionsStarterBuilder withRequiredOptions();
    OptionsStarterBuilder withOptions();
    RestrictionsStarterBuilder withRestrictions();
    Runner withEntryPoint(Consumer<ParsedOptions> task);
}
