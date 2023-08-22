package com.github.bogdanovmn.jaclin;

interface CLIUserInputParser {
    ParsedOptions parsedOptions(String... args) throws ArgumentsParsingException;
}
