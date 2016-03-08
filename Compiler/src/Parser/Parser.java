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
    
    public Parser() {
//        parseTable = new ParseTable("resources/ParseTable.csv");
        parseTable = new ParseTable("../../../resources/ParseTable.csv");
        initializeStack();
    }

    public Parser(String tokens) throws ParseException {
        this();
        generateTokenList(tokens);
        if (tokenList.length == 0) {
            throw new ParseException("Cannot parse empty file");
        }
    }
    
    public Parser(Token[] tokenList) {
        this();
        this.tokenList = tokenList;
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
        this.backtrackStack = new LinkedList<>();
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
                } else {
                    if (backtrackStack.size() > 0) {
                        // So this rule didn't work. Try backtracking and using a different rule.
                        StackState nextTry = backtrackStack.pop();
                        stack = nextTry.getStack();
                        currentTokenNum = nextTry.getTokenNumber();
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
                        backtrackStack.push(new StackState(stack, currentTokenNum));
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
                for (int i = nextStackSymbols.get(0).size() - 1; i >= 0; i--) {
                    if (!nextStackSymbols.get(0).get(i).isEpsilon()) {
                        stack.push(nextStackSymbols.get(0).get(i));
                    } else {
//                        System.out.println("Didn't push the epsilon");
                    }
                }
            } else {
                // The table didn't have something for this case.
                if (backtrackStack.size() > 0) {
                    // So this rule didn't work. Try backtracking and using a different rule.
                    StackState nextTry = backtrackStack.pop();
                    stack = nextTry.getStack();
                    currentTokenNum = nextTry.getTokenNumber();
                } else {
                    throw new ParseException("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
                }
            }
        } while (!stackSymbol.isDollarToken());
        System.out.println("Hooray! Successfully parsed input.");
        return true;
    }

    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        /* Read in file into string of text */
        // String fileName = args[0];
        String fileName = "../../../resources/test4.tokens";
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