package com.github.bogdanovmn.cmdlineapp;

import org.apache.commons.cli.*;

public class CmdLineAppBuilder {
	private final String[] args;
	private final Options options = new Options();
	private String jarName = "<app-name>";
	private String appDescription = "";
	private CmdLineAppEntryPoint<CommandLine> entryPoint;

	public CmdLineAppBuilder(String[] args) {
		this.args = args;
	}

	public CmdLineAppBuilder withJarName(String jarName) {
		this.jarName = jarName;
		return this;
	}

	public CmdLineAppBuilder withAppDescription(String appDescription) {
		this.appDescription = appDescription;
		return this;
	}

	public CmdLineAppBuilder withArg(String name, String description) {
		this.options.addOption(
			Option.builder(name.substring(0, 1).toLowerCase())
				.longOpt(name)
				.hasArg().argName("ARG")
				.desc(description)
				.required()
			.build()
		);
		return this;
	}

	CmdLineAppBuilder withEntryPoint(CmdLineAppEntryPoint<CommandLine> entryPoint) {
		this.entryPoint = entryPoint;
		return this;
	}

	public CmdLineApp build() {
		try {
			return new CmdLineApp(
				entryPoint,
				new DefaultParser()
					.parse(this.options, this.args)
			);
		}
		catch (ParseException e) {
			this.printHelp();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void printHelp() {
		new HelpFormatter()
			.printHelp(
				String.format("java -jar %s.jar", this.jarName),
				this.appDescription,
				this.options,
				"",
				true
			);
	}
}