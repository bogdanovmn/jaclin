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
        ParsedOptions runtimeOptions = new ApacheCLIUserInputParser(definedOptions).parsedOptions(args);
        optionsRestrictions.validate(definedOptions, runtimeOptions);
        task.accept(runtimeOptions);
    }
}
