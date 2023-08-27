package com.github.bogdanovmn.jaclin;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
class Option {
    @NonNull
    String name;
    @NonNull
    String shortName;
    @NonNull
    String description;
    boolean isRequired;
    boolean isList;
    Object defaultValue;
    @NonNull
    Class<?> type;

    public static class OptionBuilder {} // javadoc generation workaround

    boolean isFlag() {
        return type.equals(Boolean.class);
    }
}
