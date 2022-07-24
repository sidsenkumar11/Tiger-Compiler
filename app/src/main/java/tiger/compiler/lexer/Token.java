package tiger.compiler.lexer;

public class Token {
    public static final Token ErrorToken = new Token("error", TokenType.ERROR);
    public static final Token EOFToken = new Token("$", TokenType.EOF);
    private String lexeme;
    private TokenType type;

    public Token(String lexeme, TokenType type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    @Override
    public String toString() {
        // Int and float literals get printed like this: "3:intlit"
        if (this.type == TokenType.INTLIT || this.type == TokenType.FLOATLIT) {
            return this.lexeme + ":" + this.type.toString().toLowerCase();
        } else {
            return this.lexeme;
        }
    }
}
