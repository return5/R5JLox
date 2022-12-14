package main.java.com.github.return5.r5jlox.errorhandler;

import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;

public class ParserErrorHandler {

    private static final ParserErrorHandler self = new ParserErrorHandler();

    private boolean hadError = false;

    public void error(final int line, final String message) {
        report(line,"",message);
    }

    private void report(final int line, final String where, final String message) {
        System.err.println("[line " + line + " ] Error: " + where + ": " + message);
        hadError = true;
    }

    private <T> void reportError(final Token<T> token, final String message) {
        if(token.getType() == TokenType.EOF) {
            report(token.getLine()," at end",message);
        }
        else {
            report(token.getLine()," at '"+token.getLexeme() + "'",message);
        }
    }

    public <T> ParseError error(final Token<T> token, final String message) {
        reportError(token,message);
        return new ParseError();
    }

    public boolean isHadError() {
        return hadError;
    }

    public void setHadError(final boolean hadError) {
        this.hadError = hadError;
    }

    public static ParserErrorHandler getParseErrorHandler() {
        return self;
    }

    private ParserErrorHandler() {
       super();
    }
}
