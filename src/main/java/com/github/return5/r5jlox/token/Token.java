package main.java.com.github.return5.r5jlox.token;

public class Token <T> {
    final TokenType type;
    final String lexeme;
    final T literal;
    final int line;

    public Token(final TokenType type,final String lexeme,final T literal,final int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public T getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
