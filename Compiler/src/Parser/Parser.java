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

    public Parser(String tokens) throws Exception {
        generateTokenList(tokens);
        if (tokenList.length == 0) {
            throw new Exception("Cannot parse empty file");
        }
        parseTable = new ParseTable();
        initializeStack();
    }

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

    public void initializeStack() {
        this.stack = new LinkedList<Symbol>();
        stack.push(new Token("$", "$"));
        stack.push(new Token("START", "program"));
    }

    public void parse() throws Exception {
        Token nextToken = tokenList[currentTokenNum];
        Symbol stackSymbol;
        do {
            stackSymbol = stack.pop();
            if (stackSymbol.isTerminal() || stackSymbol.isDollarToken()) {
                if (stackSymbol.getValue().equals(nextToken.getValue())) {
                    // Parsed this token successfully. All is well.
                    currentTokenNum++;
                } else {
                    throw new Exception("Expected " + stackSymbol.getValue() + " but found " + nextToken.getValue());
                }
            } else if(!parseTable.isEmpty(stackSymbol, nextToken)) {
                // Push symbols from table in reverse order onto stack.
                Symbol[] nextStackSymbols = parseTable.get(stackSymbol, nextToken);
                for (int i = nextStackSymbols.length; i >= 0; i--) {
                    stack.push(nextStackSymbols[i]);
                }
            } else {
                // The table didn't have something for this case.
                throw new Exception("No rule to parse " + stackSymbol.getValue() + " on input " + nextToken.getValue());
            }
        } while (!stackSymbol.isDollarToken());
        System.out.println("Hooray! Successfully parsed input.");
    }
    public static void main(String[] args) throws Exception {
        /* Read in file into string of text */
        // String fileName = args[0];
        String fileName = "factorial.tokens";
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