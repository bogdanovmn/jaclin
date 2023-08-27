package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
class RuntimeOption {
    private final Option optionDefinition;
    private final String[] rawValues;

    String name() {
        return optionDefinition.getName();
    }

    String asString() {
        return Optional.ofNullable(firstRawValue())
            .orElseGet(
                () -> String.valueOf(optionDefinition.getDefaultValue())
            );
    }

    <T> T value(Class<T> expectedType) {
        checkType(expectedType);
        return value(firstRawValue(), expectedType);
    }

    <T> List<T> values(Class<T> expectedType) {
        checkType(expectedType);
        if (rawValues == null) {
            return optionDefinition.getDefaultValue() == null
                ? Collections.emptyList()
                : Collections.singletonList(
                    (T) optionDefinition.getDefaultValue()
                );
        } else {
            List<T> result = new ArrayList<>(rawValues.length);
            for (String rawValue : rawValues) {
                result.add(
                    value(rawValue, expectedType)
                );
            }
            return result;
        }
    }

    private <T> T value(String rawValue, Class<T> expectedType) {
        Object value = rawValue;
        if (value == null) {
            value = optionDefinition.getDefaultValue();
        } else {
            if (optionDefinition.getType().equals(Integer.class)) {
                value = Integer.parseInt(rawValue);
            } else if (optionDefinition.getType().isEnum()) {
                value = parseEnum(rawValue);
            }
        }
        return (T) value;
    }

    void checkType(Class<?> expectedType) {
        if (
            Enum.class.equals(expectedType) && !optionDefinition.getType().isEnum()
            ||
            !Enum.class.equals(expectedType) && !optionDefinition.getType().equals(expectedType)
        ) {
            throw new IllegalArgumentException(
                String.format(
                    "Argument %s is not %s but %s",
                        name(),
                        expectedType.getSimpleName(),
                        optionDefinition.getType().getSimpleName()
                )
            );
        }
    }

    private Enum<?> parseEnum(String rawValue) {
        try {
            return Enum.valueOf((Class<? extends Enum>) optionDefinition.getType(), rawValue);
        } catch (IllegalArgumentException ex) {
            return Enum.valueOf(
                (Class<? extends Enum>) optionDefinition.getType(),
                rawValue.toUpperCase()
            );
        }
    }

    private String firstRawValue() {
        return rawValues != null && rawValues.length > 0
            ? rawValues[0]
            : null;
    }
}
