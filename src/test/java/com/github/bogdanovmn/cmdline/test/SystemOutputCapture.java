package com.github.bogdanovmn.cmdline.test;

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
        System.setOut(new PrintStream(outContent));

        task.run();

        String[] lines = outContent.toString().split("\n");
        System.setOut(originalOut);

        Arrays.stream(lines).forEach(System.out::println);

        return lines;
    }
}
