package com.github.bogdanovmn.jaclin;

import lombok.Builder;
import org.apache.commons.cli.HelpFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

@Builder
class ApacheUsageRender implements UsageRender {
    private final List<Option> options;
    private final String executableName;
    private final String description;

    @Override
    public void print() {
        HelpFormatter helpFormatter = new HelpFormatter();
        Map<String, Integer> optIndex = IntStream.range(0, options.size()).boxed()
            .collect(
                toMap(i -> options.get(i).getName(), Function.identity())
            );
        helpFormatter.setOptionComparator(
            Comparator.comparingInt(
                option -> "h".equals(option.getOpt())
                    ? Integer.MAX_VALUE // --help option should be in the bottom
                    : optIndex.get(option.getLongOpt())
            )
        );
        helpFormatter.setLeftPadding(2);
        helpFormatter.setWidth(120);
        helpFormatter.printHelp(
            String.format("java -jar %s.jar", Optional.ofNullable(executableName).orElse("the")),
            description,
            new ApacheOptionsConversion(options).apacheOptions(),
            "",
            true
        );
    }
}
