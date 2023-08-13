package com.github.bogdanovmn.cmdline;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ShortNameRuleMultiPartFirstLetter implements ShortNameRule {
    @Override
    public Optional<String> shortName(OptionName name) {
        List<String> parts = name.parts();
        for (String part : parts) {
            if (part.length() == 0) {
                return Optional.empty();
            }
        }

        return Optional.of(
            parts.stream()
                .map(namePart -> String.valueOf(namePart.charAt(0)))
                .collect(Collectors.joining(""))
        );
    }
}
