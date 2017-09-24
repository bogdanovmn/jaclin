package com.github.bogdanovmn.cmdlineapp;

import org.junit.Test;

public class CmdLineAppBuilderTest {
	@Test
	public void build() {
		CmdLineApp app = new CmdLineAppBuilder(
			new String[] {"-s 123"}
		).withArg("source", "source arg description")
			.withEntryPoint(x -> System.out.println(x.getOptionValue("s")))
			.build();
		
		app.run();
	}
}