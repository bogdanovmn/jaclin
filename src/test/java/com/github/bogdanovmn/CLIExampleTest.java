package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import static com.github.bogdanovmn.CLIExampleTest.MyEnum.FOO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CLIExampleTest {
    enum MyEnum { FOO, BAR, BAZ }

    @Test
    public void shouldHandleOptions() {
        new CLI("app-name", "description")
            .withRequiredOptions()
                .strArg("a-opt", "description")
                    .hasDefault("def")
                    .hasShortName("a")
                    .requires("e-opt")
                .intArg("x-opt", "description")
            .withOptions()
                .intArg("b-opt", "description").asList()
                .flag("y-opt", "description")
                .enumArg("e-opt", "description", MyEnum.class)
	        .withRestrictions()
                .mutualExclusions("b-opt", "y-opt")
                .atLeastOneShouldBeUsed("e-opt", "x-opt")
            .withEntryPoint(
                options -> {
                    assertEquals(123, (long) options.getInt("x-opt"));
                    assertEquals("a1", options.get("a-opt"));
                    assertEquals(FOO, options.getEnum("e-opt"));
                    assertTrue(options.enabled("y-opt"));
                    assertFalse(options.enabled("y-opt-unknown"));
                }
            )
            .run("-a", "a1", "-x", "123", "-e", "FOO", "-y");
    }
}