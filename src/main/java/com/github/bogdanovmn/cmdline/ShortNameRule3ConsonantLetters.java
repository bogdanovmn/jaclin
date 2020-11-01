package com.github.bogdanovmn.cmdline;

import java.util.Optional;

class ShortNameRule3ConsonantLetters implements ShortNameRule {
	@Override
	public Optional<String> shortName(OptionName name) {
		char[] consonants = name.consonants();

		return consonants.length < 3
			? Optional.empty()
			: Optional.of(
				String.format(
					"%s%s%s",
					consonants[0],
					consonants[1],
					consonants[consonants.length - 1]
				)
			);
	}
}
