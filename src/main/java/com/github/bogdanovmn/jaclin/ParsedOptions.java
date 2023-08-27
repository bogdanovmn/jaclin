package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class ParsedOptions {
    private final Map<String, RuntimeOption> options;
    private final Set<String> allDefinedOptionNames;

    public boolean has(String optName) {
        return options.containsKey(optName);
    }

    public Integer getInt(String optName) {
        return runtimeOpt(optName)
            .map(ro -> ro.value(Integer.class))
            .orElse(null);
    }

    public List<String> getList(String optName) {
        return runtimeOpt(optName)
            .map(ro -> ro.values(String.class))
            .orElse(Collections.emptyList());
    }

    public String get(String optName) {
        return runtimeOpt(optName)
            .map(ro -> ro.value(String.class))
            .orElse(null);
    }

    public boolean enabled(String optName) {
        RuntimeOption flag = options.get(optName);
        if (flag != null) {
            flag.checkType(Boolean.class);
            return true;
        } else {
            return false;
        }
    }

    public Object getEnum(String optName) {
        return runtimeOpt(optName)
            .map(ro -> ro.value(Enum.class))
            .orElse(null);
    }

    public String getEnumAsRawString(String optName) {
        return runtimeOpt(optName)
            .map(RuntimeOption::asString)
            .orElse(null);
    }

    private Optional<RuntimeOption> runtimeOpt(String optName) {
        if (!allDefinedOptionNames.contains(optName)) {
            throw new IllegalArgumentException(
                String.format("Unexpected option name: %s", optName)
            );
        }
        return Optional.ofNullable(
            options.get(optName)
        );
    }

    Set<String> names() {
        return new HashSet<>(options.keySet());
    }
}
