package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderTest {
    @Test
    public void shouldHandleOptions() throws Exception {
        new CmdLineAppBuilder(new String[]{"-i", "123", "-b", "-z", "-y", "777"})
            .withArg("integer-opt", "source arg description")
            .withIntArg("integer-opt2", "source arg description").withShort("y")
            .withFlag("bool-flag", "bool-flag description")
            .withFlag("bool-flag2", "bool-flag2 description").withShort("z")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("integer-opt"));
                assertEquals(777, (long) options.getInt("integer-opt2"));
                assertTrue(options.getBool("bool-flag"));
                assertTrue(options.getBool("bool-flag2"));
            })
        .build().run();
    }

    @Test
    public void shouldHandleDefaults() throws Exception {
        new CmdLineAppBuilder(new String[]{})
            .withArg("str-opt", "source arg description").withDefault("123")
            .withIntArg("int-opt", "source arg description").withShort("y").withDefault(777)
            .withFlag("bool-flag", "bool-flag description")
            .withEntryPoint(options -> {
                assertEquals("123", options.get("str-opt"));
                assertEquals(777, (long) options.getInt("int-opt"));
                assertFalse(options.getBool("bool-flag"));
            })
            .build().run();
    }

    @Test
    public void shouldHandleDefaultsDescription() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        new CmdLineAppBuilder(new String[]{"-h"})
            .withArg("str-opt", "source arg description").withDefault("123")
            .withIntArg("int-opt", "source arg description").withShort("y").withDefault(777)
            .withFlag("bool-flag", "bool-flag description")
            .withEntryPoint(options -> {})
        .build().run();

        String[] lines = outContent.toString().split("\n");

        System.setOut(originalOut);
        Arrays.stream(lines).forEach(System.out::println);
        assertEquals("output lines count", 7, lines.length);
        assertTrue("default-1 description", lines[2].contains("Default: 123"));
        assertTrue("default-2 description", lines[5].contains("Default: 777"));
        assertTrue("help description", lines[6].contains("show this message"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleDuplicatedShortOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-b"})
                .withArg("integer-opt", "source arg description")
                .withArg("integer-opt2", "source 2 arg description").withShort("i")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("Option with short name 'i' has been already defined", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleDuplicatedOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"--integer-opt", "123"})
                .withArg("integer-opt", "source arg description")
                .withArg("integer-opt", "source 2 arg description")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (RuntimeException ex) {
            assertEquals("Option 'integer-opt' already defined", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleRequiredOption() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-b"})
                .withArg("integer-opt", "source arg description").required()
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