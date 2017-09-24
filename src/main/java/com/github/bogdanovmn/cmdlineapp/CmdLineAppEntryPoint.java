package com.github.bogdanovmn.cmdlineapp;

interface CmdLineAppEntryPoint<T> {
	void execute(T t);
}
