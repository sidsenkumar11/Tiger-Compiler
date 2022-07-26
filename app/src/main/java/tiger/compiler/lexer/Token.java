package tiger.compiler.lexer;

import tiger.compiler.parser.Symbol;

public class Token {
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

    public Symbol getTerminalSymbol() {
        // Maps this token to a symbol
        switch (this.type) {
            case KEYWORD:
                return Symbol.TerminalSymbols.get(this.lexeme.toLowerCase());
            case ID:
                return Symbol.ID;
            case INTLIT:
                return Symbol.INTLIT;
            case FLOATLIT:
                return Symbol.FLOATLIT;
            case EOF:
                return Symbol.EOF;
            default:
                return null;
        }
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

    public static final Token ErrorToken = new Token("error", TokenType.ERROR);
    public static final Token EOFToken = new Token("$", TokenType.EOF);
}
