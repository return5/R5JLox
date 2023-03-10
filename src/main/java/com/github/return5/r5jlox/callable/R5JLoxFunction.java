package main.java.com.github.return5.r5jlox.callable;

import main.java.com.github.return5.r5jlox.errors.Return;
import main.java.com.github.return5.r5jlox.interpreter.Environment;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;
import main.java.com.github.return5.r5jlox.interpreter.classes.R5JLoxInstance;
import main.java.com.github.return5.r5jlox.tree.Expr;
import main.java.com.github.return5.r5jlox.tree.Stmt;

import java.util.List;
import java.util.stream.IntStream;

public class R5JLoxFunction implements R5JLoxCallable {

    private final Expr.Function declaration;
    private final Environment closure;
    private final String name;
    private final boolean isInitializer;

    public R5JLoxFunction(final String name, final Expr.Function declaration, final Environment closure,final boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.name = name;
        this.isInitializer = isInitializer;
    }

    public <T> R5JLoxFunction(final Stmt.Function<T> e, final Environment environment,final boolean isInitializer) {
        this.closure = environment;
        this.declaration = e.getFunction();
        this.name = e.getName().getLexeme();
        this.isInitializer = isInitializer;
    }

    @Override
    public Object call(final Interpreter interpreter,final List<Object> arguments) {
        final Environment environment = new Environment(closure) ;
        IntStream.range(0,declaration.getParameters().size())
                .forEach(i -> environment.define(declaration.getParameters().get(i).getLexeme(),arguments.get(i)));
        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        }catch(final Return r) {
            if(isInitializer) {
                return closure.getAt(0,"this");
            }
            return r.getValue();
        }
        if(isInitializer) {
            return closure.getAt(0,"this");
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.getParameters().size();
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<fn>";
        }
        return "<fn " + name + " >";
    }

    public R5JLoxFunction bind(final R5JLoxInstance r5JLoxInstance) {
        final Environment environment = new Environment(closure);
        environment.define("self",r5JLoxInstance);
        return new R5JLoxFunction("self",declaration,environment,isInitializer);
    }
}
