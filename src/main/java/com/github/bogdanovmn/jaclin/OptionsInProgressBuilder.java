package com.github.bogdanovmn.jaclin;

public interface OptionsInProgressBuilder extends OptionsStarterBuilder, CLIBuilder {

    OptionsInProgressBuilder hasShortName(String shortName);

    OptionsInProgressBuilder hasDefault(Object value);

    OptionsInProgressBuilder asList();

    OptionsInProgressBuilder requires(String... otherOptionNames);

}
