package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
class RestrictionsBuilder implements RestrictionsInProgressBuilder {
    private final CLIBuilder cliBuilder;

    private List<String> atLeasOneShouldbeUsed = new ArrayList<>();
    private final List<MutualExclusions> mutualExclusions = new ArrayList<>();
    private final Map<String, Set<String>> dependencies = new HashMap<>();

    @Override
    public OptionsStarterBuilder withRequiredOptions() {
        return cliBuilder.withRequiredOptions();
    }

    @Override
    public OptionsStarterBuilder withOptions() {
        return cliBuilder.withOptions();
    }

    @Override
    public RestrictionsInProgressBuilder withRestrictions() {
        return this;
    }

    @Override
    public Runner withEntryPoint(Consumer<ParsedOptions> task) {
        return cliBuilder.withEntryPoint(task);
    }

    @Override
    public RestrictionsInProgressBuilder mutualExclusions(Object... options) {
        mutualExclusions.add(MutualExclusions.of(options));
        return this;
    }

    @Override
    public RestrictionsInProgressBuilder atLeastOneShouldBeUsed(String... options) {
        atLeasOneShouldbeUsed = Arrays.asList(options);
        return this;
    }

    void withDependencies(String optName, String... otherOptNames) {
        dependencies.put(
            optName,
            new HashSet<>(Arrays.asList(otherOptNames))
        );
    }

    OptionsRestrictions build() {
        return OptionsRestrictions.builder()
            .dependencies(dependencies)
            .atLeasOneShouldbeUsed(atLeasOneShouldbeUsed)
            .mutualExclusions(mutualExclusions)
        .build();
    }
}
