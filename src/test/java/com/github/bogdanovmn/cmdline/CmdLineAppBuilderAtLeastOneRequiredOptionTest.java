package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CmdLineAppBuilderAtLeastOneRequiredOptionTest {
    @Test(expected = RuntimeException.class)
    public void shouldRaiseAnExceptionOnAbsenceOfAtLeastOneRequiredOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-x"})
                .withArg("integer-opt", "some integer arg")
                .withArg("string-opt", "some string arg")
                .withFlag("xxx-opt", "xxx!")
                .withAtLeastOneRequiredOption("integer-opt", "string-opt")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("You should use at least one of these options: 'integer-opt', 'string-opt'", ex.getMessage());
            throw ex;
        }
    }

    @Test
    public void shouldNotRaiseAnExceptionIfAtLeastOneRequiredOptionIsDefined() throws Exception {
        new CmdLineAppBuilder(new String[]{"-x", "-s", "s value"})
            .withArg("integer-opt", "some integer arg")
            .withArg("string-opt", "some string arg")
            .withFlag("xxx-opt", "xxx!")
            .withAtLeastOneRequiredOption("integer-opt", "string-opt")
            .withEntryPoint(options -> {
                assertEquals("s value", options.get("string-opt"));
            })
        .build().run();
    }

    @Test(expected = RuntimeException.class)
    public void shouldRaiseAnExceptionOnUnknownAtLeastOneRequiredOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-m", "2", "-s", "3"})
                .withJarName("my-jar-name")
                .withDescription("My program does ...")

                .withArg("some-option", "...description of the option...")

                .withFlag("flag", "...description of the option...")

                .withCustomOption(
                    Option.builder()
                        .longOpt("custom-option")
                        .desc("...description of the option...")
                    .build()
                )

                .withRequiredArg("mandatory-option", "...description of the option...")

                .withAtLeastOneRequiredOption("some-option", "flag1")

                .withEntryPoint(options -> {})
                .build().run();
        } catch (RuntimeException ex) {
            assertEquals("Unknown option: [flag1]", ex.getMessage());
            throw ex;
        }
    }
}