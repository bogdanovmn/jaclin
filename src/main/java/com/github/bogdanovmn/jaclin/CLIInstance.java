package com.github.bogdanovmn.jaclin;

import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
class CLIInstance implements Runner {
    private final List<Option> definedOptions;
    private final OptionsRestrictions optionsRestrictions;
    private final CLI.EntryPoint task;
    private final UsageRender usageRender;

    @Override
    public void run(String... args) throws Exception {
        if (usageFlagSpecified(args)) {
            usageRender.print();
            return;
        }
        try {
            ParsedOptions runtimeOptions = new ApacheCLIUserInputParser(definedOptions).parsedOptions(args);
            if (runtimeOptions.has("help")) {
                usageRender.print();
            } else {
                optionsRestrictions.validate(definedOptions, runtimeOptions);
                task.execute(runtimeOptions);
            }
        } catch (ArgumentsParsingException ex) {
            usageRender.print();
            System.err.printf("ERROR! %s", ex.getMessage());
        }
    }

    private boolean usageFlagSpecified(String... args) {
        return Arrays.stream(args).anyMatch(a -> "-h".equals(a) || "--help".equals(a));
    }
}
