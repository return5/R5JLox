package main.java.com.github.return5.r5jlox.parser;

import main.java.com.github.return5.r5jlox.errorhandler.ParseError;
import main.java.com.github.return5.r5jlox.errorhandler.ParserErrorHandler;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.List;
import java.util.function.Supplier;

import static main.java.com.github.return5.r5jlox.token.TokenType.*;

public class Parser{
    private final List<Token<?>> tokens;
    private int current;
    private final ParserErrorHandler errorHandler = ParserErrorHandler.getParseErrorHandler();

    public Parser(final List<Token<?>> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() { return equality(); }

    private Expr equality() {
        return leftAssociate(this::comparison,BANG_EQUAL,EQUAL_EQUAL);
    }

    private Expr comparison() {
        return leftAssociate(this::term,GREATER,GREATER_EQUAL,LESS,LESS_EQUAL);
    }

    private Expr term() {
        return leftAssociate(this::factor,MINUS,PLUS,CONCAT);
    }

    private Expr factor() {
        return leftAssociate(this::unary,STAR,SLASH);
    }


    public Expr parse() {
        try {
            return expression();
        }catch(final ParseError e) {
            return null;
        }
    }

    private Expr unary() {
        if(match(BANG,MINUS)) {
            final Token<?> operator = previous();
            final Expr right = unary();
            return new Expr.Unary<>(operator,right);
        }
        return primary();
    }

    private Expr primary() {
        if(match(FALSE)) {
            return new Expr.Literal<>(false);
        }
        if(match(TRUE)) {
            return new Expr.Literal<>(true);
        }
        if(match(NIL)) {
            return new Expr.Literal<>(null);
        }
        if(match(NUMBER,STRING)) {
            return new Expr.Literal<>(previous().getLiteral());
        }
        if(match(LEFT_PAREN)) {
            final Expr expr = expression();
            consume(RIGHT_PAREN,"Expect ')' after expression");
            return new Expr.Grouping(expr);
        }
        throw errorHandler.error(peek(),"Expect Expression.");
    }

    private Expr leftAssociate(final Supplier<Expr> func,final TokenType...types) {
        Expr expr = func.get();
        while(match(types)) {
            final Token<?> operator = previous();
            final Expr right = func.get();
            expr = new Expr.Binary<>(expr,operator,right);
        }
        return expr;
    }


    private void synchronize() {
        advance();
        while(!isAtEnd()) {
            if(previous().getType() == SEMICOLON) {
                return;
            }
            switch(peek().getType()) {
                case DESIGNATION,STASH,FUNCTI,FOR,IF,WHILE,SAY,RETURN: return;
                default: // do nothing
                    break;
            }
            advance();
        }
    }

    private boolean match(final TokenType...types) {
        for(final TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(final TokenType type) {
        if(isAtEnd()) {
            return false;
        }
        return peek().getType() == type;
    }

    private Token<?> advance() {
        if(!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token<?> consume(final TokenType type, final String message) {
        if(check(type)) {
            return advance();
        }
        throw errorHandler.error(peek(),message);
    }

    private boolean isAtEnd() {
        return peek().getType() == EOF;
    }

    private Token<?> peek() {
       return tokens.get(current);
    }

    private Token<?> previous() {
        return tokens.get(current - 1);
    }


}
