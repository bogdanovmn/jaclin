package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ApacheCLIUserInputParser implements CLIUserInputParser {
    private final List<Option> definedOptions;

    @Override
    public ParsedOptions parsedOptions(String... args) throws ArgumentsParsingException {
        try {
            CommandLine cmdLine = new DefaultParser().parse(
                new ApacheOptionsConversion(definedOptions).apacheOptions(),
                args
            );
            return new ParsedOptions(
                definedOptions.stream()
                    .filter(opt -> cmdLine.hasOption(opt.getName()) || opt.getDefaultValue() != null)
                    .map(opt -> new RuntimeOption(opt, cmdLine.getOptionValues(opt.getName())))
                    .collect(
                        Collectors.toMap(RuntimeOption::name, Function.identity())
                    ),
                definedOptions.stream().map(Option::getName).collect(Collectors.toSet())
            );
        } catch (ParseException e) {
            throw new ArgumentsParsingException(e.getMessage());
        }
    }
}
