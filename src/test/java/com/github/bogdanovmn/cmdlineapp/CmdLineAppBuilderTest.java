package com.github.bogdanovmn.cmdlineapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderTest {
	@Test
	public void build() {
		new CmdLineAppBuilder(new String[] {"-i", "123", "-b"})
			.withArg("integer", "source arg description")
			.withFlag("bool-flag", "bool-flag description")
			.withEntryPoint(cmdLine -> {
				assertEquals("123", cmdLine.getOptionValue("i"));
				assertTrue(cmdLine.hasOption("b"));
			})
			.build()
				.run();
	}
}