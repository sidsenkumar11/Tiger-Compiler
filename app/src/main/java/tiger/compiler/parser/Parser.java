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
        this.convertToAST();
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

        var onlyChildIsEpsilon = children.size() == 1 && children.get(0).getSymbol() == Symbol.EPSILON;

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

    private void convertToAST() {
        this.removeStmtPrimePrimePrime(this.root);
        this.removeStmtPrimePrime(this.root);
        this.removeStmtPrime(this.root);
        this.createLvalue(this.root);
        this.createOptstore(this.root);
        this.removeIdsPrime(this.root);
        this.removeNeparamsPrime(this.root);
        this.removeStmtsPrime(this.root);
        this.removeNeexprsPrime(this.root);
        this.removeBoolexprPrime(this.root);
        this.removeClausePrime(this.root);
        this.removeNumexprPrime(this.root);
        this.removeTermPrime(this.root);
        this.removeFactorPrime(this.root);
        this.removeCondStmtEnd(this.root);
    }

    private void removeIdsPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeIdsPrime(node);
        }

        // Simplify primes generated by ids'
        if (current.getSymbol() == Symbol.idsPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - ids' -> , id ids'
                // Simplify to:
                // - ids' -> , ids
                // - ids -> id OR ids -> id , ids
                var childPrime = current.removeLast();
                var idNode = current.removeLast();
                var newParent = current.addChild(Symbol.ids);
                newParent.addChild(idNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by ids
        if (current.getSymbol() == Symbol.ids) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeNeparamsPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeNeparamsPrime(node);
        }

        // Simplify primes generated by neparams'
        if (current.getSymbol() == Symbol.neparamsPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - neparams' -> , param neparams'
                // Simplify to:
                // - neparams' -> , neparams
                // - neparams -> param OR neparams -> param , param
                var childPrime = current.removeLast();
                var paramNode = current.removeLast();
                var newParent = current.addChild(Symbol.neparams);
                newParent.addChild(paramNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by neparams
        if (current.getSymbol() == Symbol.neparams) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeStmtsPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeStmtsPrime(node);
        }

        // Simplify primes generated by stmts'
        if (current.getSymbol() == Symbol.stmtsPrime) {
            if (current.childCount() == 2) {

                // Given:
                // - stmts' -> fullstmt stmts'
                // Simplify to:
                // - stmts' -> stmts
                // - stmts -> fullstmt OR stmts -> fullstmt stmts
                var childPrime = current.removeLast();
                var fullstmtNode = current.removeLast();
                var newParent = current.addChild(Symbol.stmts);
                newParent.addChild(fullstmtNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by stmts
        if (current.getSymbol() == Symbol.stmts) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeNeexprsPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeNeexprsPrime(node);
        }

        // Simplify primes generated by neexprs'
        if (current.getSymbol() == Symbol.neexprsPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - neexprs' -> , numexpr neexprs'
                // Simplify to:
                // - neexprs' -> , neexprs
                // - neexprs -> numexpr OR neexprs -> numexpr , numexpr
                var childPrime = current.removeLast();
                var numexprNode = current.removeLast();
                var newParent = current.addChild(Symbol.neexprs);
                newParent.addChild(numexprNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by neexprs
        if (current.getSymbol() == Symbol.neexprs) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeBoolexprPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeBoolexprPrime(node);
        }

        // Simplify primes generated by boolexpr'
        if (current.getSymbol() == Symbol.boolexprPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - boolexpr' -> | clause boolexpr'
                // Simplify to:
                // - boolexpr' -> | boolexpr
                // - boolexpr -> clause OR boolexpr -> clause | clause
                var childPrime = current.removeLast();
                var clauseNode = current.removeLast();
                var newParent = current.addChild(Symbol.boolexpr);
                newParent.addChild(clauseNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by boolexpr
        if (current.getSymbol() == Symbol.boolexpr) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeClausePrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeClausePrime(node);
        }

        // Simplify primes generated by clause'
        if (current.getSymbol() == Symbol.clausePrime) {
            if (current.childCount() == 3) {

                // Given:
                // - clause' -> & pred clause'
                // Simplify to:
                // - clause' -> & clause
                // - clause -> pred OR clause -> pred & pred
                var childPrime = current.removeLast();
                var predNode = current.removeLast();
                var newParent = current.addChild(Symbol.clause);
                newParent.addChild(predNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by clause
        if (current.getSymbol() == Symbol.clause) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeNumexprPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeNumexprPrime(node);
        }

        // Simplify primes generated by numexpr'
        if (current.getSymbol() == Symbol.numexprPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - numexpr' -> linop term numexpr'
                // Simplify to:
                // - numexpr' -> linop numexpr
                // - numexpr -> term OR numexpr -> term linop term
                var childPrime = current.removeLast();
                var termNode = current.removeLast();
                var newParent = current.addChild(Symbol.numexpr);
                newParent.addChild(termNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by numexpr
        if (current.getSymbol() == Symbol.numexpr) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeTermPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeTermPrime(node);
        }

        // Simplify primes generated by term'
        if (current.getSymbol() == Symbol.termPrime) {
            if (current.childCount() == 3) {

                // Given:
                // - term' -> nonlinop factor term'
                // Simplify to:
                // - term' -> nonlinop term
                // - term -> factor OR term -> factor nonlinop factor
                var childPrime = current.removeLast();
                var factorNode = current.removeLast();
                var newParent = current.addChild(Symbol.term);
                newParent.addChild(factorNode);
                for (var grandChild : childPrime.getDerivation()) {
                    if (grandChild.getSymbol() != Symbol.EPSILON) {
                        newParent.addChild(grandChild);
                    }
                }
            }
        }

        // Simplify primes generated by term
        if (current.getSymbol() == Symbol.term) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeFactorPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeFactorPrime(node);
        }

        // Simplify primes generated by factor
        if (current.getSymbol() == Symbol.factor
                && current.getLast().getSymbol() == Symbol.factorPrime) {
            current.mergeLastNodeChildren();
        }
    }

    private void removeConst(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeConst(node);
        }

        var derivation = current.getDerivation();
        for (var i = 0; i < derivation.size(); i++) {
            var child = derivation.get(i);
            if (child.getSymbol() == Symbol.constNt) {
                derivation.set(i, child.getLast());
            }
        }
    }

    private void removeCondStmtEnd(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeCondStmtEnd(node);
        }

        if (current.getSymbol() == Symbol.stmt) {
            var derivation = current.getDerivation();
            if (derivation.size() == 5 && derivation.get(4).getSymbol() == Symbol.condstmtend) {
                current.mergeLastNodeChildren();
            }
        }
    }

    private void removeStmtPrimePrimePrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeStmtPrimePrimePrime(node);
        }

        if (current.getSymbol() == Symbol.stmtPrimePrime) {
            var derivation = current.getDerivation();
            if (derivation.size() == 2 && derivation.get(1).getSymbol() == Symbol.stmtPrimePrimePrime) {
                current.mergeLastNodeChildren();
            }
        }
    }

    private void removeStmtPrimePrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeStmtPrimePrime(node);
        }

        if (current.getSymbol() == Symbol.stmtPrime) {
            if (current.getDerivation().size() == 3 && current.getLast().getSymbol() == Symbol.stmtPrimePrime) {
                var childNode = current.getLast();
                var childDerivation = childNode.getDerivation();
                if (childDerivation.size() == 4 && childNode.getLast().getSymbol() == Symbol.RIGHT_PAREN) {
                    childNode.mergeLastNodeChildren();
                } else {
                    // expanded numexpr to be merged
                    var factorNode = new ASTNode(Symbol.factor);
                    var termNode = new ASTNode(Symbol.term);
                    var numexprNode = new ASTNode(Symbol.numexpr);

                    if (childDerivation.get(0).getSymbol() == Symbol.constNt) {
                        factorNode.addChild(childDerivation.get(0));
                        termNode.addChild(factorNode);
                        termNode.addChild(childDerivation.get(1));
                        numexprNode.addChild(termNode);
                        numexprNode.addChild(childDerivation.get(2));
                    } else if (childDerivation.get(0).getSymbol() == Symbol.LEFT_PAREN) {
                        factorNode.addChild(childDerivation.get(0));
                        factorNode.addChild(childDerivation.get(1));
                        factorNode.addChild(childDerivation.get(2));
                        termNode.addChild(factorNode);
                        termNode.addChild(childDerivation.get(3));
                        numexprNode.addChild(termNode);
                        numexprNode.addChild(childDerivation.get(4));
                    } else if (childDerivation.get(0).getSymbol() == Symbol.ID) {
                        factorNode.addChild(childDerivation.get(0));
                        factorNode.addChild(childDerivation.get(1));
                        termNode.addChild(factorNode);
                        termNode.addChild(childDerivation.get(2));
                        numexprNode.addChild(termNode);
                        numexprNode.addChild(childDerivation.get(3));
                    }
                }
            }
        }
    }

    private void removeStmtPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeStmtPrime(node);
        }

        if (current.getSymbol() == Symbol.stmt) {
            var derivation = current.getDerivation();
            if (derivation.size() == 2 && derivation.get(1).getSymbol() == Symbol.stmtPrime) {
                current.mergeLastNodeChildren();
            }
        }
    }

    private void createLvalue(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.createLvalue(node);
        }

        if (current.getSymbol() == Symbol.stmt) {
            var derivation = current.getDerivation();
            if (derivation.size() > 2 && derivation.get(0).getSymbol() == Symbol.ID
                    && derivation.get(1).getSymbol() == Symbol.optoffset) {
                var lvalueNode = new ASTNode(Symbol.lvalue);
                lvalueNode.addChild(derivation.remove(0));
                lvalueNode.addChild(derivation.remove(0));
                derivation.add(0, lvalueNode);
            }
        }
    }

    private void createOptstore(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.createOptstore(node);
        }

        if (current.getSymbol() == Symbol.stmt) {
            var derivation = current.getDerivation();
            if (derivation.get(0).getSymbol() == Symbol.ID) {
                var optstoreNode = new ASTNode(Symbol.optstore);
                optstoreNode.addChild(Symbol.EPSILON);
                derivation.add(0, optstoreNode);
            } else if (derivation.size() == 6 && derivation.get(0).getSymbol() == Symbol.lvalue
                    && derivation.get(1).getSymbol() == Symbol.COLON_EQUALS
                    && derivation.get(2).getSymbol() == Symbol.ID && derivation.get(3).getSymbol() == Symbol.LEFT_PAREN
                    && derivation.get(4).getSymbol() == Symbol.numexprs
                    && derivation.get(5).getSymbol() == Symbol.RIGHT_PAREN) {
                var optstoreNode = new ASTNode(Symbol.optstore);
                optstoreNode.addChild(derivation.remove(0));
                optstoreNode.addChild(derivation.remove(0));
                derivation.add(0, optstoreNode);
            }
        }
    }
}
