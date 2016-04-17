import java.io.*;
import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class Compiler {

    public static void compile(String fileName, boolean tokensFlag, boolean astFlag, boolean printIL) throws ParseException {

        // Scan the input.
        DFAScanner scanner = new DFAScanner();
        List<Token> tokens = scanner.scan(fileName);

        // Print tokens if asked to.
        if (tokensFlag) {
            for (Token t : tokens) {
                if (t.getType().equalsIgnoreCase("intlit") || t.getType().equalsIgnoreCase("floatlit")) {
                    System.out.print(t.getContent() + ":" + t.getType().toLowerCase());
                } else {
                    System.out.print(t + " ");
                }
            }
            System.out.println();
        }

        // Parse tokens if there was no error
        if (!tokens.get(tokens.size() - 1).getType().equals("error")) { // only continue if an error Token wasn't added
            Token[] tokenList = new Token[tokens.size()];
            for (int i = 0; i < tokenList.length; i++) {
                tokenList[i] = tokens.get(i);
            }

            Parser parser = new Parser(tokenList);
            try {
                parser.parse();
                String ast = parser.getParseAST();
                if (astFlag) {
                    // Print the ast as a text file if asked to do so.
                    System.out.println("\n" + ast);
                }

                // Begin TypeChecking
//                TypeChecker.typeCheck(ast);

                // Create Intermediate Code

            } catch (ParseException pe) {
                System.err.println(pe.getMessage());
            }
        } else {
            System.out.println("ERROR TOKEN");
        }
    }

    public static void main(String[] args) {
//        productionMain(args);
        String filename="resources/tests/test1.tgr";
        mainOne(filename, args);
//        mainAllFiles(args);
    }

    /**
     * The main method to be run when we finally submit.
     */
    public static void productionMain(String[] args) {
        String filename = args[0];

        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = true, printIL = false;

        for(String s : args) {
            if(s.equals("--tokens")) {
                printTokens = true;
            }

            if (s.equals("--ast")) {
                printAst = true;
            }

            if (s.equals("--runil")) {
                printIL = true;
            }
        }

        try {
            Compiler.compile(filename, printTokens, printAst, printIL);
        } catch (ParseException e) {
            System.out.println("Parse Failed");
        }
    }

    /**
     * Compile one file.
     */
    public static void mainOne(String filename, String[] args) {

        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = true, printIL = false;

        try {
            Compiler.compile(filename, printTokens, printAst, printIL);
        } catch (ParseException e) {
            System.out.println("Parse Failed");
        }
    }

    /**
     * Compile all the tests.
     * @param args
     */
    public static void mainAllFiles(String[] args) {

        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = true, printIL = false;

        File folder = new File("resources/tests");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().substring(listOfFiles[i].getName().length() - 4).equals(".tgr")) {
                    String fileName = "resources/tests/" + listOfFiles[i].getName();
                    try {
                        Compiler.compile(fileName, printTokens, printAst, printIL);
                        System.out.println("SUCCESS: " + listOfFiles[i].getName());
                    } catch (ParseException e) {
                        System.out.println("Parse failed on: " + listOfFiles[i].getName());
                    }
                }
            }
        }
    }
}
