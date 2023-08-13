package com.github.bogdanovmn.cmdline;

import org.apache.commons.cli.CommandLine;

public interface CmdLineAppEntryPoint {
    void execute(CommandLine cmdLine) throws Exception;
}
