package com.github.bogdanovmn.cmdline;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ParsedOptions {
    private final CommandLine cmdLineArgs;
    private final Map<String, OptionMeta<?>> meta;

    public boolean has(String optName) {
        return cmdLineArgs.hasOption(optName);
    }

    public Integer getInt(String optName) {
        OptionMeta<Integer> opt = optMeta(optName, Integer.class);
        return Optional.ofNullable(
            cmdLineArgs.getOptionValue(optName)
        ).map(Integer::parseInt)
            .orElse(opt.getDefaultValue());
    }

    public List<String> getList(String optName) {
        OptionMeta<String> opt = optMeta(optName, String.class);
        return Arrays.stream(cmdLineArgs.getOptionValues(optName))
            .collect(Collectors.toList());
    }

    public String get(String optName) {
        OptionMeta<String> opt = optMeta(optName, String.class);
        return cmdLineArgs.getOptionValue(
            optName,
            opt.getDefaultValue()
        );
    }

    public boolean getBool(String optName) {
        optMeta(optName, Boolean.class);
        return cmdLineArgs.hasOption(optName);
    }

    public Object getEnum(String optName) {
        OptionMeta opt = optMeta(optName);
        if (!cmdLineArgs.hasOption(optName)) {
            return opt.getDefaultValue();
        } else {
            try {
                return Enum.valueOf(
                    opt.getType(),
                    cmdLineArgs.getOptionValue(optName)
                );
            } catch (IllegalArgumentException ex) {
                return Enum.valueOf(
                    opt.getType(),
                    cmdLineArgs.getOptionValue(optName).toUpperCase()
                );
            }
        }
    }

    public String getEnumAsRawString(String optName) {
        OptionMeta<?> opt = optMeta(optName);
        return cmdLineArgs.getOptionValue(
            optName,
            String.valueOf(
                opt.getDefaultValue()
            )
        );
    }

    private OptionMeta<?> optMeta(String optName) {
        OptionMeta<?> opt = meta.get(optName);
        if (opt == null) {
            throw new IllegalArgumentException(
                String.format("Unexpected option name: %s", optName)
            );
        }
        return opt;
    }

    private <T> OptionMeta<T> optMeta(String optName, Class<T> expectedType) {
        OptionMeta<?> opt = optMeta(optName);
        if (!opt.getType().equals(expectedType)) {
            throw new IllegalArgumentException(
                String.format(
                    "Argument %s is not %s but %s",
                        optName, expectedType.getSimpleName(), opt.getType().getSimpleName()
                )
            );
        }
        return (OptionMeta<T>) opt;
    }
}
