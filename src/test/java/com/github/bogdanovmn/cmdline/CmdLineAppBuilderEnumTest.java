package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static com.github.bogdanovmn.cmdline.CmdLineAppBuilderEnumTest.EnumExample.BAR;
import static com.github.bogdanovmn.cmdline.CmdLineAppBuilderEnumTest.EnumExample.BAZ;
import static com.github.bogdanovmn.cmdline.CmdLineAppBuilderEnumTest.EnumExample.FOO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderEnumTest {
    enum EnumExample { FOO, BAR, BAZ }

    @Test
    public void shouldHandleEnumOption() throws Exception {
        new CmdLineAppBuilder(new String[]{"-e", "FOO"})
            .withEnumArg("enum", "enum value", EnumExample.class)
            .withEnumArg("enum2", "enum2 value", EnumExample.class).withDefault(BAR)
            .withEntryPoint(options -> {
                assertEquals(FOO, options.getEnum("enum"));
                assertEquals("FOO", options.getEnumAsRawString("enum"));
                assertEquals(BAR, options.getEnum("enum2"));
                assertEquals("BAR", options.getEnumAsRawString("enum2"));
            })
        .build().run();
    }

    @Test
    public void shouldCreateDescriptionForEnumOption() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        new CmdLineAppBuilder(new String[]{"-e", "foo", "-h"})
            .withEnumArg("enum", "enum value", EnumExample.class).withDefault(BAZ)
            .withEntryPoint(options -> {
                assertEquals("foo", options.get("enum"));
            })
        .build().run();

        String[] lines = outContent.toString().split("\n");

        System.setOut(originalOut);
        Arrays.stream(lines).forEach(System.out::println);
        assertEquals("output lines count", 5, lines.length);
        assertTrue("possible values description", lines[2].contains("Possible values: FOO | BAR | BAZ"));
        assertTrue("possible values description", lines[3].contains("Default: BAZ"));
    }
}