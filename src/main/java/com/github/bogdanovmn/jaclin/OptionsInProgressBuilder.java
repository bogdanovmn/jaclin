package com.github.bogdanovmn.jaclin;

public interface OptionsInProgressBuilder extends OptionsStarterBuilder, CLIBuilder {

    OptionsInProgressBuilder withShortName(String shortName);

    OptionsInProgressBuilder withDefault(Object value);

    OptionsInProgressBuilder asList();

    OptionsInProgressBuilder requires(String... otherOptionNames);

}
