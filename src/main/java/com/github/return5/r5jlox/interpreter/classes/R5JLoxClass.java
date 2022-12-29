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
    private final R5JLoxClass superClass;

    public R5JLoxClass(final String name) {
        this.name = name;
        this.methods = new HashMap<>();
        this.superClass = null;
    }

   public R5JLoxClass(final String name, final R5JLoxClass superClass, final Map<String, R5JLoxFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superClass = superClass;
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
        final R5JLoxInstance instance = new R5JLoxInstance(this);
        final R5JLoxFunction init = findMethod("init");
        if(init != null) {
            init.bind(instance).call(interpreter,arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        final R5JLoxFunction instance = findMethod("init");
        if(instance == null) {
            return 0;
        }
        return instance.arity();
    }

    private R5JLoxFunction findSuperClassMethod(final String name) {
        if(this.superClass != null) {
           return superClass.findMethod(name);
        }
        return null;
    }

    public R5JLoxFunction findMethod(final String name) {
        return methods.getOrDefault(name, findSuperClassMethod(name));
    }
}
