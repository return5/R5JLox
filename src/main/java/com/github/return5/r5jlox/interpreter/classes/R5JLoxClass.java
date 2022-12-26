package main.java.com.github.return5.r5jlox.interpreter.classes;


import main.java.com.github.return5.r5jlox.callable.R5JLoxCallable;
import main.java.com.github.return5.r5jlox.callable.R5JLoxFunction;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class R5JLoxClass implements R5JLoxCallable {

    private final String name;
    private final Map<String,R5JLoxFunction> methods;

    public R5JLoxClass(final String name) {
        this.name = name;
        this.methods = new HashMap<>();
    }

   public R5JLoxClass(final String name, final Map<String, R5JLoxFunction> methods) {
        this.name = name;
        this.methods = methods;
   }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) {
        return new R5JLoxInstance(this);
    }

    @Override
    public int arity() {
        return 0;
    }

    public R5JLoxFunction findMethod(final String name) {
        return methods.getOrDefault(name, null);
    }
}
