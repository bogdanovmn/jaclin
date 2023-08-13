package com.github.bogdanovmn.cmdline;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ParsedOptions {
    private final CommandLine cmdLineArgs;
    private final Map<String, OptionMeta<?>> meta;

    Integer getInt(String optName) {
        OptionMeta<Integer> opt = optMeta(optName, Integer.class);
        return Optional.ofNullable(
            cmdLineArgs.getOptionValue(optName)
        ).map(Integer::parseInt)
            .orElse(opt.getDefaultValue());
    }

    List<String> getList(String optName) {
        OptionMeta<String> opt = optMeta(optName, String.class);
        return Arrays.stream(cmdLineArgs.getOptionValues(optName))
            .collect(Collectors.toList());
    }

    String get(String optName) {
        OptionMeta<String> opt = optMeta(optName, String.class);
        return cmdLineArgs.getOptionValue(
            optName,
            opt.getDefaultValue()
        );
    }

    boolean getBool(String optName) {
        optMeta(optName, Boolean.class);
        return cmdLineArgs.hasOption(optName);
    }

    Object getEnum(String optName) {
        OptionMeta opt = optMeta(optName);
        return Enum.valueOf(
            opt.getType(),
            cmdLineArgs.getOptionValue(
                optName,
                String.valueOf(
                    opt.getDefaultValue()
                )
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
