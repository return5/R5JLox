package main.java.com.github.return5.r5jlox.interpreter.classes;

import main.java.com.github.return5.r5jlox.callable.R5JLoxFunction;
import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.token.Token;

import java.util.HashMap;
import java.util.Map;

public class R5JLoxInstance {

    private final Map<String,Object> fields = new HashMap<>();

    private final R5JLoxClass clazz;

    public R5JLoxInstance(final R5JLoxClass clazz) {
        this.clazz = clazz;
    }

    public R5JLoxClass getClazz() {
        return clazz;
    }

    public <T> Object get(final Token<T> name) {
        if(fields.containsKey(name.getLexeme())) {
            return fields.get(name.getLexeme());
        }
        final R5JLoxFunction func = clazz.findMethod(name.getLexeme());
        if(func != null) {
            return func;
        }
        throw new R5JloxRuntimeError(name,"Undefined property '" + name.getLexeme() + "'.");
    }

    @Override
    public String toString() {
        return clazz.getName() + " instance";
    }

    public <T> void setField(final Token<T> name, final Object value) {
        fields.put(name.getLexeme(),value);
    }
}
