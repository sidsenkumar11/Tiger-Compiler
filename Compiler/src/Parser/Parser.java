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
    private int astAddLocation;
    private ASTNode root;

    public Parser() {
//        Use this when testing just the Parser
//        parseTable = new ParseTable("../../../resources/ParseTable.csv");
        this.root = new ASTNode(new Symbol(false, "program"), null);
        currentNode = root;
        this.parseTable = new ParseTable("resources/ParseTable.csv");
        this.backtrackStack = new LinkedList<>();
        this.astAddLocation = -1;
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
            System.out.println(getParseAST());
            nextToken = tokenList[currentTokenNum];
//            System.out.println("STACK: " + stack);
//            System.out.println("TOKEN: " + nextToken);
            stackSymbol = stack.pop();

//            System.out.println("CURRENT STACK SYMBOL: " + stackSymbol);
            if (stackSymbol.isTerminal()) {
                if (!stackSymbol.isEpsilon()) {
                    if (stackSymbol.getValue().equals(nextToken.getValue())) {
                        // Parsed this token successfully. All is well.
                        currentTokenNum++;
                    } else {
                        if (backtrackStack.size() > 0) {
                            System.out.println("!!!! RULE FAILED !!! BACKTRACKING NOW");
                            // So this rule didn't work. Try backtracking and using a different rule.
                            StackState nextTry = backtrackStack.pop();
                            stack = nextTry.getStack();
                            currentTokenNum = nextTry.getTokenNumber();
                            currentNode = nextTry.getAST();
                            astAddLocation = nextTry.getAstStackTop();
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

                currentNode = currentNode.getParent();
                currentNode.incrementCurrentDeriv();
                if (currentNode.getCurrentDeriv() < currentNode.getDerivation().size()) {
                    currentNode = currentNode.getCurrent();
                }
            } else if(!parseTable.isEmpty(stackSymbol, nextToken)) {
                // Non-Terminal found on stack.

                // First create potential backup copies.
                LinkedList<LinkedList<Symbol>> nextStackSymbols = parseTable.get(stackSymbol, nextToken);
                if (nextStackSymbols.size() > 1) {
                    for (int i = 1; i < nextStackSymbols.size(); i++) {
                        LinkedList<Symbol> entryInEntry = nextStackSymbols.get(i);
                        for (int j = entryInEntry.size() - 1; j >= 0; j--) {
                            stack.push(entryInEntry.get(j));
                        }
                        backtrackStack.push(new StackState(stack, currentTokenNum, currentNode, astAddLocation));
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
                    System.out.println("!!!! RULE FAILED !!! BACKTRACKING NOW");
                    StackState nextTry = backtrackStack.pop();
                    stack = nextTry.getStack();
                    currentTokenNum = nextTry.getTokenNumber();
                    currentNode = nextTry.getAST();
                    astAddLocation = nextTry.getAstStackTop();
                } else {
                    throw new ParseException("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
                }
            }
        } while (!stackSymbol.isDollarToken());
        System.out.println("Hooray! Successfully parsed input.");
        return true;
    }

    public String getParseAST() {
        // Performs pre-order traversal on tree
        StringBuilder traversal = new StringBuilder();
        preOrder(root, traversal);
        return traversal.toString();
    }

    private void preOrder(ASTNode node, StringBuilder traversal) {
        if (node.getSymbol().isNonterminal()) {
            traversal.append("(");
        }
        traversal.append(node.getSymbolValue()).append(" ");

        for (int i = 0; i < node.getDerivation().size(); i++) {
            preOrder(node.getDerivation().get(i), traversal);
        }

        //traversal.deleteCharAt(traversal.length() - 1);
        if (node.getSymbol().isNonterminal()) {
            traversal.append(")");
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        /* Read in file into string of text */
        // String fileName = args[0];
        String fileName = "../../../resources/test1.tokens";
        String fullFileText = "";
        String line;

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
//        System.out.println(fullFileText);
        Parser parser = new Parser(fullFileText);
        parser.parse();
    }
}