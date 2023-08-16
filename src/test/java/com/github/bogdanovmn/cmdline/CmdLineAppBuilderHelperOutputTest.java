package com.github.bogdanovmn.cmdline;

import com.github.bogdanovmn.cmdline.test.SystemOutputCapture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderHelperOutputTest {
    enum EnumExample { FOOOOOOOOOOOO, BAAAAAAAAAAR, BAZZZZZZZZ }

    @Test
    public void shouldCreateDescriptionForEnumOption() {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CmdLineAppBuilder(new String[]{"-h"})
                    .withEnumArg("enum", "enum value loooooonnnngggggg loooooonnnngggggg loooooonnnngggggg string", EnumExample.class)
                    .withEntryPoint(options -> {})
                .build().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 4, lines.length);
        assertTrue("possible values description", lines[2].contains("Possible values"));
        assertTrue("help description", lines[3].contains("show this message"));
    }

    @Test
    public void shouldNotHaveConflictWithHelpOptionName() {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CmdLineAppBuilder(new String[]{"-h"})
                    .withFlag("home", "home opt descr")
                    .withEntryPoint(options -> {
                        assertEquals("foo", options.get("enum"));
                    })
                    .build().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 3, lines.length);
        assertTrue("home option description", lines[1].contains("-he,--home"));
        assertTrue("help option description", lines[2].contains("-h,--help"));

    }
}