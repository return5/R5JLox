package main.java.com.github.return5.r5jlox.callable;

import main.java.com.github.return5.r5jlox.interpreter.Environment;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;
import main.java.com.github.return5.r5jlox.tree.Stmt;

import java.util.List;
import java.util.stream.IntStream;

public class R5JLoxFunction<T> implements R5JLoxCallable {

    private final Stmt.Function<T> declaration;

    public R5JLoxFunction(final Stmt.Function<T> declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object call(final Interpreter interpreter,final List<Object> arguments) {
        final Environment environment = new Environment(interpreter.getGlobal()) ;
        IntStream.range(0,declaration.getParams().size())
                .forEach(i -> environment.define(declaration.getParams().get(i).getLexeme(),arguments.get(i)));
        interpreter.executeBlock(declaration.getBody(),environment);
        return null;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    public Stmt.Function<T> getDeclaration() {
        return declaration;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + " >";
    }

}
