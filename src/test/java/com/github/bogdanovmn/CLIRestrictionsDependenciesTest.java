package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLIRestrictionsDependenciesTest {
    @Test
    public void shouldHandleAllDependencies() throws Exception {
        new CLI("app-name", "description")
            .withOptions()
            .strArg("integer-opt", "integer option description")
            .strArg("string-opt", "string option description")
            .flag("bool-flag", "bool-flag description")
                .requires("integer-opt", "string-opt")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("integer-opt"));
                assertEquals("str", options.get("string-opt"));
                assertTrue(options.enabled("bool-flag"));
            })
        .run("-i", "123", "-b", "-s", "str");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleRequiredOption() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("integer-opt", "integer option description")
                    .strArg("string-opt", "string option description")
                    .flag("bool-flag", "bool-flag description")
                        .requires("integer-opt", "string-opt")
                .withEntryPoint(options -> {})
            .run("-i", "123", "-b");
        } catch (IllegalStateException ex) {
            assertEquals("With 'bool-flag' option you must also specify these: [string-opt]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnUnknownOption() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("integer-opt", "integer option description")
                    .strArg("string-opt", "string option description")
                    .flag("bool-flag", "bool-flag description")
                        .requires("integer-opt", "unknown-opt")
                .withEntryPoint(options -> {})
            .run("-i", "123", "-b");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Unknown options in dependencies configuration: [unknown-opt]",
                ex.getMessage()
            );
            throw ex;
        }
    }
}