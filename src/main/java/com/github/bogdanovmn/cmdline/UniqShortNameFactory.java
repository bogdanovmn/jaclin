package com.github.bogdanovmn.cmdline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class UniqShortNameFactory {
    private final Map<String, Integer> alreadyProduced = new HashMap<>();

    private final List<ShortNameRule> rulesForSimpleNames = Arrays.asList(
        new ShortNameRuleFirstLetter(),
        new ShortNameRuleFirstAndLastLetters(),
        new ShortNameRule3ConsonantLetters()
    );

    private final List<ShortNameRule> rulesForMultiPartNames = Arrays.asList(
        new ShortNameRuleFirstLetter(),
        new ShortNameRuleMultiPartFirstLetter(),
        new ShortNameRuleMultiPartWithoutDashes(),
        new ShortNameRuleMultiPartNumberSuffix(
            new ShortNameRuleMultiPartFirstLetter()
        )
    );

    String produce(String originName) {
        if (originName == null) {
            throw new IllegalStateException("Option name expected");
        }

        OptionName name = new OptionName(originName);
        String result = null;

        List<ShortNameRule> rules = name.isMultiPart()
            ? rulesForMultiPartNames
            : rulesForSimpleNames;

        for (ShortNameRule rule : rules) {
            Optional<String> shortName = rule.shortName(name);
            if (shortName.isPresent()) {
                result = shortName.get();
                if (!alreadyProduced.containsKey(result)) {
                    alreadyProduced.put(result, alreadyProduced.size() + 1);
                    break;
                } else {
                    result = null;
                }
            }
        }

        if (result == null) {
            if (alreadyProduced.containsKey(originName)) {
                throw new IllegalStateException(
                    String.format("Short name for %s is already produced. All short names: %s", originName, alreadyProduced)
                );
            } else {
                alreadyProduced.put(originName, alreadyProduced.size() + 1);
                result = originName;
            }
        }

        return result;
    }

    void add(String shortName) {
        if (null != alreadyProduced.put(shortName, alreadyProduced.size() + 1)) {
            throw new IllegalStateException(
                String.format("Option with short name '%s' has been already defined", shortName)
            );
        }
    }

    public int orderNumberByShortName(String shortName) {
        return alreadyProduced.getOrDefault(shortName, -1);
    }

    public void remove(String shortName) {
        alreadyProduced.remove(shortName);
    }
}
