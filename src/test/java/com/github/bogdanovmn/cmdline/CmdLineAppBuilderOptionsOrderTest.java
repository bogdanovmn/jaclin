package com.github.bogdanovmn.cmdline;

import com.github.bogdanovmn.cmdline.test.SystemOutputCapture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderOptionsOrderTest {
    @Test
    public void shouldPrintOptionsInNaturalOrder() throws Exception {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CmdLineAppBuilder(new String[]{"-h"})
                    .withArg("zoo", "blabla")
                    .withArg("bar", "roflmao")
                    .withArg("foo", "yadayada")
                    .withEntryPoint(options -> {})
                .build().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 5, lines.length);
        assertTrue("zoo position is 1", lines[1].contains("--zoo"));
        assertTrue("bar position is 2", lines[2].contains("--bar"));
        assertTrue("foo position is 3", lines[3].contains("--foo"));
        assertTrue("help position is 4", lines[4].contains("--help"));

    }
}