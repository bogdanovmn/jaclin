package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLIRestrictionsAtLeastOneShouldBeUsedTest {
    @Test(expected = IllegalStateException.class)
    public void shouldRaiseAnExceptionOnAbsenceOfAtLeastOneRequiredOption() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("integer-opt", "some integer arg")
                    .strArg("string-opt", "some string arg")
                    .flag("xxx-opt", "xxx!")
                .withRestrictions()
                    .atLeastOneShouldBeUsed("integer-opt", "string-opt")
                .withEntryPoint(options -> {})
            .run("-x");
        } catch (IllegalStateException ex) {
            assertEquals("You should use at least one of these options: 'integer-opt', 'string-opt'", ex.getMessage());
            throw ex;
        }
    }

    @Test
    public void shouldNotRaiseAnExceptionIfAtLeastOneRequiredOptionIsDefined() throws Exception {
        new CLI("app-name", "description")
            .withOptions()
                .strArg("integer-opt", "some integer arg")
                .strArg("string-opt", "some string arg")
                .flag("xxx-opt", "xxx!")
            .withRestrictions()
                .atLeastOneShouldBeUsed("integer-opt", "string-opt")
            .withEntryPoint(options -> {
                assertEquals("s value", options.get("string-opt"));
            })
        .run("-x", "-s", "s value");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRaiseAnExceptionOnUnknownAtLeastOneRequiredOption() throws Exception {
        try {
            new CLI("app-name", "description")
                .withRequiredOptions()
                    .strArg("mandatory-option", "...description of the option...")
                .withOptions()
                    .strArg("some-option", "...description of the option...")
                    .flag("flag", "...description of the option...")
                .withRestrictions()
                    .atLeastOneShouldBeUsed("some-option", "flag1")
                .withEntryPoint(options -> {})
            .run("-m", "2", "-s", "3");
        } catch (IllegalStateException ex) {
            assertEquals("Unknown option: [flag1]", ex.getMessage());
            throw ex;
        }
    }
}