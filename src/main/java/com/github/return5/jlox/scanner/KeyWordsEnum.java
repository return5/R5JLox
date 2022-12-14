package main.java.com.github.return5.jlox.scanner;

import main.java.com.github.return5.jlox.token.TokenType;

public enum KeyWordsEnum {
    AND("and", TokenType.AND),
    DESIGNATION("designation",TokenType.DESIGNATION),
    ELSE("else",TokenType.ELSE),
    FALSE("false",TokenType.FALSE),
	FUNCTI("functi",TokenType.FUNCTI),
    FOR("for",TokenType.FOR),
	IF("if",TokenType.IF),
	NIL("nil",TokenType.NIL),
	PRINT("print",TokenType.SAY),
	RETURN("return",TokenType.RETURN),
	SUPER("super",TokenType.SUPER),
	SELF("self",TokenType.SELF),
	TRUE("true",TokenType.TRUE),
	STASH("stash",TokenType.STASH),
	WHILE("while",TokenType.WHILE);

    private final String keyWord;
    private final TokenType type;

    KeyWordsEnum(final String keyWord, final TokenType type) {
        this.keyWord = keyWord;
        this.type = type;
    }
    public String keyWord() {
        return this.keyWord;
    }
    public TokenType type() {
        return this.type;
    }
}
