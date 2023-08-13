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
            .withEntryPoint(options -> {
                assertEquals("123", options.get("integer-opt"));
                assertEquals("str", options.get("string-opt"));
                assertTrue(options.getBool("bool-flag"));
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
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("With 'bool-flag' option you must also specify these: [string-opt]", ex.getMessage());
            throw ex;
        }
    }
}