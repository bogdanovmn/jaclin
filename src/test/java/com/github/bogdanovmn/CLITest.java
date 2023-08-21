package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

public class CLITest {
    enum MyEnum { FOO, BAR, BAZ }

    @Test
    public void shouldHandleOptions() throws Exception {
        new CLI("app-name", "description")
            .withRequiredOptions() // OptionsBuilder
                .strArg("a-opt", "description") // all builders
                    .hasDefault("def")
                    .hasShortName("a")
                    .requires("e-opt")
                .intArg("b-opt", "description")
			        .asList()
            .withOptions() // OptionsBuilder
                .strArg("x-opt", "description")
                .flag("y-opt", "description")
                .enumArg("e-opt", "description", MyEnum.class)
	        .withRestrictions() // restrictions builder
                .mutualExclusions("x-opt", "y-opt") // CLI builder
                .atLeastOneShouldBeUsed("e-opt", "x-opt")
            .withEntryPoint(
                        options -> {
//                            int = options.getInt("b-opt"); // first entry
//                            List<Integer> b = options.getIntList("b-opt"); // :-\
//                            String a = options.get("a-opt");
//                            boolean y = options.enabled("y-opt");
//                            MyEnum e = (MyEnum) options.getEnum("e-opt");
                        }
                    ) // Runner
            .run("-h");
    }
}