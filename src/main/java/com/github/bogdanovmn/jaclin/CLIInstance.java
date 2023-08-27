package com.github.bogdanovmn.jaclin;

import lombok.Builder;

import java.util.List;

@Builder
class CLIInstance implements Runner {
    private final List<Option> definedOptions;
    private final OptionsRestrictions optionsRestrictions;
    private final CLI.EntryPoint task;
    private final UsageRender usageRender;

    @Override
    public void run(String... args) throws Exception {
        ParsedOptions runtimeOptions = new ApacheCLIUserInputParser(definedOptions).parsedOptions(args);
        optionsRestrictions.validate(definedOptions, runtimeOptions);
        if (runtimeOptions.has("help")) {
            usageRender.print();
        } else {
            task.execute(runtimeOptions);
        }
    }
}
