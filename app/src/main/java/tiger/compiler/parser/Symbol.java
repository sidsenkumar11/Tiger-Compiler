package tiger.compiler.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tiger.compiler.lexer.TokenType;

public class Symbol {
    private String name;
    private TokenType terminalType;

    private Symbol(String name) {
        this.name = name;
    }

    private Symbol(String name, TokenType terminalType) {
        this.name = name;
        this.terminalType = terminalType;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTerminal() {
        return this.terminalType != null;
    }

    public TokenType getTerminalType() {
        return this.terminalType;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    // Terminal symbols
    public static final Symbol ARRAY = new Symbol("array", TokenType.KEYWORD);
    public static final Symbol BEGIN = new Symbol("begin", TokenType.KEYWORD);
    public static final Symbol BREAK = new Symbol("break", TokenType.KEYWORD);
    public static final Symbol DO = new Symbol("do", TokenType.KEYWORD);
    public static final Symbol ELSE = new Symbol("else", TokenType.KEYWORD);
    public static final Symbol END = new Symbol("end", TokenType.KEYWORD);
    public static final Symbol ENDDO = new Symbol("enddo", TokenType.KEYWORD);
    public static final Symbol ENDIF = new Symbol("endif", TokenType.KEYWORD);
    public static final Symbol FLOAT = new Symbol("float", TokenType.KEYWORD);
    public static final Symbol FOR = new Symbol("for", TokenType.KEYWORD);
    public static final Symbol FUNC = new Symbol("func", TokenType.KEYWORD);
    public static final Symbol IF = new Symbol("if", TokenType.KEYWORD);
    public static final Symbol IN = new Symbol("in", TokenType.KEYWORD);
    public static final Symbol INT = new Symbol("int", TokenType.KEYWORD);
    public static final Symbol LET = new Symbol("let", TokenType.KEYWORD);
    public static final Symbol OF = new Symbol("of", TokenType.KEYWORD);
    public static final Symbol RETURN = new Symbol("return", TokenType.KEYWORD);
    public static final Symbol THEN = new Symbol("then", TokenType.KEYWORD);
    public static final Symbol TO = new Symbol("to", TokenType.KEYWORD);
    public static final Symbol TYPE = new Symbol("type", TokenType.KEYWORD);
    public static final Symbol VAR = new Symbol("var", TokenType.KEYWORD);
    public static final Symbol WHILE = new Symbol("while", TokenType.KEYWORD);
    public static final Symbol COMMA = new Symbol(",", TokenType.KEYWORD);
    public static final Symbol COLON = new Symbol(":", TokenType.KEYWORD);
    public static final Symbol SEMICOLON = new Symbol(";", TokenType.KEYWORD);
    public static final Symbol LEFT_PAREN = new Symbol("(", TokenType.KEYWORD);
    public static final Symbol RIGHT_PAREN = new Symbol(")", TokenType.KEYWORD);
    public static final Symbol LEFT_BRACKET = new Symbol("[", TokenType.KEYWORD);
    public static final Symbol RIGHT_BRACKET = new Symbol("]", TokenType.KEYWORD);
    public static final Symbol LEFT_CURLY_BRACE = new Symbol("{", TokenType.KEYWORD);
    public static final Symbol RIGHT_CURLY_BRACE =
            new Symbol("}", TokenType.KEYWORD);
    public static final Symbol PERIOD = new Symbol(".", TokenType.KEYWORD);
    public static final Symbol PLUS = new Symbol("+", TokenType.KEYWORD);
    public static final Symbol MINUS = new Symbol("-", TokenType.KEYWORD);
    public static final Symbol STAR = new Symbol("*", TokenType.KEYWORD);
    public static final Symbol SLASH = new Symbol("/", TokenType.KEYWORD);
    public static final Symbol EQUALS = new Symbol("=", TokenType.KEYWORD);
    public static final Symbol NOT_EQUALS = new Symbol("<>", TokenType.KEYWORD);
    public static final Symbol LESS_THAN = new Symbol("<", TokenType.KEYWORD);
    public static final Symbol GREATER_THAN = new Symbol(">", TokenType.KEYWORD);
    public static final Symbol LESS_OR_EQ = new Symbol("<=", TokenType.KEYWORD);
    public static final Symbol GREATER_OR_EQ = new Symbol(">=", TokenType.KEYWORD);
    public static final Symbol AND = new Symbol("&", TokenType.KEYWORD);
    public static final Symbol OR = new Symbol("|", TokenType.KEYWORD);
    public static final Symbol COLON_EQUALS = new Symbol(":=", TokenType.KEYWORD);

    // Terminal user-provided symbols
    public static final Symbol ID = new Symbol("id", TokenType.ID);
    public static final Symbol INTLIT = new Symbol("intlit", TokenType.INTLIT);
    public static final Symbol FLOATLIT = new Symbol("floatlit", TokenType.FLOATLIT);
    public static final Symbol EPSILON = new Symbol("epsilon", TokenType.EPSILON);

    // Terminal system-provided symbols
    public static final Symbol EOF = new Symbol("eof", TokenType.EOF);

    // Non-Terminal Symbols
    public static final Symbol boolexpr = new Symbol("boolexpr");
    public static final Symbol boolexprPrime = new Symbol("boolexpr'");
    public static final Symbol boolop = new Symbol("boolop");
    public static final Symbol clause = new Symbol("clause");
    public static final Symbol clausePrime = new Symbol("clause'");
    public static final Symbol condstmtend = new Symbol("condstmtend");
    public static final Symbol constNt = new Symbol("const");
    public static final Symbol declseg = new Symbol("declseg");
    public static final Symbol factor = new Symbol("factor");
    public static final Symbol factorPrime = new Symbol("factor'");
    public static final Symbol fullstmt = new Symbol("fullstmt");
    public static final Symbol funcdecl = new Symbol("funcdecl");
    public static final Symbol funcdecls = new Symbol("funcdecls");
    public static final Symbol ids = new Symbol("ids");
    public static final Symbol idsPrime = new Symbol("ids'");
    public static final Symbol linop = new Symbol("linop");
    public static final Symbol neexprs = new Symbol("neexprs");
    public static final Symbol neexprsPrime = new Symbol("neexprs'");
    public static final Symbol neparams = new Symbol("neparams");
    public static final Symbol neparamsPrime = new Symbol("neparams'");
    public static final Symbol nonlinop = new Symbol("nonlinop");
    public static final Symbol numexpr = new Symbol("numexpr");
    public static final Symbol numexprPrime = new Symbol("numexpr'");
    public static final Symbol numexprs = new Symbol("numexprs");
    public static final Symbol optinit = new Symbol("optinit");
    public static final Symbol optoffset = new Symbol("optoffset");
    public static final Symbol optrettype = new Symbol("optrettype");
    public static final Symbol param = new Symbol("param");
    public static final Symbol params = new Symbol("params");
    public static final Symbol pred = new Symbol("pred");
    public static final Symbol program = new Symbol("program");
    public static final Symbol stmt = new Symbol("stmt");
    public static final Symbol stmtPrime = new Symbol("stmt'");
    public static final Symbol stmtPrimePrime = new Symbol("stmt''");
    public static final Symbol stmtPrimePrimePrime = new Symbol("stmt'''");
    public static final Symbol stmts = new Symbol("stmts");
    public static final Symbol stmtsPrime = new Symbol("stmts'");
    public static final Symbol term = new Symbol("term");
    public static final Symbol termPrime = new Symbol("term'");
    public static final Symbol typeNT = new Symbol("type");
    public static final Symbol typedecl = new Symbol("typedecl");
    public static final Symbol typedecls = new Symbol("typedecls");
    public static final Symbol vardecl = new Symbol("vardecl");
    public static final Symbol vardecls = new Symbol("vardecls");

    public static final Map<String, Symbol> TerminalSymbols;
    static {
        var terminals = new HashMap<String, Symbol>();
        terminals.put("array", ARRAY);
        terminals.put("begin", BEGIN);
        terminals.put("break", BREAK);
        terminals.put("do", DO);
        terminals.put("else", ELSE);
        terminals.put("end", END);
        terminals.put("enddo", ENDDO);
        terminals.put("endif", ENDIF);
        terminals.put("float", FLOAT);
        terminals.put("for", FOR);
        terminals.put("func", FUNC);
        terminals.put("if", IF);
        terminals.put("in", IN);
        terminals.put("int", INT);
        terminals.put("let", LET);
        terminals.put("of", OF);
        terminals.put("return", RETURN);
        terminals.put("then", THEN);
        terminals.put("to", TO);
        terminals.put("type", TYPE);
        terminals.put("var", VAR);
        terminals.put("while", WHILE);
        terminals.put(",", COMMA);
        terminals.put(":", COLON);
        terminals.put(";", SEMICOLON);
        terminals.put("(", LEFT_PAREN);
        terminals.put(")", RIGHT_PAREN);
        terminals.put("[", LEFT_BRACKET);
        terminals.put("]", RIGHT_BRACKET);
        terminals.put("{", LEFT_CURLY_BRACE);
        terminals.put("}", RIGHT_CURLY_BRACE);
        terminals.put(".", PERIOD);
        terminals.put("+", PLUS);
        terminals.put("-", MINUS);
        terminals.put("*", STAR);
        terminals.put("/", SLASH);
        terminals.put("=", EQUALS);
        terminals.put("<>", NOT_EQUALS);
        terminals.put("<", LESS_THAN);
        terminals.put(">", GREATER_THAN);
        terminals.put("<=", LESS_OR_EQ);
        terminals.put(">=", GREATER_OR_EQ);
        terminals.put("&", AND);
        terminals.put("|", OR);
        terminals.put(":=", COLON_EQUALS);
        TerminalSymbols = Collections.unmodifiableMap(terminals);
    }
}
