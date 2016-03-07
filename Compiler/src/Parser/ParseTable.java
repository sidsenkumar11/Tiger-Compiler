import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by Siddarth on 3/3/2016.
 */
public class ParseTable {

    private Hashtable<Integer, LinkedList<LinkedList<Symbol>>> parseTable;
    private HashMap<String, Symbol> symbolList;
    public ParseTable(String file) {
        parseTable = new Hashtable<Integer, LinkedList<LinkedList<Symbol>>>();
        symbolList = new HashMap<String, Symbol>();
        generateSymbolList();
        generateParseTable(file);
    }

    private void generateSymbolList() {
        // Create all terminal symbols
        Symbol let = new Symbol(true, "let");
        Symbol in = new Symbol(true, "in");
        Symbol end = new Symbol(true, "end");
        Symbol id = new Symbol(true, "id");
        Symbol colonEquals = new Symbol(true, ":=");
        Symbol semicolon = new Symbol(true, ";");
        Symbol integer = new Symbol(true, "int");
        Symbol floating = new Symbol(true, "float");
        Symbol array = new Symbol(true, "array");
        Symbol leftBracket = new Symbol(true, "[");
        Symbol intlit = new Symbol(true, "intlit");
        Symbol rightBracket = new Symbol(true, "]");
        Symbol of = new Symbol(true, "of");
        Symbol var = new Symbol(true, "var");
        Symbol colon = new Symbol(true, ":");
        Symbol comma = new Symbol(true, "?");
        Symbol func = new Symbol(true, "func");
        Symbol leftParen = new Symbol(true, "(");
        Symbol rightParen = new Symbol(true, ")");
        Symbol begin = new Symbol(true, "begin");
        Symbol ifs = new Symbol(true, "if");
        Symbol then = new Symbol(true, "then");
        Symbol endif = new Symbol(true, "endif");
        Symbol elses = new Symbol(true, "else");
        Symbol whiles = new Symbol(true, "while");
        Symbol dos = new Symbol(true, "do");
        Symbol enddo = new Symbol(true, "enddo");
        Symbol fors = new Symbol(true, "for");
        Symbol to = new Symbol(true, "to");
        Symbol breaks = new Symbol(true, "break");
        Symbol returns = new Symbol(true, "return");
        Symbol pipe = new Symbol(true, "|");
        Symbol ampersand = new Symbol(true, "&");
        Symbol equals = new Symbol(true, "=");
        Symbol lessGreater = new Symbol(true, "<>");
        Symbol lessEqual = new Symbol(true, "<=");
        Symbol greaterEqual = new Symbol(true, ">=");
        Symbol less = new Symbol(true, "<");
        Symbol greater = new Symbol(true, ">");
        Symbol plus = new Symbol(true, "+");
        Symbol minus = new Symbol(true, "-");
        Symbol star = new Symbol(true, "*");
        Symbol divide = new Symbol(true, "/");
        Symbol floatlit = new Symbol(true, "floatlit");
        Symbol dollar = new Symbol(true, "$");
        Symbol epsilon = new Symbol(true, "''");
        Symbol nullSymbol = new Symbol(true, "NULL");

        // Create all non-termnal symbols
        Symbol program = new Symbol(false, "program");
        Symbol declseg = new Symbol(false, "declseg");
        Symbol typedecls = new Symbol(false, "typedecls");
        Symbol typedecl = new Symbol(false, "typedecl");
        Symbol type = new Symbol(false, "type");
        Symbol vardecls = new Symbol(false, "vardecls");
        Symbol vardecl = new Symbol(false, "vardecl");
        Symbol ids = new Symbol(false, "ids");
        Symbol idsPrime = new Symbol(false, "ids'");
        Symbol optinit = new Symbol(false, "optinit");
        Symbol funcdecls = new Symbol(false, "funcdecls");
        Symbol funcdecl = new Symbol(false, "funcdecl");
        Symbol params = new Symbol(false, "params");
        Symbol neparams = new Symbol(false, "neparams");
        Symbol neparamsPrime = new Symbol(false, "neparams'");
        Symbol param = new Symbol(false, "param");
        Symbol optrettype = new Symbol(false, "optrettype");
        Symbol stmts = new Symbol(false, "stmts");
        Symbol fullstmt = new Symbol(false, "fullstmt");
        Symbol stmt = new Symbol(false, "stmt");
        Symbol stmtPrime = new Symbol(false, "stmt'");
        Symbol lvalue = new Symbol(false, "lvalue");
        Symbol optoffset = new Symbol(false, "optoffset");
        Symbol optstore = new Symbol(false, "optstore");
        Symbol numexprs = new Symbol(false, "numexprs");
        Symbol neexprs = new Symbol(false, "neexprs");
        Symbol neexprsPrime = new Symbol(false, "neexprs'");
        Symbol boolexpr = new Symbol(false, "boolexpr");
        Symbol boolexprPrime = new Symbol(false, "boolexpr'");
        Symbol clause = new Symbol(false, "clause");
        Symbol clausePrime = new Symbol(false, "clause'");
        Symbol pred = new Symbol(false, "pred");
        Symbol boolop = new Symbol(false, "boolop");
        Symbol numexpr = new Symbol(false, "numexpr");
        Symbol numexprPrime = new Symbol(false, "numexpr'");
        Symbol linop = new Symbol(false, "linop");
        Symbol term = new Symbol(false, "term");
        Symbol termPrime = new Symbol(false, "term'");
        Symbol nonlinop = new Symbol(false, "nonlinop");
        Symbol factor = new Symbol(false, "factor");
        Symbol factorPrime = new Symbol(false, "factor'");
        Symbol consts = new Symbol(false, "const");

        // Add them all to symbol list

        symbolList.put("NULL", nullSymbol);
        symbolList.put("let", let);
        symbolList.put("in", in);
        symbolList.put("end", end);
        symbolList.put("id", id);
        symbolList.put(":=", colonEquals);
        symbolList.put(";", semicolon);
        symbolList.put("int", integer);
        symbolList.put("float", floating);
        symbolList.put("array", array);
        symbolList.put("[", leftBracket);
        symbolList.put("intlit", intlit);
        symbolList.put("]", rightBracket);
        symbolList.put("of", of);
        symbolList.put("var", var);
        symbolList.put(":", colon);
        symbolList.put("?", comma);
        symbolList.put("func", func);
        symbolList.put("(", leftParen);
        symbolList.put(")", rightParen);
        symbolList.put("begin", begin);
        symbolList.put("if", ifs);
        symbolList.put("then", then);
        symbolList.put("endif", endif);
        symbolList.put("else", elses);
        symbolList.put("while", whiles);
        symbolList.put("do", dos);
        symbolList.put("enddo", enddo);
        symbolList.put("for", fors);
        symbolList.put("to", to);
        symbolList.put("break", breaks);
        symbolList.put("return", returns);
        symbolList.put("|", pipe);
        symbolList.put("&", ampersand);
        symbolList.put("=", equals);
        symbolList.put("<>", lessGreater);
        symbolList.put("<=", lessEqual);
        symbolList.put(">=", greaterEqual);
        symbolList.put("<", less);
        symbolList.put(">", greater);
        symbolList.put("+", plus);
        symbolList.put("-", minus);
        symbolList.put("*", star);
        symbolList.put("/", divide);
        symbolList.put("floatlit", floatlit);
        symbolList.put("$", dollar);
        symbolList.put("''", epsilon);
        symbolList.put("program", program);
        symbolList.put("declseg", declseg);
        symbolList.put("typedecls", typedecls);
        symbolList.put("typedecl", typedecl);
        symbolList.put("type", type);
        symbolList.put("vardecls", vardecls);
        symbolList.put("vardecl", vardecl);
        symbolList.put("ids", ids);
        symbolList.put("ids'", idsPrime);
        symbolList.put("optinit", optinit);
        symbolList.put("funcdecls", funcdecls);
        symbolList.put("funcdecl", funcdecl);
        symbolList.put("params", params);
        symbolList.put("neparams", neparams);
        symbolList.put("neparams'", neparamsPrime);
        symbolList.put("param", param);
        symbolList.put("optrettype", optrettype);
        symbolList.put("stmts", stmts);
        symbolList.put("fullstmt", fullstmt);
        symbolList.put("stmt", stmt);
        symbolList.put("stmt'", stmtPrime);
        symbolList.put("lvalue", lvalue);
        symbolList.put("optoffset", optoffset);
        symbolList.put("optstore", optstore);
        symbolList.put("numexprs", numexprs);
        symbolList.put("neexprs", neexprs);
        symbolList.put("neexprs'", neexprsPrime);
        symbolList.put("boolexpr", boolexpr);
        symbolList.put("boolexpr'", boolexprPrime);
        symbolList.put("clause", clause);
        symbolList.put("clause'", clausePrime);
        symbolList.put("pred", pred);
        symbolList.put("boolop", boolop);
        symbolList.put("numexpr", numexpr);
        symbolList.put("numexpr'", numexprPrime);
        symbolList.put("linop", linop);
        symbolList.put("term", term);
        symbolList.put("term'", termPrime);
        symbolList.put("nonlinop", nonlinop);
        symbolList.put("factor", factor);
        symbolList.put("factor'", factorPrime);
        symbolList.put("const", consts);
    }

    public void generateParseTable(String file) {

        // Read in file containing parse table
        // Note that question mark means comma from csv file
        // zzz separates multiple entries for the same cell
        String[] rows = readIn(file);
        String[] columns = rows[0].split(",");

        for (int i = 1; i < rows.length; i++) {
            String[] row = rows[i].split(",");
            String firstArgument = row[0];
            for (int j = 1; j < row.length; j++) {
                if (!row[j].equals("NULL")) {
                    LinkedList<LinkedList<Symbol>> netEntry = new LinkedList<>();
                    // Handle multiple entries per cell.
                    String[] multipleEntries = row[j].split("zzz");
                    for (String x : multipleEntries) {
                        String[] singleEntry = x.split(" -> ");
                        String[] actualEntry = singleEntry[1].split(" ");
                        netEntry.add(createEntry(actualEntry));
                    }
                    set(getSymbol(firstArgument), getSymbol(columns[j]), netEntry);
                }
            }
        }
    }

    private LinkedList<Symbol> createEntry(String[] entry) {
        LinkedList<Symbol> list = new LinkedList<Symbol>();
        for (int i = 0; i < entry.length; i++) {
            list.add(getSymbol(entry[i]));
        }
        return list;
    }

    private Symbol getSymbol(String symbolText) {
        return symbolList.get(symbolText);
    }

    private void set(Symbol row, Symbol col, LinkedList<LinkedList<Symbol>> entry) {
        SymbolArray key = new SymbolArray(row, col);
        parseTable.put(key.hashCode(), entry);
    }

    /**
     * Gets the expanded symbol list for this stack value on this input token.
     * @param stackSymbol The current stack symbol.
     * @param nextTokenSymbol The next input token.
     * @return The expanded symbol list for this input token on this stack value.
     */
    public LinkedList<LinkedList<Symbol>> get(Symbol stackSymbol, Symbol nextTokenSymbol) {
        SymbolArray key = new SymbolArray(stackSymbol, nextTokenSymbol);
        return parseTable.get(key.hashCode());
    }

    /**
     * Tells whether there is a definition for this stack value on this input token.
     * @param stackSymbol The current stack symbol.
     * @param nextTokenSymbol The next input token.
     * @return If there is a definition for this stack value on this input token.
     */
    public boolean isEmpty(Symbol stackSymbol, Symbol nextTokenSymbol) {
        return get(stackSymbol, nextTokenSymbol) == null;
    }

    private String[] readIn(String fileName) {
        String fullFileText = "";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                fullFileText += line + "\n";
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
            // ex.printStackTrace();
        }

        return fullFileText.split("\n");
    }

    public String toString() {
        return parseTable.toString();
    }
}
