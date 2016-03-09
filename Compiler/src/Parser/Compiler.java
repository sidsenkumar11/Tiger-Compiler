import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class Compiler {

    public static void compile(String fileName, boolean tokensFlag, boolean astFlag) throws ParseException {

        // Scan the input.
        DFAScanner scanner = new DFAScanner();
        List<Token> tokens = scanner.scan(fileName);

        // Print tokens if asked to.
        if(tokensFlag) {
            for(Token t : tokens) {
                if(t.getType().equalsIgnoreCase("intlit") || t.getType().equalsIgnoreCase("floatlit")) {
                    System.out.print(t.getContent() + ":" + t.getType().toLowerCase());
                } else {
                    System.out.print(t + " ");
                }
            }
            System.out.println();
        }

        // Parse tokens if there was no error
        if(!tokens.get(tokens.size()-1).getType().equals("error")) { // only continue if an error Token wasn't added
            Token[] tokenList = new Token[tokens.size()];
            for (int i = 0; i < tokenList.length; i++) {
                tokenList[i] = tokens.get(i);
            }

            Parser parser = new Parser(tokenList);
            try {
                parser.parse();
                System.out.println("\n" + parser.getParseAST());
            } catch (ParseException pe) {
                System.err.println(pe.getMessage());
            }
        }
    }
    public static void main(String[] args) {

        String filename = args[0];
        // Decide which flags have been inputted.
        boolean printTokens = false, printAst = false;
        for(String s : args) {
            if(s.equals("-tokens") || s.equals("--tokens")) {
                printTokens = true;
            }

            if (s.equals("-ast") || s.equals("--ast")) {
                printAst = true;
            }
        }

        try {
            Compiler.compile("resources/tests/test4.tgr", printTokens, printAst);
//            Compiler.compile(filename, printTokens, printAst);
        } catch (ParseException e) {
            System.out.println("Parse Failed");
        }
        // Look at all .tgr files in directory.
//        File folder = new File("resources/tests");
//        File[] listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                if (listOfFiles[i].getName().substring(listOfFiles[i].getName().length() - 4).equals(".tgr")) {
//                    String fileName = "resources/tests/" + listOfFiles[i].getName();
//                    try {
//                        Compiler.compile(fileName, printTokens, printAst);
//                        System.out.println("SUCCESS: " + listOfFiles[i].getName());
//                    } catch (ParseException e) {
//                        System.out.println("Parse failed on: " + listOfFiles[i].getName());
//                    }
//                }
//            }
//        }
    }
}
