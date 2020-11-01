package com.github.bogdanovmn.cmdline;

import java.util.Optional;

interface ShortNameRule {
	Optional<String> shortName(OptionName name);
}
