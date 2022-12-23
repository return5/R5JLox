package main.java.com.github.return5.r5jlox.callable;

import main.java.com.github.return5.r5jlox.callable.foreignfunctions.R5JLoxClock;

public enum FFEnum {
    CLOCK("clock", R5JLoxClock.clock());


    private final String name;
    private final R5JLoxCallable func;

    FFEnum(final String name, final R5JLoxCallable func) {
        this.name = name;
        this.func = func;
    }

    public String getName() {
        return name;
    }

    public R5JLoxCallable getFunc() {
        return func;
    }
}
