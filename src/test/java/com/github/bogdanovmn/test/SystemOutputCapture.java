package com.github.bogdanovmn.test;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

@RequiredArgsConstructor
public class SystemOutputCapture {
    private final Runnable task;

    public String[] outputOfExecution() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        PrintStream outCaptureStream = new PrintStream(outContent);
        System.setOut(outCaptureStream);
        System.setErr(outCaptureStream);

        task.run();

        String[] lines = outContent.toString().split("\n");
        System.setOut(originalOut);
        System.setErr(originalErr);

        Arrays.stream(lines).forEach(System.out::println);

        return lines;
    }
}
