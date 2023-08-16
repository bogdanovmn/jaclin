package com.github.bogdanovmn.cmdline;

import com.github.bogdanovmn.cmdline.test.SystemOutputCapture;
import org.junit.Test;

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
    public void shouldHandleEnumOptionInDifferentCase() throws Exception {
        new CmdLineAppBuilder(new String[]{"-e", "foo"})
            .withEnumArg("enum", "enum value", EnumExample.class)
            .withEntryPoint(options -> {
                assertEquals(FOO, options.getEnum("enum"));
                assertEquals("foo", options.getEnumAsRawString("enum"));
            })
            .build().run();
    }

    @Test
    public void shouldCreateDescriptionForEnumOption() {
        String[] lines = new SystemOutputCapture(() -> {
            try {
                new CmdLineAppBuilder(new String[]{"-e", "foo", "-h"})
                    .withEnumArg("enum", "enum value", EnumExample.class).withDefault(BAZ)
                    .withEntryPoint(options -> {
                        assertEquals("foo", options.get("enum"));
                    })
                .build().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).outputOfExecution();

        assertEquals("output lines count", 5, lines.length);
        assertTrue("possible values description", lines[2].contains("Possible values: FOO | BAR | BAZ"));
        assertTrue("possible values description", lines[3].contains("Default: BAZ"));
    }
}