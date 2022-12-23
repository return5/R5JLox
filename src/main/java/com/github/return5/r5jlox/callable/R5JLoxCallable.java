package main.java.com.github.return5.r5jlox.callable;

import main.java.com.github.return5.r5jlox.interpreter.Interpreter;

import java.util.List;

public interface R5JLoxCallable {
    Object call(final Interpreter interpreter, final List<Object> arguments);
    int arity();
}
