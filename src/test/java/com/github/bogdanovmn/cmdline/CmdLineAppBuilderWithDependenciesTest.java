package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderWithDependenciesTest {
    @Test
    public void shouldHandleAllDependencies() throws Exception {
        new CmdLineAppBuilder(new String[]{"-i", "123", "-b", "-s", "str"})
            .withArg("integer-opt", "integer option description")
            .withArg("string-opt", "string option description")
            .withFlag("bool-flag", "bool-flag description")
            .withDependencies("bool-flag", "integer-opt", "string-opt")
            .withEntryPoint(cmdLine -> {
                assertEquals("123", cmdLine.getOptionValue("i"));
                assertEquals("str", cmdLine.getOptionValue("s"));
                assertTrue(cmdLine.hasOption("b"));
            })
            .build().run();
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleRequiredOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-i", "123", "-b"})
                .withArg("integer-opt", "integer option description")
                .withArg("string-opt", "string option description")
                .withFlag("bool-flag", "bool-flag description")
                .withDependencies("bool-flag", "integer-opt", "string-opt")
                .withEntryPoint(cmdLine -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("With 'bool-flag' option you must also specify these: [string-opt]", ex.getMessage());
            throw ex;
        }
    }
}