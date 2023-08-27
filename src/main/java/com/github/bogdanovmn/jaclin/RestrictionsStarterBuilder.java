package com.github.bogdanovmn.jaclin;

public interface RestrictionsStarterBuilder {
    RestrictionsInProgressBuilder mutualExclusions(Object... options);
    RestrictionsInProgressBuilder atLeastOneShouldBeUsed(String... options);
}
