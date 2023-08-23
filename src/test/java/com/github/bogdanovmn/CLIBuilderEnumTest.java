package com.github.bogdanovmn;

import com.github.bogdanovmn.test.SystemOutputCapture;
import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLIBuilderEnumTest {
    enum EnumExample { FOO, BAR, BAZ }

    @Test
    public void shouldHandleEnumOption() {
        new CLI("app-name", "description")
            .withOptions()
            .enumArg("enum", "enum value", EnumExample.class)
            .enumArg("enum2", "enum2 value", EnumExample.class)
                .hasDefault(EnumExample.BAR)
            .withEntryPoint(options -> {
                assertEquals(EnumExample.FOO, options.getEnum("enum"));
                assertEquals("FOO", options.getEnumAsRawString("enum"));
                assertEquals(EnumExample.BAR, options.getEnum("enum2"));
                assertEquals("BAR", options.getEnumAsRawString("enum2"));
            })
        .run("-e", "FOO");
    }

    @Test
    public void shouldHandleEnumOptionInDifferentCase() {
        new CLI("app-name", "description")
            .withOptions()
                .enumArg("enum", "enum value", EnumExample.class)
            .withEntryPoint(options -> {
                assertEquals(EnumExample.FOO, options.getEnum("enum"));
                assertEquals("foo", options.getEnumAsRawString("enum"));
            })
            .run("-e", "foo");
    }

    @Test
    public void shouldCreateDescriptionForEnumOption() {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CLI("app-name", "description")
                    .withOptions()
                        .enumArg("enum", "enum value", EnumExample.class)
                            .hasDefault(EnumExample.BAZ)
                    .withEntryPoint(options -> {
                        assertEquals("foo", options.get("enum"));
                    })
                .run("-e", "foo", "-h");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 6, lines.length);
        assertTrue("possible values description", lines[3].contains("Possible values: FOO | BAR | BAZ"));
        assertTrue("possible values description", lines[4].contains("Default: BAZ"));
    }
}