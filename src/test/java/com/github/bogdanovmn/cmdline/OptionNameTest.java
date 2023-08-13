package com.github.bogdanovmn.cmdline;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class OptionNameTest {

    @Test
    public void isMultiPart() {
        assertTrue(new OptionName("option-name").isMultiPart());
        assertFalse(new OptionName("option").isMultiPart());
    }

    @Test
    public void parts() {
        assertEquals(Collections.singletonList("option"), new OptionName("option").parts());
        assertEquals(Arrays.asList("option", "number", "two"), new OptionName("option-number_two").parts());
    }

    @Test
    public void consonants() {
        assertArrayEquals(
            new char[]{'p', 't', 'n'},
            new OptionName("option").consonants()
        );
    }
}