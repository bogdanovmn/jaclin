package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static com.github.bogdanovmn.cmdline.CmdLineAppBuilderEnumTest.EnumExample.FOO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderEnumTest {
    enum EnumExample { FOO, BAR, BAZ }

    @Test
    public void shouldHandleEnumOption() throws Exception {
        new CmdLineAppBuilder(new String[]{"-e", "FOO"})
            .withEnumArg("enum", "enum value", EnumExample.class)
            .withEntryPoint(options -> {
                assertEquals(FOO, options.getEnum("enum"));
                assertEquals("FOO", options.getEnumAsRawString("enum"));
            })
        .build().run();
    }

    @Test
    public void shouldCreateDescriptionForEnumOption() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        new CmdLineAppBuilder(new String[]{"-e", "foo", "-h"})
            .withEnumArg("enum", "enum value", EnumExample.class)
            .withEntryPoint(options -> {
                assertEquals("foo", options.get("enum"));
            })
        .build().run();

        String[] lines = outContent.toString().split("\n");

        System.setOut(originalOut);
        Arrays.stream(lines).forEach(System.out::println);
        assertEquals("output lines count", 4, lines.length);
        assertTrue("possible values description", lines[2].contains("Possible values: FOO | BAR | BAZ"));
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
}