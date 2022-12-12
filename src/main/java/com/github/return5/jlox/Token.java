package main.java.com.github.return5.jlox;

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

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
