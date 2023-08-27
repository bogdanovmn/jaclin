package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import com.github.bogdanovmn.test.SystemOutputCapture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLIUsageOutputTest {
    enum EnumExample { FOOOOOOOOOOOO, BAAAAAAAAAAR, BAZZZZZZZZ }

    @Test
    public void shouldCreateDescriptionForEnumOption() {
        String[] lines = new SystemOutputCapture(
            () -> {
                try {
                    new CLI("app-name", "description")
                        .withOptions()
                            .enumArg("enum", "enum value loooooonnnngggggg loooooonnnngggggg loooooonnnngggggg string", EnumExample.class)
                        .withEntryPoint(options -> {})
                    .run("-h");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        ).outputOfExecution();

        assertEquals("output lines count", 5, lines.length);
        assertTrue("possible values description", lines[3].contains("Possible values"));
        assertTrue("help description", lines[4].contains("show this message"));
    }

    @Test
    public void shouldNotHaveConflictWithHelpOptionName() {
        String[] lines = new SystemOutputCapture(
            () -> {
                try {
                    new CLI("app-name", "description")
                        .withOptions()
                            .flag("home", "home opt descr")
                        .withEntryPoint(options -> {
                            assertEquals("foo", options.get("enum"));
                        })
                    .run("-h");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        ).outputOfExecution();

        assertEquals("output lines count", 4, lines.length);
        assertTrue("home option description", lines[2].contains("-he,--home"));
        assertTrue("help option description", lines[3].contains("-h,--help"));
    }

    @Test
    public void shouldPrintOptionsInNaturalOrder() {
        String[] lines = new SystemOutputCapture(
            () -> {
                try {
                    new CLI("app-name", "description")
                        .withOptions()
                            .strArg("zoo", "blabla")
                            .strArg("bar", "roflmao")
                            .strArg("foo", "yadayada")
                        .withEntryPoint(options -> {})
                    .run("-h");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        ).outputOfExecution();

        assertEquals("output lines count", 6, lines.length);
        assertTrue("zoo position is 1", lines[2].contains("--zoo"));
        assertTrue("bar position is 2", lines[3].contains("--bar"));
        assertTrue("foo position is 3", lines[4].contains("--foo"));
        assertTrue("help position is 4", lines[5].contains("--help"));
    }

    @Test
    public void shouldPrintUsageEvenRequiredArgNotSpecified() {
        String[] lines = new SystemOutputCapture(
            () -> {
                try {
                    new CLI("app-name", "description")
                        .withRequiredOptions()
                            .strArg("zoo", "blabla")
                        .withEntryPoint(options -> {})
                    .run("-h");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        ).outputOfExecution();

        assertEquals("output lines count", 4, lines.length);
        assertTrue("zoo position is 1", lines[2].contains("--zoo"));
        assertTrue("help position is 4", lines[3].contains("--help"));
    }
}