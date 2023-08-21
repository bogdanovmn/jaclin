package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class OptionsGroup {
    private final Set<String> names;

    OptionsGroup(String optName) {
        this.names = new HashSet<>();
        this.names.add(optName);
    }

    boolean contains(String optName) {
        return names.contains(optName);
    }

    @Override
    public String toString() {
        return names.size() == 1
            ? names.iterator().next()
            : String.format("{%s}", names.stream().sorted().collect(Collectors.joining(", ")));
    }
}
