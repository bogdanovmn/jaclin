package com.github.bogdanovmn.jaclin;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class Option {
    String name;
    String shortName;
    String description;
    boolean isRequired;
    boolean isList;
    Object defaultValue;
    Class<?> type;
}
