package com.github.bogdanovmn.cmdline;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.cli.Option;

@Value
@Builder
class OptionMeta <T> {
    Option original;
    Class<T> type;
    T defaultValue;

    static OptionMeta<String> string(Option opt) {
        return OptionMeta.<String>builder()
            .type(String.class)
            .original(opt)
        .build();
    }

    static OptionMeta<Boolean> bool(Option opt) {
        return OptionMeta.<Boolean>builder()
            .type(Boolean.class)
            .defaultValue(false)
            .original(opt)
        .build();
    }

    static OptionMeta<Integer> integer(Option opt) {
        return OptionMeta.<Integer>builder()
            .type(Integer.class)
            .original(opt)
        .build();
    }
}
