package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.ArgumentsParsingException;
import com.github.bogdanovmn.jaclin.CLI;
import com.github.bogdanovmn.test.SystemOutputCapture;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CLIBuilderTest {
    @Test
    public void shouldHandleOptions() throws Exception {
        new CLI("app", "app description")
            .withOptions()
                .strArg("integer-opt", "source arg description")
                .intArg("integer-opt2", "source arg description").withShortName("y")
                .flag("bool-flag", "bool-flag description")
                .flag("bool-flag2", "bool-flag2 description").hasShortName("z")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("integer-opt"));
                assertEquals(777, (long) options.getInt("integer-opt2"));
                assertTrue(options.enabled("bool-flag"));
                assertTrue(options.enabled("bool-flag2"));
            })
            .run("-i", "123", "-b", "-z", "-y", "777");
    }

    @Test
    public void shouldHandleDefaults() throws Exception {
        new CLI("app", "app description")
            .withOptions()
                .strArg("str-opt", "source arg description").withDefault("123")
                .intArg("int-opt", "source arg description").withDefault(777)
                .intArg("null-opt", "source arg description")
                .flag("bool-flag", "bool-flag description")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("str-opt"));
                assertEquals(777, (long) options.getInt("int-opt"));
                assertNull(options.get("null-opt"));
                assertFalse(options.enabled("bool-flag"));
            })
        .run();
    }

    @Test
    public void shouldHandleDefaultsDescription() {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CLI("app", "app description")
                    .withOptions()
                        .strArg("str-opt", "source arg description")
                            .withDefault("123")
                        .intArg("int-opt", "source arg description")
                            .withShortName("y")
                            .withDefault(777)
                        .flag("bool-flag", "bool-flag description")
                    .withEntryPoint(options -> {})
                .run("-h");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 8, lines.length);
        assertTrue("default-1 description", lines[3].contains("Default: 123"));
        assertTrue("default-2 description", lines[5].contains("Default: 777"));
        assertTrue("help description", lines[7].contains("show this message"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleDuplicatedShortOption() throws Exception {
        try {
            new CLI("app", "app description")
                .withOptions()
                    .strArg("integer-opt", "source arg description")
                    .strArg("integer-opt2", "source 2 arg description").withShortName("i")
                .withEntryPoint(options -> {})
            .run("-b");
        } catch (RuntimeException ex) {
            assertEquals("Option with short name 'i' has been already defined", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleDuplicatedOption() throws Exception {
        try {
            new CLI("app", "app description")
                .withOptions()
                    .strArg("integer-opt", "source arg description")
                    .strArg("integer-opt", "source 2 arg description")
                .withEntryPoint(options -> {})
            .run("--integer-opt", "123");
        } catch (RuntimeException ex) {
            assertEquals("Option 'integer-opt' is already defined", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = ArgumentsParsingException.class)
    public void shouldHandleRequiredOption() throws Exception {
        try {
            new CLI("app", "app description")
                .withRequiredOptions()
                    .strArg("integer-opt", "source arg description")
                .withOptions()
                    .flag("bool-flag", "bool-flag description")
                .withEntryPoint(options -> {})
            .run("-b");
        } catch (ArgumentsParsingException ex) {
            assertEquals("Missing required option: i", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOptionDifferentType() throws Exception {
        try {
            new CLI("app", "app description")
                .withOptions()
                    .flag("integer-opt", "source arg description")
                .withEntryPoint(options -> options.getInt("integer-opt"))
            .run("-i");
        } catch (IllegalArgumentException ex) {
            assertEquals("Argument integer-opt is not Integer but Boolean", ex.getMessage());
            throw ex;
        }
    }

    @Test
    public void shouldHandleOptionWithMultiValue() throws Exception {
        new CLI("app", "app description")
            .withOptions()
                .strArg("integer-opt", "source arg description")
                    .asList()
                .flag("bool-flag", "bool-flag description")
            .withEntryPoint(options -> {
                assertEquals(3, options.getList("integer-opt").size());
                assertEquals(
                    Arrays.asList("123", "456", "789"),
                    options.getList("integer-opt")
                );
                assertTrue(options.enabled("bool-flag"));
            })
        .run("-i", "123", "456", "789", "-b");
    }
}