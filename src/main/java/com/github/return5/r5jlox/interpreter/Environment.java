package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.callable.FFEnum;
import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.token.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();

    public Environment(final Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Environment(final FFEnum[] values) {
        Arrays.stream(values).forEach(e -> this.define(e.getName(),e.getFunc()));
        this.enclosing = null;
    }

   public void define(final String name, final Object value) {
        values.put(name,value);
    }

    <T> Object get(final Token<T> name) {
        if(values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }
        if(enclosing != null) {
            return enclosing.get(name);
        }
        throw new R5JloxRuntimeError(name,"Undefined variable '" + name.getLexeme() + "'.");
    }

    public <T> void assign(final Token<T> name, final Object value) {
        if(values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
        }
        else if(enclosing != null) {
            enclosing.assign(name,value);
        }
        else {
            throw new R5JloxRuntimeError(name,"Undefined variable '" + name.getLexeme() + "'.");
        }
    }

    public Environment getEnclosing() {
        return enclosing;
    }

}
