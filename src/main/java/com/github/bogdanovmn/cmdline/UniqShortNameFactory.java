package com.github.bogdanovmn.cmdline;

import java.util.*;

class UniqShortNameFactory {
	private final Set<String> alreadyProduced = new HashSet<>();
	private final List<ShortNameRule> rulesForSimpleNames = Arrays.asList(
		new ShortNameRuleFirstLetter(),
		new ShortNameRuleFirstAndLastLetters(),
		new ShortNameRule3ConsonantLetters()
	);
	private final List<ShortNameRule> rulesForMultiPartNames = Arrays.asList(
		new ShortNameRuleFirstLetter(),
		new ShortNameRuleFirstLetterMultiPart()
	);

	String shortName(String originName) {
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
				if (!alreadyProduced.contains(result)) {
					alreadyProduced.add(result);
					break;
				}
				else {
					result = null;
				}
			}
		}

		if (result == null) {
			if (alreadyProduced.contains(originName)) {
				throw new IllegalStateException(
					String.format("Short name for %s is already produced. All short names: %s", originName, alreadyProduced)
				);
			}
			else {
				alreadyProduced.add(originName);
				result = originName;
			}
		}

		return result;
	}

	void add(String shortName) {
		if (!alreadyProduced.add(shortName)) {
			throw new IllegalStateException(
				String.format("Option with short name '%s' has been already defined", shortName)
			);
		}
	}
}
