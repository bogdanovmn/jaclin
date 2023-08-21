package com.github.bogdanovmn.jaclin;

import java.util.Optional;

class ShortNameRuleFirstAndLastLetters implements ShortNameRule {
    @Override
    public Optional<String> shortName(OptionName name) {
        return name.value().length() > 2
            ? Optional.of(
                name.value().charAt(0)
                +
                String.valueOf(
                    name.value().charAt(
                        name.value().length() - 1
                    )
                )

            )
            : Optional.empty();
    }
}
