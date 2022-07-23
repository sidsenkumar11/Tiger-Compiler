package tiger.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tiger.compiler.parser.ParseException;
import tiger.compiler.parser.Parser;
import tiger.compiler.parser.Token;
import tiger.compiler.intermediatecode.CodeGenerator;
import tiger.compiler.interpreter.Interpreter;
import tiger.compiler.lexer.LexException;
import tiger.compiler.lexer.Lexer;
import java.util.ArrayList;

public class App {

    private static void compile(
            String fileName, boolean tokensFlag, boolean astFlag, boolean interpretFlag)
            throws IOException, LexException, ParseException {

        // Scan source into tokens
        String fileText = Files.readString(Path.of(fileName));
        Lexer scanner = new Lexer();
        List<Token> tokens = scanner.scan(fileText);

        if (tokensFlag) {
            for (Token t : tokens) {
                // Int and float literals get printed like this: "1:intlit"
                if (t.getType().equalsIgnoreCase("Intlit")
                        || t.getType().equalsIgnoreCase("Floatlit")) {
                    System.out.print(t.getContent() + ":" + t.getType().toLowerCase() + " ");
                } else {
                    System.out.print(t + " ");
                }
            }
            System.out.println();
        }

        // Continue if there was no scanning error
        if (tokens.size() > 0 && tokens.get(tokens.size() - 1).getType().equals("error")) {
            throw new LexException("Scanning tokens failed");
        }

        // Parse tokens into AST
        Parser parser = new Parser(tokens);
        parser.parse();
        String ast = parser.getParseAST();
        if (astFlag) {
            System.out.println("\n" + ast);
        }

        // // Type Check the AST
        // // TypeChecker.typeCheck(ast);

        // // Generate Intermediate Code
        // ArrayList<String> IR = CodeGenerator.generateCode(ast);
        // System.out.println(IR);

        // // Run interpreter on IR or generate machine code
        // if (interpretFlag) {
        // Interpreter interpreter = new Interpreter(IR);
        // interpreter.run();
        // }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Missing filename");
            System.exit(1);
        }

        String filename = args[0];
        boolean printTokens = false, printAst = false, interpret = false;

        for (String s : args) {
            if (s.equals("--tokens")) {
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
            App.compile(filename, printTokens, printAst, interpret);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (LexException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println("Parse Failed: " + e.getMessage());
        }
    }
}
