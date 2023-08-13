package com.github.bogdanovmn.cmdline;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.apache.commons.cli.Option;

@Value
@Builder
class OptionMeta<T> {
    Option.Builder original;
    Class<T> type;
    @With
    T defaultValue;

    static OptionMeta<String> string(Option.Builder opt) {
        return OptionMeta.<String>builder()
            .type(String.class)
            .original(opt)
        .build();
    }

    static OptionMeta<Boolean> bool(Option.Builder opt) {
        return OptionMeta.<Boolean>builder()
            .type(Boolean.class)
            .defaultValue(false)
            .original(opt)
        .build();
    }

    static OptionMeta<Integer> integer(Option.Builder opt) {
        return OptionMeta.<Integer>builder()
            .type(Integer.class)
            .original(opt)
        .build();
    }
}
