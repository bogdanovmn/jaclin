package com.github.bogdanovmn.cmdlineapp;

public interface CmdLineAppEntryPoint<T> {
	void execute(T t) throws Exception;
}
