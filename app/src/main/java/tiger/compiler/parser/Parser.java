package tiger.compiler.parser;

import java.util.List;

import tiger.compiler.lexer.Token;

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
    public ASTNode parse() throws ParseException {
        this.parse(Symbol.program, this.root);

        // Convert parse tree into an AST
        // An AST abstracts away the intermediate non-terminals that were useful for
        // parsing but are not useful for the remaining compilation phases
        this.convertToAST();
        return this.root;
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
                currNode.setValue(currToken.getLexeme().toLowerCase());
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

        if (current.getSymbol() == Symbol.boolexpr) {

            // Transform left-recursive tree to right-recursive tree
            while (current.getLast().childCount() != 1) {
                var derivation = current.getDerivation();

                if (derivation.size() == 2) {
                    var newFirstNode = new ASTNode(Symbol.boolexpr);
                    newFirstNode.addChild(derivation.get(0));
                    derivation.set(0, newFirstNode);
                    current.mergeLastNodeChildren();
                } else {
                    // boolexpr1 -> boolexpr2 |1 clause1 boolexpr'1
                    // boolexpr'1 -> |2 clause2 boolexpr'2
                    // becomes
                    // boolexpr1 -> boolexpr3 |2 clause2 boolexpr'2
                    // boolexpr3 -> boolexpr2 |1 clause1
                    var newFirstNode = new ASTNode(Symbol.boolexpr);
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    derivation.add(0, newFirstNode);
                    current.mergeLastNodeChildren();
                }
            }

            // Remove the final epsilon boolexpr'
            current.removeLast();
        }
    }

    private void removeClausePrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeClausePrime(node);
        }

        if (current.getSymbol() == Symbol.clause) {

            // Transform left-recursive tree to right-recursive tree
            while (current.getLast().childCount() != 1) {
                var derivation = current.getDerivation();

                if (derivation.size() == 2) {
                    var newFirstNode = new ASTNode(Symbol.clause);
                    newFirstNode.addChild(derivation.get(0));
                    derivation.set(0, newFirstNode);
                    current.mergeLastNodeChildren();
                } else {
                    // clause1 -> clause2 &1 pred1 clause'1
                    // clause'1 -> &2 pred2 clause'2
                    // becomes
                    // clause1 -> clause3 &2 pred2 clause'2
                    // clause3 -> clause2 &1 pred1
                    var newFirstNode = new ASTNode(Symbol.clause);
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    derivation.add(0, newFirstNode);
                    current.mergeLastNodeChildren();
                }
            }

            // Remove the final epsilon clause'
            current.removeLast();
        }
    }

    private void removeNumexprPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeNumexprPrime(node);
        }

        if (current.getSymbol() == Symbol.numexpr) {

            // Transform left-recursive tree to right-recursive tree
            while (current.getLast().childCount() != 1) {
                var derivation = current.getDerivation();

                if (derivation.size() == 2) {
                    var newFirstNode = new ASTNode(Symbol.numexpr);
                    newFirstNode.addChild(derivation.get(0));
                    derivation.set(0, newFirstNode);
                    current.mergeLastNodeChildren();
                } else {
                    // numexpr1 -> numexpr2 linop1 term1 numexpr'1
                    // numexpr'1 -> linop2 term2 numexpr'2
                    // becomes
                    // numexpr1 -> numexpr3 linop2 term2 numexpr'2
                    // numexpr3 -> numexpr2 linop1 term1
                    var newFirstNode = new ASTNode(Symbol.numexpr);
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    derivation.add(0, newFirstNode);
                    current.mergeLastNodeChildren();
                }
            }

            // Remove the final epsilon numexpr'
            current.removeLast();
        }
    }

    private void removeTermPrime(ASTNode current) {
        for (var node : current.getDerivation()) {
            this.removeTermPrime(node);
        }

        if (current.getSymbol() == Symbol.term) {

            // Transform left-recursive tree to right-recursive tree
            while (current.getLast().childCount() != 1) {
                var derivation = current.getDerivation();

                if (derivation.size() == 2) {
                    var newFirstNode = new ASTNode(Symbol.term);
                    newFirstNode.addChild(derivation.get(0));
                    derivation.set(0, newFirstNode);
                    current.mergeLastNodeChildren();
                } else {
                    // term1 -> term2 nonlinop1 factor1 term'1
                    // term'1 -> nonlinop2 factor2 term'2
                    // becomes
                    // term1 -> term3 nonlinop2 factor2 term'2
                    // term3 -> term2 nonlinop1 factor1
                    var newFirstNode = new ASTNode(Symbol.term);
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    newFirstNode.addChild(derivation.remove(0));
                    derivation.add(0, newFirstNode);
                    current.mergeLastNodeChildren();
                }
            }

            // Remove the final epsilon term'
            current.removeLast();
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
            if (derivation.size() == 2
                    && derivation.get(1).getSymbol() == Symbol.stmtPrimePrimePrime) {
                current.mergeLastNodeChildren();
            }
        }
    }

    private void removeStmtPrimePrime(ASTNode current) {
        for (var i = 0; i < current.getDerivation().size(); i++) {
            var node = current.get(i);
            this.removeStmtPrimePrime(node);
        }

        if (current.getSymbol() == Symbol.stmtPrime) {
            if (current.getDerivation().size() == 3
                    && current.getLast().getSymbol() == Symbol.stmtPrimePrime) {
                var childNode = current.getLast();
                var childDerivation = childNode.getDerivation();
                if (childDerivation.size() == 4
                        && childNode.getLast().getSymbol() == Symbol.RIGHT_PAREN) {
                    current.mergeLastNodeChildren();
                } else {
                    // expanded numexpr to be merged
                    var factorNode = new ASTNode(Symbol.factor);
                    var termNode = new ASTNode(Symbol.term);
                    var numexprNode = new ASTNode(Symbol.numexpr);
                    var firstNode = childDerivation.remove(0);
                    factorNode.addChild(firstNode);

                    if (firstNode.getSymbol() == Symbol.constNt) {
                        // No more to add to factorNode
                    } else if (firstNode.getSymbol() == Symbol.LEFT_PAREN) {
                        factorNode.addChild(childDerivation.remove(0));
                        factorNode.addChild(childDerivation.remove(0));

                    } else if (firstNode.getSymbol() == Symbol.ID) {
                        factorNode.addChild(childDerivation.remove(0));
                    }

                    termNode.addChild(factorNode);
                    termNode.addChild(childDerivation.remove(0));
                    numexprNode.addChild(termNode);
                    numexprNode.addChild(childDerivation.remove(0));
                    childDerivation.add(numexprNode);
                    current.mergeLastNodeChildren();
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
                    && derivation.get(2).getSymbol() == Symbol.ID
                    && derivation.get(3).getSymbol() == Symbol.LEFT_PAREN
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
