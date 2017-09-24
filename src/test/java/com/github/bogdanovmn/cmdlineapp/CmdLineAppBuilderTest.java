package com.github.bogdanovmn.cmdlineapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CmdLineAppBuilderTest {
	@Test
	public void build() {
		new CmdLineAppBuilder(new String[] {"-i", "123", "-b"})
			.withArg("integer", "source arg description")
			.withFlag("bool-flag", "bool-flag description")
			.withEntryPoint(cmdLine -> {
				assertEquals("123", cmdLine.getOptionValue("i"));
				assertEquals("true", cmdLine.getOptionValue("b"));
			})
			.build()
				.run();
	}
}