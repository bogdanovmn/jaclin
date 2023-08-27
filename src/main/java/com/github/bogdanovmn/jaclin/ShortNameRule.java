package com.github.bogdanovmn.jaclin;

import java.util.Optional;

interface ShortNameRule {
    Optional<String> shortName(OptionName name);
}
