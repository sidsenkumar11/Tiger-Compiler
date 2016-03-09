import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Parser {

    private Token[] tokenList;
    private LinkedList<Symbol> stack;
    private LinkedList<StackState> backtrackStack;
    private int currentTokenNum;
    private ParseTable parseTable;
    private ASTNode currentNode;
    private ASTNode root;

    public Parser() {
//        Use this when testing just the Parser
//        parseTable = new ParseTable("../../../resources/ParseTable.csv");
        this.root = new ASTNode(new Symbol(false, "program"), null, null);
        root.setRoot(root);
        currentNode = root;
        this.parseTable = new ParseTable("resources/ParseTable.csv");
        this.backtrackStack = new LinkedList<>();
        initializeStack();
    }

    public Parser(String tokens) throws ParseException {
        this();
        generateTokenList(tokens);
        if (tokenList.length == 0) {
            throw new ParseException("Cannot parse empty file");
        }
    }
    
    public Parser(Token[] tokenList) throws ParseException {
        this();
        this.tokenList = tokenList;
        if (tokenList.length == 0) {
            throw new ParseException("Cannot parse empty file");
        }
    }

    /**
     * Puts all the tokens from the scanner's output into an array.
     * @param tokens The tokens scanned from the Scanner.
     */
    public void generateTokenList(String tokens) {
        String[] lines = tokens.split("[\\r\\n]+");
        tokenList = new Token[lines.length + 1];
        for (int i = 0; i < lines.length; i++) {
            // Assumes format: Keyword let
            String[] separated = lines[i].split(" ");
            Token current = new Token(separated[0].trim(), separated[1].trim());
            tokenList[i] = current;
        }
        tokenList[lines.length] = new Token("$", "$");
    }

    /**
     * Sets up stack by pushing EOF marker and the initial program symbol.
     */
    public void initializeStack() {
        this.stack = new LinkedList<>();
        stack.push(new Token("$", "$"));
        stack.push(new Symbol(false, "program"));
    }

    /**
     * Parses the token array using an LL(1) parse table.
     * @throws ParseException If the input program does not parse successfully.
     */
    public boolean parse() throws ParseException {
        Token nextToken = tokenList[currentTokenNum];
        Symbol stackSymbol;
        do {
            nextToken = tokenList[currentTokenNum];
//            System.out.println("STACK: " + stack);
//            System.out.println("TOKEN: " + nextToken);
            stackSymbol = stack.pop();

//            System.out.println("CURRENT STACK SYMBOL: " + stackSymbol);
//            System.out.println("THE CURRENT NODE IS " + currentNode);
//            System.out.println("IT's PARENT IS " + currentNode.getParent());
//            System.out.println("THE CURRENT NODE INDEX IS " + currentNode.getCurrentDeriv());
//            printAST();

            if (stackSymbol.isTerminal()) {
                if (!stackSymbol.isEpsilon()) {
                    if (stackSymbol.getValue().equals(nextToken.getValue())) {
                        // Parsed this token successfully. All is well.
                        currentTokenNum++;
                    } else {
                        if (backtrackStack.size() > 0) {
                            // So this rule didn't work. Try backtracking and using a different rule.
                            StackState nextTry = backtrackStack.pop();
                            stack = nextTry.getStack();
                            currentTokenNum = nextTry.getTokenNumber();
                            currentNode = nextTry.getAST();
                            root = nextTry.getRoot();
                        } else {
                            throw new ParseException("Expected " + stackSymbol.getValue() + " but found " + nextToken.getValue());
                        }
                    }
                } else {
                    // Just let it pop off if it's epsilon.
                }

                // Handle ASTNode things with Terminal
                if (stackSymbol.getValue().equalsIgnoreCase("id")) {
                    currentNode.setSymbol(nextToken);
                }

                if (stackSymbol.getValue().equalsIgnoreCase("intlit") || stackSymbol.getValue().equalsIgnoreCase("floatlit")) {
                    currentNode.setSymbol(nextToken);
                }

                if (!stackSymbol.getValue().equals("$") && !stackSymbol.getValue().equalsIgnoreCase("end")) {
                    while (currentNode.getDerivation().size() == 1 || currentNode.getDerivation().size() - 1 == currentNode.getCurrentDeriv() || currentNode.getDerivation().size() == 0) {
                        currentNode = currentNode.getParent();
                    }
                    currentNode.incrementCurrentDeriv();
                    currentNode = currentNode.getCurrent();
                }


            } else if(!parseTable.isEmpty(stackSymbol, nextToken)) {
                // Non-Terminal found on stack.

                // First create potential backup copies.
                LinkedList<LinkedList<Symbol>> nextStackSymbols = parseTable.get(stackSymbol, nextToken);
                if (nextStackSymbols.size() > 1) {
                    for (int i = 1; i < nextStackSymbols.size(); i++) {
                        // Make copy of root. Note that this does not copy parent and root nodes into each one.
                        ASTNode tempRoot = root.deepCopy();
                        tempRoot.setRoot(tempRoot);

                        // Set parent and root nodes for each node in the new tree.
                        for (int j = 0; j < tempRoot.getDerivation().size(); j++) {
                            setParentsAndRoots(tempRoot.getDerivation().get(j), tempRoot, tempRoot);
                        }

                        // Get "currentNode" from the new tree.
                        ASTNode temp = getNodeFromInt(currentNode.getNum(), tempRoot);

                        LinkedList<Symbol> entryInEntry = nextStackSymbols.get(i);
                        for (int j = entryInEntry.size() - 1; j >= 0; j--) {
                            stack.push(entryInEntry.get(j));
                            temp.addFirst(entryInEntry.get(j), temp);
                        }
                        temp = temp.getCurrent();
                        backtrackStack.push(new StackState(stack, currentTokenNum, temp, tempRoot));
                        // Pop off those values we just pushed. We don't actually push here; we're just doing it to save the potential stack state.
                        for (int j = entryInEntry.size() - 1; j >= 0; j--) {
                            stack.pop();
                        }
                    }
                }

                // Actually add the symbols to the stack now, in reverse order of the derivation.
                for (int i = nextStackSymbols.get(0).size() - 1; i >= 0; i--) {
                    Symbol current = nextStackSymbols.get(0).get(i);
                    stack.push(current);
                    currentNode.addFirst(current, currentNode);
                }
                currentNode = currentNode.getCurrent();
            } else {
                // The table didn't have something for this case.
                if (backtrackStack.size() > 0) {
                    // So this rule didn't work. Try backtracking and using a different rule.
                    StackState nextTry = backtrackStack.pop();
                    stack = nextTry.getStack();
                    currentTokenNum = nextTry.getTokenNumber();
                    currentNode = nextTry.getAST();
                    root = nextTry.getRoot();
                } else {
                    throw new ParseException("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
                }
            }
        } while (!stackSymbol.isDollarToken());
//        System.out.println("Hooray! Successfully parsed input.");
        return true;
    }

    private ASTNode getNodeFromInt(int num, ASTNode tempRoot) {
        if (tempRoot.getNum() == num) {
            return tempRoot;
        }
        ASTNode node = null;
        for (int i = 0; i < tempRoot.getDerivation().size() && node == null; i++) {
            node = getNodeFromInt(num, tempRoot.getDerivation().get(i));
        }
        return node;
    }

    private void setParentsAndRoots(ASTNode node, ASTNode parent, ASTNode theRoot) {
        node.setParent(parent);
        node.setRoot(theRoot);

        for (int i = 0; i < node.getDerivation().size(); i++) {
            setParentsAndRoots(node.getDerivation().get(i), node, theRoot);
        }
    }

    public String getParseAST() {
        // Performs pre-order traversal on tree
        StringBuilder traversal = new StringBuilder();
        preOrder(root, traversal);
        return traversal.toString();
    }

    public void printAST() {
        printASTRecur(root);
        System.out.println("-----------------------------------------------------------------------");
    }

    public void printASTRecur(ASTNode node) {
        System.out.print(node + ": ");
        for (int i = 0; i < node.getDerivation().size(); i++) {
            System.out.print(node.getDerivation().get(i) + " ");
        }
        System.out.println();
        for (int i = 0; i < node.getDerivation().size(); i++) {
            printASTRecur(node.getDerivation().get(i));
        }
    }

    private void preOrder(ASTNode node, StringBuilder traversal) {
        // Don't print epsilons or nodes whose children are epsilons
        if (node.getSymbol().isPotentialSpecialParentOfEpsilon() && node.getDerivation().get(0).getSymbolValue().equalsIgnoreCase("''")) {

        } else {
            if (!node.getSymbol().isEpsilon()) {
                if (node.getSymbol().isNonterminal()) {
                    traversal.append("(");
                }
                traversal.append(node.getSymbolValue()).append(" ");

                for (int i = 0; i < node.getDerivation().size(); i++) {
                    preOrder(node.getDerivation().get(i), traversal);
                }

                if (node.getSymbol().isNonterminal()) {
                    traversal.deleteCharAt(traversal.length() - 1);
                    traversal.append(") ");
                }
            }
        }
    }
}