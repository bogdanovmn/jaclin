package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLITest {
    enum MyEnum { FOO, BAR, BAZ }

    @Test
    public void shouldHandleOptions() throws Exception {
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
                .mutualExclusions("x-opt", "y-opt")
                .atLeastOneShouldBeUsed("e-opt", "x-opt")
            .withEntryPoint(
                options -> {
                    assertEquals("a1", options.get("a-opt"));
                    assertEquals(123, (long) options.getInt("x-opt"));
                }
            )
            .run("-a", "a1", "-x", "123");
    }
}