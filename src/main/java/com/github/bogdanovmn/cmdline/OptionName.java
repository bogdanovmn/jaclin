package com.github.bogdanovmn.cmdline;

import java.util.Arrays;
import java.util.List;

class OptionName {
    private final String value;

    private static final String PARTS_SEPARATOR_PATTERN = "[-_]";

    OptionName(String value) {
        this.value = value;
    }

    boolean isMultiPart() {
        return value.matches(
            String.format(".*%s.*", PARTS_SEPARATOR_PATTERN)
        );
    }

    String value() {
        return value;
    }

    List<String> parts() {
        return Arrays.asList(
            value.split(PARTS_SEPARATOR_PATTERN)
        );
    }

    char[] consonants() {
        return value.replaceAll("[eyuioa]", "").toCharArray();
    }
}
