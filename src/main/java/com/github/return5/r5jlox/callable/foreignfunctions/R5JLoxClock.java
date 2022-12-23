package main.java.com.github.return5.r5jlox.callable.foreignfunctions;

import main.java.com.github.return5.r5jlox.callable.R5JLoxCallable;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;

import java.util.List;

public class R5JLoxClock implements R5JLoxCallable {

    public static final R5JLoxClock clock = new R5JLoxClock();

    private R5JLoxClock(){
        super();
    }

    public static R5JLoxClock clock() {
        return clock;
    }

    @Override
    public Object call(final Interpreter interpreter,final List<Object> arguments) {
        return System.currentTimeMillis() / 1000.0;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public String toString() {
        return "<native fn>";
    }
}
