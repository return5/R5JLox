package main.java.com.github.return5.r5jlox.callable;

import main.java.com.github.return5.r5jlox.errors.Return;
import main.java.com.github.return5.r5jlox.interpreter.Environment;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;
import main.java.com.github.return5.r5jlox.tree.Stmt;

import java.util.List;
import java.util.stream.IntStream;

public class R5JLoxFunction<T> implements R5JLoxCallable {

    private final Stmt.Function<T> declaration;
    private final Environment closure;

    public R5JLoxFunction(final Stmt.Function<T> declaration,final Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(final Interpreter interpreter,final List<Object> arguments) {
        final Environment environment = new Environment(closure) ;
        IntStream.range(0,declaration.getParams().size())
                .forEach(i -> environment.define(declaration.getParams().get(i).getLexeme(),arguments.get(i)));
        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        }catch(final Return r) {
            return r.getValue();
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + " >";
    }
}
