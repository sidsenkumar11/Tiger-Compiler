import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Parser {

    private Token[] tokenList;
    private LinkedList<Token> stack;
    private int currentToken;

    public Parser(String tokens) throws Exception {
        generateTokenList(tokens);
        if (tokenList.length == 0) {
            throw new Exception("Cannot parse empty file");
        }
        initializeStack();
    }

    public void generateTokenList(String tokens) {
        String[] lines = tokens.split("[\\r\\n]+");
        tokenList = new Token[lines.length];
        for (int i = 0; i < lines.length; i++) {
            // Assumes format: Keyword? let
            String[] separated = lines[i].split("\\?");
            Token current = new Token(separated[0].trim(), separated[1].trim());
            tokenList[i] = current;
        }
    }

    public void initializeStack() {
        this.stack = new LinkedList<Token>();
        stack.push(new Token("$", "$"));
        stack.push(tokenList[currentToken++]);
        for (int i = 0; i < stack.size(); i++) {
            System.out.println(stack.get(i));
        }
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
    }
}