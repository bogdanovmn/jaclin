package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderHelperOutputTest {
    enum EnumExample { FOOOOOOOOOOOO, BAAAAAAAAAAR, BAZZZZZZZZ }

    @Test
    public void shouldCreateDescriptionForEnumOption() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        new CmdLineAppBuilder(new String[] {"-h"})
            .withEnumArg("enum", "enum value loooooonnnngggggg loooooonnnngggggg loooooonnnngggggg string", EnumExample.class)
            .withEntryPoint(cmdLine -> {
                assertEquals("foo", cmdLine.getOptionValue("enum"));
            })
            .build().run();

        String[] lines = outContent.toString().split("\n");

        System.setOut(originalOut);
        Arrays.stream(lines).forEach(System.out::println);
        assertEquals("output lines count", 4, lines.length);
        assertTrue("possible values description", lines[2].contains("Possible values"));
        assertTrue("help description", lines[3].contains("show this message"));
    }
}