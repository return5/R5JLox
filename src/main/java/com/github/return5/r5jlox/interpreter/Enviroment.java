package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.token.Token;

import java.util.HashMap;
import java.util.Map;

public class Enviroment {
    private final Map<String,Object> values = new HashMap<>();

    void define(final String name, final Object value) {
        values.put(name,value);
    }

    <T> Object get(final Token<T> name) {
        if(values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }
        throw new R5JloxRuntimeError(name,"Undefined variable '" + name.getLexeme() + "'.");
    }
}
