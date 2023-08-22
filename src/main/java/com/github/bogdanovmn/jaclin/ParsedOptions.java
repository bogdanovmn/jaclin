package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ParsedOptions {
    private final Map<String, RuntimeOption> options;

    public boolean has(String optName) {
        return options.containsKey(optName);
    }

    public Integer getInt(String optName) {
        return runtimeOpt(optName).value(Integer.class);
    }

    public List<String> getList(String optName) {
        return runtimeOpt(optName).values(String.class);
    }

    public String get(String optName) {
        return runtimeOpt(optName).value(String.class);
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
        return runtimeOpt(optName).value(Enum.class);
    }

    public String getEnumAsRawString(String optName) {
        return runtimeOpt(optName).asString();
    }

    private RuntimeOption runtimeOpt(String optName) {
        RuntimeOption opt = options.get(optName);
        if (opt == null) {
            throw new IllegalArgumentException(
                String.format("Unexpected option name: %s", optName)
            );
        }
        return opt;
    }

    Set<String> names() {
        return new HashSet<>(options.keySet());
    }
}
