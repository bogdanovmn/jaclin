package com.github.bogdanovmn.jaclin;

public interface FlagOptionInProgressBuilder extends OptionsStarterBuilder, CLIBuilder {

    FlagOptionInProgressBuilder hasShortName(String shortName);

    FlagOptionInProgressBuilder requires(String... otherOptionNames);
}
