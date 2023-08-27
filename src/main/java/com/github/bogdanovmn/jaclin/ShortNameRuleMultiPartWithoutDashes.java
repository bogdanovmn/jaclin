package com.github.bogdanovmn.jaclin;

import java.util.Optional;

class ShortNameRuleMultiPartWithoutDashes implements ShortNameRule {
    @Override
    public Optional<String> shortName(OptionName name) {
        return Optional.of(
            String.join("", name.parts())
        );
    }
}
