package main.java.com.github.return5.r5jlox.parser;

import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.errors.ParseError;
import main.java.com.github.return5.r5jlox.stmt.Stmt;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static main.java.com.github.return5.r5jlox.token.TokenType.*;

public class Parser{
    private final List<Token<?>> tokens;
    private int current;
    private final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();

    public Parser(final List<Token<?>> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() { return assignment(); }

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

    private Expr assignment() {
        final Expr expr = or();
        if(match(EQUAL)) {
            final Token<?> equal = previous();
            final Expr value = assignment();
            if(expr instanceof final Expr.Variable<?> val ) {
                final Token<?> name = val.getName();
                return new Expr.Assign<>(name,value);
            }
            errorHandler.error(equal, "Invalid assignment target.");
        }
        return expr;
    }

    public List<Stmt> parse() {
        final List<Stmt> statements = new LinkedList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Expr or() {
        return logical(this::and,this::previous,Expr.Logical::new,OR);
    }

    private Expr and() {
        return logical(this::equality,this::previous,Expr.Logical::new,AND);
    }

    private Expr logical(final Supplier<Expr> supplier, final Supplier<Token<?>> opFunc,
                         final ConstructorFunc<Expr.Logical<?>, Expr,Token<?>,Expr> constructor,final TokenType match) {
        Expr expr = supplier.get();
        while(match(match)) {
            final Token<?> operator = opFunc.get();
            final Expr right = supplier.get();
            expr = constructor.construct(expr,operator,right);
        }
        return expr;
    }

    private Stmt statement() {
        if(match(IF)) {
           return ifStatement();
        }
        if(match(SAY)) {
            return sayStatement();
        }
        if(match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }
        return expressionStatement();
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN,"Expect '(' after 'if'.");
        final Expr condition = expression();
        consume(RIGHT_PAREN,"Expect ')' after if condition.");
        final Stmt thenBranch = statement();
        final Stmt elseBranch = (match(ELSE))? statement() : null;
        return new Stmt.If(condition,thenBranch,elseBranch);
    }

    private List<Stmt> block() {
        final List<Stmt> statements = new LinkedList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE,"Expect '}' after block.");
        return statements;
    }

    private Stmt declaration() {
        try {
            if(match(STASH)) {
                return varDeclaration();
            }
            return statement();
        }catch(final ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        final Token<?> name = consume(IDENTIFIER,"Expect variable name.");
        final Expr initializer = (match(EQUAL)) ? expression() : null;
        consume(SEMICOLON,"Expect ';' after variable declaration.");
        return new Stmt.Stash<>(name,initializer);
    }

    private Stmt sayStatement() {
        final Expr value = expression();
        consume(SEMICOLON, "Expect ; after value.");
        return new Stmt.Say(value);
    }

    private Stmt expressionStatement() {
        final Expr value = expression();
        consume(SEMICOLON, "Expect ; after value.");
        return new Stmt.Expression(value);
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
        if(match(IDENTIFIER)) {
            return new Expr.Variable<>(previous());
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
