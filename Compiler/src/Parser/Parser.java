import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Parser {

    private Token[] tokenList;
    private LinkedList<Symbol> stack;
    private int currentTokenNum;
    private ParseTable parseTable;

    public Parser(String tokens) throws ParseException {
        generateTokenList(tokens);
        if (tokenList.length == 0) {
            throw new ParseException("Cannot parse empty file");
        }
        parseTable = new ParseTable("../../../resources/ParseTable.csv");
        initializeStack();
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
    public void parse() throws ParseException {
        Token nextToken = tokenList[currentTokenNum];
        Symbol stackSymbol;
        do {
            nextToken = tokenList[currentTokenNum];
            System.out.println("STACK: " + stack);
            System.out.println("TOKEN: " + nextToken);
            stackSymbol = stack.pop();
            System.out.println("CURRENT STACK SYMBOL: " + stackSymbol);
            if (stackSymbol.isTerminal()) {
                if (stackSymbol.getValue().equals(nextToken.getValue())) {
                    // Parsed this token successfully. All is well.
                    currentTokenNum++;
                } else {
                    throw new ParseException("Expected " + stackSymbol.getValue() + " but found " + nextToken.getValue());
                }
            } else if(!parseTable.isEmpty(stackSymbol, nextToken)) {
                // Push symbols from table in reverse order onto stack.
                LinkedList<Symbol> nextStackSymbols = parseTable.get(stackSymbol, nextToken);
                for (int i = nextStackSymbols.size() - 1; i >= 0; i--) {
                    if (!nextStackSymbols.get(i).isEpsilon()) {
                        stack.push(nextStackSymbols.get(i));
                    } else {
                        System.out.println("Didn't push the epsilon");
                    }
                }
            } else {
                // The table didn't have something for this case.
                throw new ParseException("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
            }
        } while (!stackSymbol.isDollarToken());
        System.out.println("Hooray! Successfully parsed input.");
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

        Parser parser = new Parser(fullFileText);
        parser.parse();
    }
}