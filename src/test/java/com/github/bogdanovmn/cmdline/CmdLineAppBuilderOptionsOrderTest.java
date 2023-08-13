package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderOptionsOrderTest {
    @Test
    public void shouldPrintOptionsInNaturalOrder() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        new CmdLineAppBuilder(new String[]{"-h"})
            .withArg("zoo", "blabla")
            .withArg("bar", "roflmao")
            .withArg("foo", "yadayada")
            .withEntryPoint(options -> {})
        .build().run();

        String[] lines = outContent.toString().split("\n");

        System.setOut(originalOut);

        assertEquals("output lines count", 5, lines.length);
        assertTrue("zoo position is 1", lines[1].contains("--zoo"));
        assertTrue("bar position is 2", lines[2].contains("--bar"));
        assertTrue("foo position is 3", lines[3].contains("--foo"));
        assertTrue("help position is 4", lines[4].contains("--help"));

    }
}