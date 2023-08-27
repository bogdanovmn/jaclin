package com.github.bogdanovmn.jaclin;

public interface OptionsStarterBuilder {
    OptionsInProgressBuilder strArg(String name, String description);

    OptionsInProgressBuilder intArg(String name, String description);

    <E extends Enum<E>> OptionsInProgressBuilder enumArg(String name, String description, Class<E> type);

    FlagOptionInProgressBuilder flag(String name, String description);
}
