package com.github.bogdanovmn.cmdline;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class MutualExclusions {
    private final List<OptionsGroup> groups;

    static MutualExclusions of(Object[] optionNames) {
        if (optionNames.length < 2) {
            throw new IllegalArgumentException("Mutual exclusion must contain more than one option");
        }
        Set<String> uniqOptions = new HashSet<>();
        List<OptionsGroup> groups = new ArrayList<>(optionNames.length);
        for (Object rawNames : optionNames) {
            if (rawNames instanceof String) {
                String optName = (String) rawNames;
                if (!uniqOptions.add(optName)) {
                    throw new IllegalArgumentException(
                        String.format("Mutual exclusions already contains '%s' option", optName)
                    );
                }
                groups.add(
                    new OptionsGroup((String) rawNames)
                );
            } else if (rawNames instanceof Collection) {
                HashSet<String> names;
                try {
                    names = new HashSet<>((Collection<String>) rawNames);
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("Mutual exclusion group must contain option names");
                }
                for (String name : names) {
                    if (!uniqOptions.add(name)) {
                        throw new IllegalArgumentException(
                            String.format("Mutual exclusions already contains '%s' option", name)
                        );
                    }
                }
                groups.add(
                    new OptionsGroup(names)
                );
            } else {
                throw new IllegalArgumentException("Mutual exclusion arguments must be names or collections of names");
            }
        }
        return new MutualExclusions(groups);
    }

    public boolean contains(String optName) {
        return groups.stream()
            .anyMatch(g -> g.contains(optName));
    }

    public OptionsGroup optionGroup(String optName) {
        return groups.stream()
            .filter(g -> g.contains(optName))
            .findFirst().orElse(null);
    }
}
