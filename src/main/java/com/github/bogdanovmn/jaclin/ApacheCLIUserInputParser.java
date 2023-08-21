package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class ApacheCLIUserInputParser implements CLIUserInputParser {
    private final List<Option> definedOptions;

    @Override
    public ParsedOptions parsedOptions(String... args) {
        return null;
    }
}
