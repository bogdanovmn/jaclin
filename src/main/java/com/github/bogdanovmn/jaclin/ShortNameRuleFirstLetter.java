package com.github.bogdanovmn.jaclin;

import java.util.Optional;

class ShortNameRuleFirstLetter implements ShortNameRule {
    @Override
    public Optional<String> shortName(OptionName name) {
        return Optional.of(
            String.valueOf(
                name.value().charAt(0)
            )
        );
    }
}
