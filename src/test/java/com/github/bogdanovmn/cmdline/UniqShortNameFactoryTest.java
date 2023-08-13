package com.github.bogdanovmn.cmdline;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class UniqShortNameFactoryTest {

    private final String optionName;
    private final String shortOptionName;

    private final static UniqShortNameFactory options = new UniqShortNameFactory();

    public UniqShortNameFactoryTest(String optionName, String shortOptionName) {
        this.optionName = optionName;
        this.shortOptionName = shortOptionName;
    }

    @Parameterized.Parameters(name = "{index}: {0} --> {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"option-x",       "o"},
            {"option2",        "o2"},
            {"pain",           "p"},
            {"position",       "pn"},
            {"pattern",        "ptn"},
            {"option-two",     "ot"},
            {"option-three",   "optionthree"},
            {"option_three",   "ot2"},
            {"boolean",        "b"},
            {"bla-bla-option", "bbo"}
        });
    }

    @Test
    public void shortName() {
		assertEquals(shortOptionName, options.produce(optionName));
	}
}