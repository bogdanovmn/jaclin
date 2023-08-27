package com.github.bogdanovmn;

import com.github.bogdanovmn.jaclin.CLI;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CLIRestrictionsMutualExclusionsTest {
    @Test
    public void shouldHandleMutualExclusions() throws Exception {
        new CLI("app-name", "description")
            .withOptions()
                .strArg("a-opt", "a option description")
                .strArg("b-opt", "b option description")
            .withRestrictions()
                .mutualExclusions("a-opt", "b-opt")
            .withEntryPoint(options -> {
                assertEquals("foo", options.get("a-opt"));
            })
        .run("-a", "foo");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMutualExclusions() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("a-opt", "a option description")
                    .strArg("b-opt", "b option description")
                    .strArg("c-opt", "c option description")
                .withRestrictions()
                    .mutualExclusions("a-opt", "b-opt", "c-opt")
                .withEntryPoint(options -> {})
            .run("-a", "foo", "-b", "bar");
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion: [a-opt, b-opt], expected only one of these", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMutualExclusionsGroup() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("a-opt", "a option description")
                    .strArg("b-opt", "b option description")
                    .strArg("c-opt", "c option description")
                .withRestrictions()
                    .mutualExclusions("a-opt", Arrays.asList("b-opt", "c-opt"))
                .withEntryPoint(options -> {})
            .run("-a", "foo", "-b", "bar");
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion: [a-opt, {b-opt, c-opt}], expected only one of these", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnRequiredOptionInMutualExclusions() throws Exception {
        try {
            new CLI("app-name", "description")
                .withRequiredOptions()
                    .strArg("a-opt", "a option description")
                .withOptions()
                    .strArg("b-opt", "b option description")
                .withRestrictions()
                    .mutualExclusions("a-opt", "b-opt")
                .withEntryPoint(options -> {})
            .run("-a", "foo", "-b", "bar");
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion options can't be required, but: a-opt", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnRequiredOptionInMutualExclusionsGroup() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("a-opt", "a option description")
                    .strArg("b-opt", "b option description")
                .withRequiredOptions()
                    .strArg("c-opt", "c option description")
                .withRestrictions()
                    .mutualExclusions("a-opt", Arrays.asList("b-opt", "c-opt"))
                .withEntryPoint(options -> {})
            .run("-a", "foo", "-b", "bar", "-c", "baz");
        } catch (IllegalStateException ex) {
            assertEquals("Mutual exclusion options can't be required, but: c-opt", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnSingleOptionInMutualExclusions() throws Exception {
        try {
            new CLI("app-name", "description")
                .withOptions()
                    .strArg("a-opt", "a option description")
                    .strArg("b-opt", "b option description")
                .withRestrictions()
                    .mutualExclusions("a-opt")
                .withEntryPoint(options -> {})
            .run("-a", "foo", "-b", "bar");
        } catch (IllegalArgumentException ex) {
            assertEquals("Mutual exclusion must contain more than one option", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnOptionDuplicationInMutualExclusionsGroup() throws Exception {
        try {
            new CLI("app-name", "description")
                .withRequiredOptions()
                    .strArg("c-opt", "c option description")
                .withOptions()
                    .strArg("a-opt", "a option description")
                    .strArg("b-opt", "b option description")
                .withRestrictions()
                    .mutualExclusions("a-opt", Arrays.asList("b-opt", "a-opt"))
                .withEntryPoint(options -> {})
            .run("-a", "foo");
        } catch (IllegalArgumentException ex) {
            assertEquals("Mutual exclusions already contains 'a-opt' option", ex.getMessage());
            throw ex;
        }
    }
}