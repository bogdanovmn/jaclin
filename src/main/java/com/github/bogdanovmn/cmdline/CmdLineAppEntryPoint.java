package com.github.bogdanovmn.cmdline;

public interface CmdLineAppEntryPoint {
    void execute(ParsedOptions options) throws Exception;
}
