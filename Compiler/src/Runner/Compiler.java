import java.io.*;
import java.util.List;
import java.util.ArrayList;
/**
 * Created by nick on 3/6/16.
 */
public class Compiler {

    public static void compile(String fileName, boolean tokensFlag, boolean astFlag, boolean interpret) throws IOException, ScanException, ParseException {

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
                ArrayList<String> IR = CodeGenerator.generateCode(ast);
                System.out.println(IR);
                // If asked to interpret, just run the interpreted code.
                if (interpret) {
                    Interpreter interpreter = new Interpreter(IR);
                    interpreter.run();
                }
            } catch (ParseException pe) {
                System.err.println(pe.getMessage());
            }
        } else {
            throw new ScanException("Scanning tokens failed");
        }
    }

    public static void main(String[] args) {
//        productionMain(args);
        String filename="../../resources/p1tests/count.tgr";
        mainOne(filename, args);
//        mainAllFiles(args);
    }

    /**
     * The main method to be run when we finally submit.
     */
    public static void productionMain(String[] args) {
        String filename = args[0];

        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = false, interpret = false;

        for(String s : args) {
            if(s.equals("--tokens")) {
                printTokens = true;
            }

            if (s.equals("--ast")) {
                printAst = true;
            }

            if (s.equals("--runil")) {
                interpret = true;
            }
        }

        try {
            Compiler.compile(filename, printTokens, printAst, interpret);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ScanException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            System.out.println("Parse Failed");
        }
    }

    /**
     * Compile one file.
     */
    public static void mainOne(String filename, String[] args) {

        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = true, interpret = false;

        try {
            Compiler.compile(filename, printTokens, printAst, interpret);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ScanException e) {
            System.out.println(e.getMessage());
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
        boolean printTokens = false, printAst = false, interpret = false;

        File folder = new File("../../resources/tests");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().substring(listOfFiles[i].getName().length() - 4).equals(".tgr")) {
                    String filename = "../../resources/tests/" + listOfFiles[i].getName();
                    try {
                        Compiler.compile(filename, printTokens, printAst, interpret);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (ScanException e) {
                        System.out.println(e.getMessage());
                    } catch (ParseException e) {
                        System.out.println("Parse Failed");
                    }
                }
            }
        }
    }
}
