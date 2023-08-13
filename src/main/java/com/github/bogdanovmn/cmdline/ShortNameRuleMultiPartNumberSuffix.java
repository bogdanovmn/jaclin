package com.github.bogdanovmn.cmdline;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ShortNameRuleMultiPartNumberSuffix implements ShortNameRule {
    private final Map<String, Integer> lastNumbers = new HashMap<>();
    private final ShortNameRule baseRule;

    ShortNameRuleMultiPartNumberSuffix(ShortNameRule baseRule) {
        this.baseRule = baseRule;
    }

    @Override
    public Optional<String> shortName(OptionName originalName) {
        Optional<String> baseName = baseRule.shortName(originalName);
        if (baseName.isPresent()) {
            String name = baseName.get();
            int lastNumber = lastNumbers.getOrDefault(name, 1);
            lastNumbers.put(name, lastNumber++);
            return Optional.of(name + lastNumber);
        } else {
            return baseName;
        }
    }
}
