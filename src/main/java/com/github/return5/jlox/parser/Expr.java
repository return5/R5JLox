package main.java.com.github.return5.jlox.parser;

import main.java.com.github.return5.jlox.scanner.Token;

public abstract class Expr {

    private Expr() {
        super();
    }

    static class Binary <T> extends Expr {
        final Expr left;
        final Expr right;
        final Token<T> operator;

        public Binary(final Expr left, final Token<T> operator,final Expr right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
    }
}
