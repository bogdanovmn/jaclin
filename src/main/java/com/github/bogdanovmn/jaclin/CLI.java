package com.github.bogdanovmn.jaclin;

import java.util.List;
import java.util.function.Consumer;

public class CLI implements CLIBuilder {
    private final String executableName;
    private final String appDescription;

    private final OptionsBuilder optionsBuilder;
    private final RestrictionsBuilder restrictionsBuilder;

    public CLI(String executableName, String appDescription) {
        this.executableName = executableName;
        this.appDescription = appDescription;
        this.restrictionsBuilder = new RestrictionsBuilder(this);
        this.optionsBuilder = new OptionsBuilder(this, restrictionsBuilder);
    }

    @Override
    public OptionsStarterBuilder withRequiredOptions() {
        return optionsBuilder.required();
    }

    @Override
    public OptionsStarterBuilder withOptions() {
        return optionsBuilder.notRequired();
    }

    @Override
    public RestrictionsStarterBuilder withRestrictions() {
        return restrictionsBuilder;
    }

    @Override
    public Runner withEntryPoint(Consumer<ParsedOptions> task) {
        List<Option> definedOptions = optionsBuilder.build();
        return CLIInstance.builder()
            .definedOptions(definedOptions)
            .optionsRestrictions(restrictionsBuilder.build())
            .task(task)
            .usageRender(
                new ApacheUsageRender(definedOptions, executableName, appDescription)
            )
        .build();
    }

}
