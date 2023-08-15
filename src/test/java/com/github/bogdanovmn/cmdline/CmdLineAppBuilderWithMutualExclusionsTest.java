package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CmdLineAppBuilderWithMutualExclusionsTest {
    @Test
    public void shouldHandleMutualExclusions() throws Exception {
        new CmdLineAppBuilder(new String[]{"-a", "foo"})
            .withArg("a-opt", "a option description")
            .withArg("b-opt", "b option description")
            .withMutualExclusions("a-opt", "b-opt")
            .withEntryPoint(options -> {
                assertEquals("foo", options.get("a-opt"));
            })
        .build().run();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMutualExclusions() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo", "-b", "bar"})
                .withArg("a-opt", "a option description")
                .withArg("b-opt", "b option description")
                .withArg("c-opt", "c option description")
                .withMutualExclusions("a-opt", "b-opt", "c-opt")
                .withEntryPoint(options -> {})
            .build().run();
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion: [a-opt, b-opt], expected only one of these", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMutualExclusionsGroup() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo", "-b", "bar"})
                .withArg("a-opt", "a option description")
                .withArg("b-opt", "b option description")
                .withArg("c-opt", "c option description")
                .withMutualExclusions("a-opt", Arrays.asList("b-opt", "c-opt"))
                .withEntryPoint(options -> {})
                .build().run();
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion: [a-opt, {b-opt, c-opt}], expected only one of these", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnRequiredOptionInMutualExclusions() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo", "-b", "bar"})
                .withArg("a-opt", "a option description").required()
                .withArg("b-opt", "b option description")
                .withMutualExclusions("a-opt", "b-opt")
                .withEntryPoint(options -> {})
                .build().run();
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion options can't be required, but: a-opt", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnRequiredOptionInMutualExclusionsGroup() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo", "-b", "bar", "-c", "baz"})
                .withArg("a-opt", "a option description")
                .withArg("b-opt", "b option description")
                .withArg("c-opt", "c option description").required()
                .withMutualExclusions("a-opt", Arrays.asList("b-opt", "c-opt"))
                .withEntryPoint(options -> {})
                .build().run();
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion options can't be required, but: c-opt", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnSingleOptionInMutualExclusions() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo", "-b", "bar"})
                .withArg("a-opt", "a option description")
                .withArg("b-opt", "b option description")
                .withMutualExclusions("a-opt")
                .withEntryPoint(options -> {})
                .build().run();
        } catch (IllegalArgumentException ex) {
            assertEquals("Mutual exclusion must contain more than one option", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOptionDuplicationInMutualExclusionsGroup() throws Exception {
        try {
            new CmdLineAppBuilder(new String[]{"-a", "foo"})
                .withArg("a-opt", "a option description")
                .withArg("b-opt", "b option description")
                .withArg("c-opt", "c option description").required()
                .withMutualExclusions("a-opt", Arrays.asList("b-opt", "a-opt"))
                .withEntryPoint(options -> {})
                .build().run();
        } catch (IllegalArgumentException ex) {
            assertEquals("Mutual exclusions already contains 'a-opt' option", ex.getMessage());
            throw ex;
        }
    }
}