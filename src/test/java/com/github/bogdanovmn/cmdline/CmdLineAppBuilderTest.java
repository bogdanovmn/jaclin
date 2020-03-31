package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.Option;
import org.junit.Test;

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

	@Test(expected = RuntimeException.class)
	public void shouldRaiseAnExceptionOnAbsenceOfAtLeastOneRequiredOption() throws Exception {
		try {
			new CmdLineAppBuilder(new String[]{"-x"})
				.withArg("integer-opt", "some integer arg")
				.withArg("string-opt", "some string arg")
				.withFlag("xxx-opt", "xxx!")
				.withAtLeastOneRequiredOption("integer-opt", "string-opt")
				.withEntryPoint(cmdLine -> {})
				.build().run();
		}
		catch (RuntimeException ex) {
			assertEquals("You should use at least one of these options: 'integer-opt', 'string-opt'", ex.getMessage());
			throw ex;
		}
	}

	@Test
	public void shouldNotRaiseAnExceptionIfAtLeastOneRequiredOptionIsDefined() throws Exception {
		new CmdLineAppBuilder(new String[]{"-x", "-s", "s value"})
			.withArg("integer-opt", "some integer arg")
			.withArg("string-opt", "some string arg")
			.withFlag("xxx-opt", "xxx!")
			.withAtLeastOneRequiredOption("integer-opt", "string-opt")
			.withEntryPoint(cmdLine -> {
				assertEquals("s value", cmdLine.getOptionValue("s"));
			})
			.build().run();
	}

	@Test(expected = RuntimeException.class)
	public void shouldRaiseAnExceptionOnUnknownAtLeastOneRequiredOption() throws Exception {
		try {
			new CmdLineAppBuilder(new String[]{"-m", "2", "-s", "3"})
				.withJarName("my-jar-name")
				.withDescription("My program does ...")

				.withArg("some-option", "...description of the option...")

				.withFlag("flag", "...description of the option...")

				.withCustomOption(
					Option.builder()
						.longOpt("custom-option")
						.desc("...description of the option...")
						.build()
				)

				.withRequiredArg("mandatory-option", "...description of the option...")

				.withAtLeastOneRequiredOption("some-option", "flag1")

				.withEntryPoint(
					cmdLine -> {}
				).build().run();
		}
		catch (RuntimeException ex) {
			assertEquals("Unknown option: [flag1]", ex.getMessage());
			throw ex;
		}
	}
}