package tiger.compiler.parser;

import java.util.List;

import tiger.compiler.lexer.Token;
import tiger.compiler.lexer.TokenType;

public class Parser {
    private List<Token> tokenList;
    private int currentTokenNum;
    private ASTNode root;

    public Parser(List<Token> tokenList) throws ParseException {

        // Check if empty file (only EOF token)
        if (tokenList.size() <= 1) {
            throw new ParseException("Cannot parse empty file");
        }

        this.tokenList = tokenList;
        this.currentTokenNum = 0;
        this.root = new ASTNode(Symbol.program);
    }

    /**
     * Parses the list of tokens into an AST using an LL(1) parse table.
     *
     * @throws ParseException if the input program does not parse successfully.
     */
    public void parse() throws ParseException {
        this.parse(Symbol.program, this.root);

        // Convert parse tree into an AST
        // An AST abstracts away the intermediate non-terminals that were useful for
        // parsing but are not useful for the remaining compilation phases
        // fixTree();
    }

    private void parse(Symbol symbol, ASTNode currNode) throws ParseException {
        var currToken = this.tokenList.get(this.currentTokenNum);
        if (symbol.isTerminal()) {

            // If token doesn't match expected terminal, parse error.
            if (symbol != currToken.getTerminalSymbol() && symbol != Symbol.EPSILON) {
                throw new ParseException("Expected \"" + symbol + "\" but found \""
                        + currToken.getTerminalSymbol() + "\"");
            }

            // Advance token if non-epsilon terminal
            if (symbol != Symbol.EPSILON) {
                this.currentTokenNum++;
            }

            // Add values to current node if relevant
            if (symbol == Symbol.ID || symbol == Symbol.INTLIT || symbol == Symbol.FLOATLIT) {
                currNode.setValue(currToken.getLexeme());
            }
            return;
        }

        // symbol is non-terminal, check parse table for derivation
        var rhsSymbols = ParseTable.get(symbol, currToken);
        if (rhsSymbols == null) {
            throw new ParseException(
                    "No rule to parse \"" + symbol + "\" on input \"" + currToken + "\"");
        }

        // Recursively parse each symbol from the derivation
        for (var rhsSymbol : rhsSymbols) {
            if (rhsSymbol == Symbol.EOF) {
                continue;
            }

            var childNode = currNode.addChild(rhsSymbol);
            this.parse(rhsSymbol, childNode);
        }
    }

    public String getAST() {
        return this.inOrder(this.root).toString();
    }

    private StringBuilder inOrder(ASTNode current) {
        var currSymbol = current.getSymbol();
        if (currSymbol.isTerminal()) {
            if (currSymbol == Symbol.EPSILON) {
                return new StringBuilder();
            } else if (currSymbol.getTerminalType() == TokenType.KEYWORD) {
                return new StringBuilder(current.getSymbol().toString());
            } else {
                return new StringBuilder(current.getValue());
            }
        }

        var children = current.getDerivation();
        StringBuilder x = new StringBuilder();

        var onlyChildIsEpsilon =
                children.size() == 1 && children.get(0).getSymbol() == Symbol.EPSILON;

        if (onlyChildIsEpsilon) {
            x.append(current.toString());
        } else {
            x.append("(");
            x.append(current.toString());
            x.append(" ");
        }

        for (var i = 0; i < children.size() - 1; i++) {
            if (children.get(i).getSymbol() != Symbol.EPSILON) {
                x.append(this.inOrder(children.get(i)));
                x.append(" ");
            }
        }

        var lastChild = children.get(children.size() - 1);
        if (lastChild.getSymbol() != Symbol.EPSILON) {
            x.append(this.inOrder(children.get(children.size() - 1)));
        }

        if (!onlyChildIsEpsilon) {
            x.append(")");
        }
        return x;
    }
}
