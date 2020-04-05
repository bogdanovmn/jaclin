package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderTest {
	@Test
	public void shouldHandleOptions() throws Exception {
		new CmdLineAppBuilder(new String[] {"-i", "123", "-b"})
			.withArg("integer-opt", "source arg description")
			.withFlag("bool-flag", "bool-flag description")
			.withEntryPoint(cmdLine -> {
				assertEquals("123", cmdLine.getOptionValue("i"));
				assertTrue(cmdLine.hasOption("b"));
			})
			.build().run();
	}

	@Test(expected = RuntimeException.class)
	public void shouldHandleRequiredOption() throws Exception {
		try {
			new CmdLineAppBuilder(new String[]{"-b"})
				.withRequiredArg("integer-opt", "source arg description")
				.withFlag("bool-flag", "bool-flag description")
				.withEntryPoint(cmdLine -> {})
				.build().run();
		}
		catch (RuntimeException ex) {
			assertEquals("Missing required option: i", ex.getMessage());
			throw ex;
		}
	}

	@Test
	public void shouldHandleOptionWithMultiValue() throws Exception {
		new CmdLineAppBuilder(new String[] {"-i", "123", "456", "789", "-b"})
			.withArg("integer-opt", "source arg description")
			.withFlag("bool-flag", "bool-flag description")
			.withEntryPoint(cmdLine -> {
				assertEquals(3, cmdLine.getOptionValues("i").length);
				assertEquals(
					Arrays.asList("123", "456", "789"),
					Arrays.asList(cmdLine.getOptionValues("i"))
				);
				assertTrue(cmdLine.hasOption("b"));
			})
			.build().run();
	}
}