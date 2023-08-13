package com.github.bogdanovmn.cmdline;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CmdLineApp {
    private final CmdLineAppEntryPoint entryPoint;
    private final ParsedOptions parsedOptions;

    public void run() throws Exception {
        this.entryPoint.execute(
            this.parsedOptions
        );
    }
}
