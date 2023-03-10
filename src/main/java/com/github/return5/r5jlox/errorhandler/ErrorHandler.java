package main.java.com.github.return5.r5jlox.errorhandler;

import main.java.com.github.return5.r5jlox.errors.ParseError;
import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;

public class ErrorHandler {

    private static final ErrorHandler self = new ErrorHandler();

    private boolean hadError = false;
    private boolean hadRuntimeError = false;

    public void error(final int line, final String message) {
        report(line,"",message);
    }

    private void report(final int line, final String where, final String message) {
        System.err.println("[line " + line + " ] Error: " + where + ": " + message);
        hadError = true;
    }

    public <T> void reportError(final Token<T> token, final String message) {
        if(token.getType() == TokenType.EOF) {
            report(token.getLine()," at end",message);
        }
        else {
            report(token.getLine()," at '"+token.getLexeme() + "'",message);
        }
    }

    public <T> ParseError throwableError(final Token<T> token, final String message) {
        reportError(token,message);
        return new ParseError();
    }

    public boolean isHadError() {
        return hadError;
    }

    public void setHadError(final boolean hadError) {
        this.hadError = hadError;
    }

    public static ErrorHandler getParseErrorHandler() {
        return self;
    }

    private ErrorHandler() {
       super();
    }

    public boolean isHadRuntimeError() {
        return hadRuntimeError;
    }

    public void runtimeError(final R5JloxRuntimeError e) {
        System.err.println(e.getMessage() + "\n[Line " + e.getToken().getLine() + "]");
        hadRuntimeError = true;
    }
}
