package com.github.bogdanovmn.jaclin;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
class OptionsRestrictions {
    private final List<String> atLeasOneShouldbeUsed;
    private final List<MutualExclusions> mutualExclusions;
    private final Map<String, Set<String>> dependencies;

    public void validate(List<Option> definedOptions, ParsedOptions runtimeOptions) {

    }
}
