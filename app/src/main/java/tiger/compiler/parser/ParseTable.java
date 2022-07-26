package tiger.compiler.parser;

import java.util.HashMap;
import java.util.Map;
import tiger.compiler.lexer.Token;
import java.util.List;
import java.util.Collections;
import static java.util.Map.entry;
import java.util.Arrays;

public class ParseTable {

    private static Map<Symbol, Map<Symbol, List<Symbol>>> parseTable;
    static {
        var boolexprMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.clause, Symbol.boolexprPrime)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.clause, Symbol.boolexprPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.clause, Symbol.boolexprPrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.clause, Symbol.boolexprPrime))));

        var boolexprPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.DO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.THEN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.OR, Arrays.asList(Symbol.OR, Symbol.clause, Symbol.boolexprPrime))));

        var boolopMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LESS_THAN, Arrays.asList(Symbol.LESS_THAN)),
                entry(Symbol.LESS_OR_EQ, Arrays.asList(Symbol.LESS_OR_EQ)),
                entry(Symbol.NOT_EQUALS, Arrays.asList(Symbol.NOT_EQUALS)),
                entry(Symbol.EQUALS, Arrays.asList(Symbol.EQUALS)),
                entry(Symbol.GREATER_THAN, Arrays.asList(Symbol.GREATER_THAN)),
                entry(Symbol.GREATER_OR_EQ, Arrays.asList(Symbol.GREATER_OR_EQ))));

        var clauseMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.pred, Symbol.clausePrime)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.pred, Symbol.clausePrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.pred, Symbol.clausePrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.pred, Symbol.clausePrime))));

        var clausePrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.AND, Arrays.asList(Symbol.AND, Symbol.pred, Symbol.clausePrime)),
                entry(Symbol.DO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.THEN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.OR, Arrays.asList(Symbol.EPSILON))));

        var condstmtendMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.ELSE, Arrays.asList(Symbol.ELSE, Symbol.stmts, Symbol.ENDIF)),
                entry(Symbol.ENDIF, Arrays.asList(Symbol.ENDIF))));

        var constMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.FLOATLIT)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.INTLIT))));

        var declsegMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FUNC,
                        Arrays.asList(Symbol.typedecls, Symbol.vardecls, Symbol.funcdecls)),
                entry(Symbol.IN,
                        Arrays.asList(Symbol.typedecls, Symbol.vardecls, Symbol.funcdecls)),
                entry(Symbol.TYPE,
                        Arrays.asList(Symbol.typedecls, Symbol.vardecls, Symbol.funcdecls)),
                entry(Symbol.VAR,
                        Arrays.asList(Symbol.typedecls, Symbol.vardecls, Symbol.funcdecls))));

        var factorMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN,
                        Arrays.asList(Symbol.LEFT_PAREN, Symbol.numexpr, Symbol.RIGHT_PAREN)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.constNt)),
                entry(Symbol.ID, Arrays.asList(Symbol.ID, Symbol.factorPrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.constNt))));

        var factorPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.AND, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.STAR, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.PLUS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.COMMA, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.MINUS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.SLASH, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.SEMICOLON, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.NOT_EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LEFT_BRACKET,
                        Arrays.asList(Symbol.LEFT_BRACKET, Symbol.numexpr, Symbol.RIGHT_BRACKET)),
                entry(Symbol.RIGHT_BRACKET, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.DO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.THEN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.TO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.OR, Arrays.asList(Symbol.EPSILON))));

        var fullstmtMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.BREAK, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON)),
                entry(Symbol.FOR, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON)),
                entry(Symbol.ID, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON)),
                entry(Symbol.IF, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON)),
                entry(Symbol.RETURN, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON)),
                entry(Symbol.WHILE, Arrays.asList(Symbol.stmt, Symbol.SEMICOLON))));

        var funcdeclMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FUNC,
                        Arrays.asList(Symbol.FUNC, Symbol.ID, Symbol.LEFT_PAREN, Symbol.params,
                                Symbol.RIGHT_PAREN, Symbol.optrettype, Symbol.BEGIN, Symbol.stmts,
                                Symbol.END, Symbol.SEMICOLON))));

        var funcdeclsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FUNC, Arrays.asList(Symbol.funcdecl, Symbol.funcdecls)),
                entry(Symbol.IN, Arrays.asList(Symbol.EPSILON))));

        var idsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.ID, Arrays.asList(Symbol.ID, Symbol.idsPrime))));

        var idsPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.COMMA, Arrays.asList(Symbol.COMMA, Symbol.ID, Symbol.idsPrime)),
                entry(Symbol.COLON, Arrays.asList(Symbol.EPSILON))));

        var linopMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.PLUS, Arrays.asList(Symbol.PLUS)),
                entry(Symbol.MINUS, Arrays.asList(Symbol.MINUS))));

        var neexprsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.numexpr, Symbol.neexprsPrime)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.numexpr, Symbol.neexprsPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.numexpr, Symbol.neexprsPrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.numexpr, Symbol.neexprsPrime))));

        var neexprsPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.COMMA,
                        Arrays.asList(Symbol.COMMA, Symbol.numexpr, Symbol.neexprsPrime))));

        var neparamsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.ID, Arrays.asList(Symbol.param, Symbol.neparamsPrime))));

        var neparamsPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.COMMA,
                        Arrays.asList(Symbol.COMMA, Symbol.param, Symbol.neparamsPrime))));

        var nonlinopMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.STAR, Arrays.asList(Symbol.STAR)),
                entry(Symbol.SLASH, Arrays.asList(Symbol.SLASH))));

        var numexprMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.term, Symbol.numexprPrime)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.term, Symbol.numexprPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.term, Symbol.numexprPrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.term, Symbol.numexprPrime))));

        var numexprPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.AND, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.PLUS, Arrays.asList(Symbol.linop, Symbol.term, Symbol.numexprPrime)),
                entry(Symbol.COMMA, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.MINUS, Arrays.asList(Symbol.linop, Symbol.term, Symbol.numexprPrime)),
                entry(Symbol.SEMICOLON, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.NOT_EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.RIGHT_BRACKET, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.DO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.THEN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.TO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.OR, Arrays.asList(Symbol.EPSILON))));

        var numexprsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.neexprs)),
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.neexprs)),
                entry(Symbol.ID, Arrays.asList(Symbol.neexprs)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.neexprs))));

        var optinitMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.COLON_EQUALS, Arrays.asList(Symbol.COLON_EQUALS, Symbol.constNt)),
                entry(Symbol.SEMICOLON, Arrays.asList(Symbol.EPSILON))));

        var optoffsetMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.COLON_EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LEFT_BRACKET,
                        Arrays.asList(Symbol.LEFT_BRACKET, Symbol.numexpr, Symbol.RIGHT_BRACKET))));

        var optrettypeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.COLON, Arrays.asList(Symbol.COLON, Symbol.typeNT)),
                entry(Symbol.BEGIN, Arrays.asList(Symbol.EPSILON))));

        var paramMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.ID, Arrays.asList(Symbol.ID, Symbol.COLON, Symbol.typeNT))));

        var paramsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.ID, Arrays.asList(Symbol.neparams))));

        var predMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN,
                        Arrays.asList(Symbol.numexpr, Symbol.boolop, Symbol.numexpr)),
                entry(Symbol.FLOATLIT,
                        Arrays.asList(Symbol.numexpr, Symbol.boolop, Symbol.numexpr)),
                entry(Symbol.ID, Arrays.asList(Symbol.numexpr, Symbol.boolop, Symbol.numexpr)),
                entry(Symbol.INTLIT,
                        Arrays.asList(Symbol.numexpr, Symbol.boolop, Symbol.numexpr))));

        var programMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LET, Arrays.asList(Symbol.LET, Symbol.declseg, Symbol.IN, Symbol.stmts,
                        Symbol.END, Symbol.EOF))));

        var stmtMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.BREAK, Arrays.asList(Symbol.BREAK)),
                entry(Symbol.FOR,
                        Arrays.asList(Symbol.FOR, Symbol.ID, Symbol.COLON_EQUALS, Symbol.numexpr,
                                Symbol.TO, Symbol.numexpr, Symbol.DO, Symbol.stmts, Symbol.ENDDO)),
                entry(Symbol.ID, Arrays.asList(Symbol.ID, Symbol.stmtPrime)),
                entry(Symbol.IF, Arrays.asList(Symbol.IF, Symbol.boolexpr, Symbol.THEN,
                        Symbol.stmts, Symbol.condstmtend)),
                entry(Symbol.RETURN, Arrays.asList(Symbol.RETURN, Symbol.numexpr)),
                entry(Symbol.WHILE, Arrays.asList(Symbol.WHILE, Symbol.boolexpr, Symbol.DO,
                        Symbol.stmts, Symbol.ENDDO))));

        var stmtPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN,
                        Arrays.asList(Symbol.LEFT_PAREN, Symbol.numexprs, Symbol.RIGHT_PAREN)),
                entry(Symbol.COLON_EQUALS,
                        Arrays.asList(Symbol.optoffset, Symbol.COLON_EQUALS,
                                Symbol.stmtPrimePrime)),
                entry(Symbol.LEFT_BRACKET,
                        Arrays.asList(Symbol.optoffset, Symbol.COLON_EQUALS,
                                Symbol.stmtPrimePrime))));

        var stmtPrimePrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN,
                        Arrays.asList(Symbol.LEFT_PAREN, Symbol.numexpr, Symbol.RIGHT_PAREN,
                                Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.FLOATLIT,
                        Arrays.asList(Symbol.constNt, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.ID, Symbol.stmtPrimePrimePrime)),
                entry(Symbol.INTLIT,
                        Arrays.asList(Symbol.constNt, Symbol.termPrime, Symbol.numexprPrime))));

        var stmtPrimePrimePrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN,
                        Arrays.asList(Symbol.LEFT_PAREN, Symbol.numexprs, Symbol.RIGHT_PAREN)),
                entry(Symbol.STAR,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.PLUS,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.MINUS,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.SLASH,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.SEMICOLON,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime)),
                entry(Symbol.LEFT_BRACKET,
                        Arrays.asList(Symbol.factorPrime, Symbol.termPrime, Symbol.numexprPrime))));

        var stmtsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.BREAK, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.FOR, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.IF, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.RETURN, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.WHILE, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime))));

        var stmtsPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.BREAK, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.ELSE, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.END, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.ENDDO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.ENDIF, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.FOR, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.IF, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.RETURN, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime)),
                entry(Symbol.WHILE, Arrays.asList(Symbol.fullstmt, Symbol.stmtsPrime))));

        var termMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.LEFT_PAREN, Arrays.asList(Symbol.factor, Symbol.termPrime)),
                entry(Symbol.FLOATLIT, Arrays.asList(Symbol.factor, Symbol.termPrime)),
                entry(Symbol.ID, Arrays.asList(Symbol.factor, Symbol.termPrime)),
                entry(Symbol.INTLIT, Arrays.asList(Symbol.factor, Symbol.termPrime))));

        var termPrimeMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.AND, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.RIGHT_PAREN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.STAR, Arrays.asList(Symbol.nonlinop, Symbol.factor, Symbol.termPrime)),
                entry(Symbol.PLUS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.COMMA, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.MINUS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.SLASH,
                        Arrays.asList(Symbol.nonlinop, Symbol.factor, Symbol.termPrime)),
                entry(Symbol.SEMICOLON, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.LESS_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.NOT_EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.EQUALS, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_THAN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.GREATER_OR_EQ, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.RIGHT_BRACKET, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.DO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.THEN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.TO, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.OR, Arrays.asList(Symbol.EPSILON))));

        var typeNTMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.ARRAY,
                        Arrays.asList(Symbol.ARRAY, Symbol.LEFT_BRACKET, Symbol.INTLIT,
                                Symbol.RIGHT_BRACKET, Symbol.OF, Symbol.typeNT)),
                entry(Symbol.FLOAT, Arrays.asList(Symbol.FLOAT)),
                entry(Symbol.ID, Arrays.asList(Symbol.ID)),
                entry(Symbol.INT, Arrays.asList(Symbol.INT))));

        var typedeclMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.TYPE, Arrays.asList(Symbol.TYPE, Symbol.ID, Symbol.COLON_EQUALS,
                        Symbol.typeNT, Symbol.SEMICOLON))));

        var typedeclsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FUNC, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.IN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.TYPE, Arrays.asList(Symbol.typedecl, Symbol.typedecls)),
                entry(Symbol.VAR, Arrays.asList(Symbol.EPSILON))));

        var vardeclMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.VAR, Arrays.asList(Symbol.VAR, Symbol.ids, Symbol.COLON,
                        Symbol.typeNT, Symbol.optinit, Symbol.SEMICOLON))));

        var vardeclsMap = new HashMap<Symbol, List<Symbol>>(Map.ofEntries(
                entry(Symbol.FUNC, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.IN, Arrays.asList(Symbol.EPSILON)),
                entry(Symbol.VAR, Arrays.asList(Symbol.vardecl, Symbol.vardecls))));

        var table = new HashMap<Symbol, HashMap<Symbol, List<Symbol>>>();
        table.put(Symbol.boolexpr, boolexprMap);
        table.put(Symbol.boolexprPrime, boolexprPrimeMap);
        table.put(Symbol.boolop, boolopMap);
        table.put(Symbol.clause, clauseMap);
        table.put(Symbol.clausePrime, clausePrimeMap);
        table.put(Symbol.condstmtend, condstmtendMap);
        table.put(Symbol.constNt, constMap);
        table.put(Symbol.declseg, declsegMap);
        table.put(Symbol.factor, factorMap);
        table.put(Symbol.factorPrime, factorPrimeMap);
        table.put(Symbol.fullstmt, fullstmtMap);
        table.put(Symbol.funcdecl, funcdeclMap);
        table.put(Symbol.funcdecls, funcdeclsMap);
        table.put(Symbol.ids, idsMap);
        table.put(Symbol.idsPrime, idsPrimeMap);
        table.put(Symbol.linop, linopMap);
        table.put(Symbol.neexprs, neexprsMap);
        table.put(Symbol.neexprsPrime, neexprsPrimeMap);
        table.put(Symbol.neparams, neparamsMap);
        table.put(Symbol.neparamsPrime, neparamsPrimeMap);
        table.put(Symbol.nonlinop, nonlinopMap);
        table.put(Symbol.numexpr, numexprMap);
        table.put(Symbol.numexprPrime, numexprPrimeMap);
        table.put(Symbol.numexprs, numexprsMap);
        table.put(Symbol.optinit, optinitMap);
        table.put(Symbol.optoffset, optoffsetMap);
        table.put(Symbol.optrettype, optrettypeMap);
        table.put(Symbol.param, paramMap);
        table.put(Symbol.params, paramsMap);
        table.put(Symbol.pred, predMap);
        table.put(Symbol.program, programMap);
        table.put(Symbol.stmt, stmtMap);
        table.put(Symbol.stmtPrime, stmtPrimeMap);
        table.put(Symbol.stmtPrimePrime, stmtPrimePrimeMap);
        table.put(Symbol.stmtPrimePrimePrime, stmtPrimePrimePrimeMap);
        table.put(Symbol.stmts, stmtsMap);
        table.put(Symbol.stmtsPrime, stmtsPrimeMap);
        table.put(Symbol.term, termMap);
        table.put(Symbol.termPrime, termPrimeMap);
        table.put(Symbol.typeNT, typeNTMap);
        table.put(Symbol.typedecl, typedeclMap);
        table.put(Symbol.typedecls, typedeclsMap);
        table.put(Symbol.vardecl, vardeclMap);
        table.put(Symbol.typeNT, typeNTMap);
        table.put(Symbol.vardecls, vardeclsMap);
        parseTable = Collections.unmodifiableMap(table);
    }

    public static List<Symbol> get(Symbol stackSymbol, Token currToken) {
        if (!parseTable.containsKey(stackSymbol)) {
            return null;
        }
        return parseTable.get(stackSymbol).get(currToken.getTerminalSymbol());
    }
}
