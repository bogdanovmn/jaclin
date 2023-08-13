package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderTest {
    @Test
    public void shouldHandleOptions() throws Exception {
        new CmdLineAppBuilder(new String[]{"-i", "123", "-b", "-z", "-y", "777"})
            .withArg("integer-opt", "source arg description")
            .withIntArg("integer-opt2", "y", "source arg description")
            .withFlag("bool-flag", "bool-flag description")
            .withFlag("bool-flag2", "z", "bool-flag2 description")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("integer-opt"));
                assertEquals(777, (long) options.getInt("integer-opt2"));
                assertTrue(options.getBool("bool-flag"));
                assertTrue(options.getBool("bool-flag2"));
            })
        .build().run();
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleDuplicatedShortOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-b"})
                .withArg("integer-opt", "source arg description")
                .withArg("integer-opt", "i", "source 2 arg description")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("Option with short name 'i' has been already defined", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleRequiredOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-b"})
                .withRequiredArg("integer-opt", "source arg description")
                .withFlag("bool-flag", "bool-flag description")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("Missing required option: i", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOptionDifferentType() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-i"})
                .withFlag("integer-opt", "source arg description")
                .withEntryPoint(options -> options.getInt("integer-opt"))
            .build().run();
        } catch (IllegalArgumentException ex) {
            assertEquals("Argument integer-opt is not Integer but Boolean", ex.getMessage());
            throw ex;
        }
    }

    @Test
    public void shouldHandleOptionWithMultiValue() throws Exception {
        new CmdLineAppBuilder(new String[]{"-i", "123", "456", "789", "-b"})
            .withArg("integer-opt", "source arg description")
            .withFlag("bool-flag", "bool-flag description")
            .withEntryPoint(options -> {
                assertEquals(3, options.getList("integer-opt").size());
                assertEquals(
                    Arrays.asList("123", "456", "789"),
                    options.getList("integer-opt")
                );
                assertTrue(options.getBool("bool-flag"));
            })
        .build().run();
    }
}