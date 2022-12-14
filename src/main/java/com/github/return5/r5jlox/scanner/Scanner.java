package main.java.com.github.return5.r5jlox.scanner;

import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;

import java.util.*;
import java.util.stream.Collectors;

import static main.java.com.github.return5.r5jlox.token.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token<?>> tokens = new LinkedList<>();
    private int start = 0;  //first char in lexeme.
    private int current = 0; //current character being scanned.
    private int line = 0;  //current source line.
    private final Map<String, TokenType> keyWords = Arrays.stream(KeyWordsEnum.values())
            .collect(Collectors.toUnmodifiableMap(KeyWordsEnum::keyWord, KeyWordsEnum::type));
    private final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();

    public Scanner(final String source) {
        this.source = source;
    }

    public List<Token<?>> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token<>(EOF," ",null,line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        final char c = advance();
        switch (c) {
            case ')' -> addToken(LEFT_PAREN);
            case '(' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(match('.') ? CONCAT : DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> handleComment();
            case ' ', '\r', '\t' -> {
                //do nothing with empty space
            }
            case '\n' -> line++;
            case '"' -> handleString();
            case '0','1','2','3','4','5','6','7','8','9' -> number();
            case '_','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x',
                    'y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V',
                    'W','X','Y','Z' -> identifier();
            default -> errorHandler.error(line, "Unexpected character.");
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(final TokenType type) {
        addToken(type,null);
    }

    private <T>void addToken(final TokenType type,final T literal) {
        final String text = source.substring(start,current);
        tokens.add(new Token<>(type,text,literal,line));
    }

    private boolean match(final char expected) {
        if(isAtEnd()) {
            return false;
        }
        if(expected != source.charAt(current)) {
            return false;
        }
        current++;
        return true;
    }

    private void handleComment() {
        if(match('/')) {
            while(peek() != '\n' && !isAtEnd()) {
                advance();
            }
        }
        else {
            addToken(SLASH);
        }
    }

    private char peek() {
        if(isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private void handleString() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            errorHandler.error(line,"Unterminated String.");
            return;
        }
        advance();
        final String val = source.substring(start + 1, current -1);
        addToken(STRING,val);
    }

    private boolean isDigit(final char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
            default -> false;
        };
    }

    private void advanceDigit() {
        while(isDigit(peek())) {
            advance();
        }
    }

    private void number() {
        advanceDigit();
        if(peek() == '.' && isDigit(peekNext())) {
            advance();
            advanceDigit();
            addToken(NUMBER,Double.parseDouble(source.substring(start,current)));
        }
        else {
            addToken(NUMBER, Integer.parseInt(source.substring(start,current)));
        }
    }

    private char peekNext() {
        final int next = current + 1;
        if( next >= source.length()) {
            return '\0';
        }
        return source.charAt(next);
    }

    private boolean isAlpha(final char c) {
       return switch(c) {
           case '_','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x',
                   'y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V',
                   'W','X','Y','Z' -> true;
           default -> false;
       };
    }

    private boolean isAlphaNumeric(final char c) {
        return isDigit(c) || isAlpha(c);
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) {
            advance();
        }
        final String text = source.substring(start,current);
        final TokenType type = keyWords.get(text);
        addToken(Objects.requireNonNullElse(type, IDENTIFIER));
    }
}
