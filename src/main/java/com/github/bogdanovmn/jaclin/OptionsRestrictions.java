package com.github.bogdanovmn.jaclin;

import lombok.Builder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
class OptionsRestrictions {
    private final List<String> atLeasOneShouldbeUsed;
    private final List<MutualExclusions> mutualExclusions;
    private final Map<String, Set<String>> dependencies;

    public void validate(List<Option> definedOptions, ParsedOptions runtimeOptions) {
        atLeastOneRequiredOptionValidation(definedOptions, runtimeOptions);
        dependenciesValidation(definedOptions, runtimeOptions);
        mutualExclusionsValidation(definedOptions, runtimeOptions);
    }

    private void dependenciesValidation(List<Option> definedOptions, ParsedOptions runtimeOptions) {
        if (!dependencies.isEmpty()) {
            Set<String> allRuntimeOptions = runtimeOptions.names();
            Set<String> allDefinedOptions = definedOptions.stream().map(Option::getName).collect(Collectors.toSet());
            dependencies.forEach((parent, child) -> {
                Set<String> unknown = child.stream().filter(
                    optName -> !allDefinedOptions.contains(optName)
                ).collect(Collectors.toSet());
                if (!unknown.isEmpty()) {
                    throw new IllegalStateException(
                        String.format(
                            "Unknown options in dependencies configuration: %s", unknown
                        )
                    );
                }
                if (allRuntimeOptions.contains(parent) && !allRuntimeOptions.containsAll(child)) {
                    Set<String> missedOptions = new HashSet<>(child);
                    missedOptions.removeAll(allRuntimeOptions);
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

    private void atLeastOneRequiredOptionValidation(List<Option> definedOptions, ParsedOptions runtimeOptions) {
        if (atLeasOneShouldbeUsed.isEmpty()) {
            return;
        }
        Set<String> allOptionNames = definedOptions.stream().map(Option::getName).collect(Collectors.toSet());
        if (!allOptionNames.containsAll(atLeasOneShouldbeUsed)) {
            Set<String> unknownOptions = new HashSet<>(atLeasOneShouldbeUsed);
            unknownOptions.removeAll(allOptionNames);
            throw new IllegalStateException("Unknown option: " + unknownOptions);
        }

        if (runtimeOptions.names().stream().noneMatch(atLeasOneShouldbeUsed::contains)) {
            throw new IllegalStateException(
                String.format(
                    "You should use at least one of these options: '%s'",
                    atLeasOneShouldbeUsed.stream()
                        .sorted()
                        .collect(Collectors.joining("', '"))
                )
            );
        }
    }

    private void mutualExclusionsValidation(List<Option> definedOptions, ParsedOptions runtimeOptions) {
        if (mutualExclusions.isEmpty()) {
            return;
        }
        for (MutualExclusions mExcl : mutualExclusions) {
            definedOptions.forEach(opt -> {
                if (mExcl.contains(opt.getName()) && opt.isRequired()) {
                    throw new IllegalStateException(
                        String.format("Mutual exclusion options can't be required, but: %s", opt.getName())
                    );
                }
            });
            List<String> allRuntimeMutualExclusions = runtimeOptions.names().stream()
                .map(mExcl::optionGroup)
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
    }
}
