package main.java.com.github.return5.r5jlox.errors;

import main.java.com.github.return5.r5jlox.token.Token;

public class R5JloxRuntimeError extends RuntimeException{
    final Token<?> token;

    public R5JloxRuntimeError(final Token<?> token,final String message) {
       super(message);
       this.token = token;
    }

    public Token<?> getToken() {
        return token;
    }
}
