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
    private LinkedList<ASTSymbol> parseAST;
    private int astAddLocation;

    public Parser() {
//        Use this when testing just the Parser
//        parseTable = new ParseTable("../../../resources/ParseTable.csv");
        this.parseAST = new LinkedList<>();
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
        this.stack = new LinkedList<Symbol>();
        stack.push(new Token("$", "$"));
        stack.push(new Symbol(false, "program"));
    }

    /**
     * Parses the token array using an LL(1) parse table.
     * @throws Exception If the input program does not parse successfully.
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
            if (stackSymbol.isTerminal()) {
                if (stackSymbol.getValue().equals(nextToken.getValue())) {
                    // Parsed this token successfully. All is well.
                    currentTokenNum++;
                    if (stackSymbol.getValue().equals("id") || stackSymbol.getValue().equals("intlit") || stackSymbol.getValue().equals("floatlit")) {
                        parseAST.set(astAddLocation, new ASTSymbol(nextToken, false, false));
                    }
                    astAddLocation++;
                } else {
                    if (backtrackStack.size() > 0) {
                        // So this rule didn't work. Try backtracking and using a different rule.
                        StackState nextTry = backtrackStack.pop();
                        stack = nextTry.getStack();
                        currentTokenNum = nextTry.getTokenNumber();
                        parseAST = nextTry.getAST();
                        astAddLocation = nextTry.getAstStackTop();
                    } else {
                        throw new ParseException("Expected " + stackSymbol.getValue() + " but found " + nextToken.getValue());
                    }
                }
            } else if(!parseTable.isEmpty(stackSymbol, nextToken)) {
                // Push symbols from table in reverse order onto stack.
                LinkedList<LinkedList<Symbol>> nextStackSymbols = parseTable.get(stackSymbol, nextToken);
                if (nextStackSymbols.size() > 1) {
                    for (int i = 1; i < nextStackSymbols.size(); i++) {
                        LinkedList<Symbol> entryInEntry = nextStackSymbols.get(i);
                        for (int j = entryInEntry.size() - 1; j >= 0; j--) {
                            if (!entryInEntry.get(j).isEpsilon()) {
                                stack.push(entryInEntry.get(j));
                            } else {
//                                System.out.println("Didn't push the epsilon");
                            }
                        }
                        backtrackStack.push(new StackState(stack, currentTokenNum, parseAST, astAddLocation));
                        // Pop off those values we just pushed. We don't actually push here; we're just doing it to save the potential stack state.
                        for (int j = entryInEntry.size() - 1; j >= 0; j--) {
                            if (!entryInEntry.get(j).isEpsilon()) {
                                stack.pop();
                            } else {
//                                System.out.println("Didn't pop the epsilon (cuz you can't!)");
                            }
                        }
                    }
                }

                // Now actually push.
                boolean pushedParen = false;
                int oldASTVal = astAddLocation;
                int newASTVal = astAddLocation;
                for (int i = nextStackSymbols.get(0).size() - 1; i >= 0; i--) {
                    Symbol current = nextStackSymbols.get(0).get(i);
                    if (!current.isEpsilon()) {
                        stack.push(current);

                        Symbol astCurrent = nextStackSymbols.get(0).get(nextStackSymbols.get(0).size() - 1 - i);
                        if (i == nextStackSymbols.get(0).size() - 1) {
                            // Add in the very first element
                            if (astCurrent.isNonterminal()) {
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, true, false));
                                pushedParen = true;
                            } else {
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, false, false));
                            }
                        } else if (i == 0){
                            // Add in the very last element
                            if (pushedParen) {
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, false, true));
                            } else {
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, false, false));
                            }
                        } else {
                            // Add in a middle element
                            if (astCurrent.isNonterminal()) {
                                // If nonterminal, assume it will be matched. If it's not, remove the parens later.
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, true, true));
                            } else {
                                parseAST.add(astAddLocation + 1, new ASTSymbol(astCurrent, false, false));
                            }
                        }
                        astAddLocation++;
                    } else {
//                        System.out.println("Didn't push the epsilon");
                    }
                }
                newASTVal = astAddLocation;
                astAddLocation = oldASTVal + 1;
//                for (int i = oldASTVal + 1; i < newASTVal; i++) {
//                    if (parseAST.get(i).getSymbol().isNonterminal()) {
//                        parseAST.get(i).setLeftParen(true);
//                    }
//                }
//                while (parseAST.get(astAddLocation).isTerminal()) {
//                    astAddLocation++;
//                }
            } else {
                // The table didn't have something for this case.
                if (backtrackStack.size() > 0) {
                    // So this rule didn't work. Try backtracking and using a different rule.
                    StackState nextTry = backtrackStack.pop();
                    stack = nextTry.getStack();
                    currentTokenNum = nextTry.getTokenNumber();
                    parseAST = nextTry.getAST();
                    astAddLocation = nextTry.getAstStackTop();
                } else {
                    throw new ParseException("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
                }
            }
//            System.out.println(getParseAST());
        } while (!stackSymbol.isDollarToken());
        parseAST.add(0, new ASTSymbol(new Symbol(false, "program"), true, true));
//        System.out.println("Hooray! Successfully parsed input.");

        // Remove direct epsilons - nonterminals that have right paren and immediately go to epsilon
        // First, we need the productions for this non-terminal to know if any even go to epsilon directly.
        String[] productions = ParseTable.readIn("resources/productions.txt");
        for (int i = 0; i < parseAST.size(); i++) {
            if (parseAST.get(i).isRightParen()) {
                for (int j = 0; j < productions.length; j++) {
                    String[] production = productions[j].split(" -> ");
                    if (production[0].equals(parseAST.get(i).getSymbol().toString())) {
                        // The LHS matches!
                        if (production[1].equals("''")) {
                            parseAST.remove(i);
                            i--;
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    public String getParseAST() {
        String retString = "";
        for (int i = 0; i < parseAST.size(); i++) {
            retString += parseAST.get(i) + " ";
        }
        return retString;
    }

    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        /* Read in file into string of text */
        // String fileName = args[0];
        String fileName = "../../../resources/test1.tokens";
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
//        System.out.println(fullFileText);
        Parser parser = new Parser(fullFileText);
        parser.parse();
    }
}