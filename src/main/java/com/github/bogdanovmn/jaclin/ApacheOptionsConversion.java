package com.github.bogdanovmn.jaclin;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.Options;

import java.util.List;

import static org.apache.commons.cli.Option.UNINITIALIZED;
import static org.apache.commons.cli.Option.UNLIMITED_VALUES;

@RequiredArgsConstructor
class ApacheOptionsConversion {
    private final List<Option> originalOptions;

    Options apacheOptions() {
        Options apacheOptions = new Options();
        originalOptions.stream()
            .map(
                oo -> org.apache.commons.cli.Option.builder()
                    .longOpt(oo.getName())
                    .option(oo.getShortName())
                    .desc(oo.getDescription())
                    .required(oo.isRequired())
                    .numberOfArgs(
                        oo.isFlag()
                            ? UNINITIALIZED
                            : oo.isList()
                                ? UNLIMITED_VALUES
                                : 1
                    )
                    .argName(
                        argRenderName(oo.getType())
                    )
                .build()
            )
            .forEach(apacheOptions::addOption);
        return apacheOptions;
    }

    private String argRenderName(Class<?> type) {
        String result = "STR";
        if (type.isEnum()) {
            result = "ENUM";
        } else if (Integer.class.equals(type)) {
            result = "INT";
        } else if (Boolean.class.equals(type)) {
            result = null;
        }
        return result;
    }
}
