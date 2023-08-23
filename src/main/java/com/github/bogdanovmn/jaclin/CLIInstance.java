package com.github.bogdanovmn.jaclin;

import lombok.Builder;

import java.util.List;
import java.util.function.Consumer;

@Builder
class CLIInstance implements Runner {
    private final List<Option> definedOptions;
    private final OptionsRestrictions optionsRestrictions;
    private final Consumer<ParsedOptions> task;
    private final UsageRender usageRender;

    @Override
    public void run(String... args) {
        ParsedOptions runtimeOptions;
        try {
            runtimeOptions = new ApacheCLIUserInputParser(definedOptions).parsedOptions(args);
            optionsRestrictions.validate(definedOptions, runtimeOptions);
            if (runtimeOptions.has("help")) {
                usageRender.print();
            } else {
                task.accept(runtimeOptions);
            }
        } catch (ArgumentsParsingException ex) {
//            System.err.printf("CLI arguments parsing error: %s%n", ex.getMessage());
//            usageRender.print();
            throw new RuntimeException(ex.getMessage());
        }
    }
}
