package com.github.bogdanovmn.cmdlineapp;

import org.apache.commons.cli.CommandLine;

public interface CmdLineAppEntryPoint {
	void execute(CommandLine cmdLine) throws Exception;
}
