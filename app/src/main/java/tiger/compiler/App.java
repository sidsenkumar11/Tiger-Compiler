package tiger.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tiger.compiler.parser.ASTNode;
import tiger.compiler.parser.ParseException;
import tiger.compiler.parser.Parser;
import tiger.compiler.typechecker.TypeCheckException;
import tiger.compiler.typechecker.TypeChecker;
import tiger.compiler.intermediatecode.ILGenerator;
import tiger.compiler.interpreter.Interpreter;
import tiger.compiler.lexer.LexException;
import tiger.compiler.lexer.Lexer;
import tiger.compiler.lexer.Token;
import tiger.compiler.lexer.TokenType;

import java.util.ArrayList;

public class App {

    public static String TokensString(String fileName) throws IOException {
        List<Token> tokens = App.Scan(fileName);
        StringBuilder x = new StringBuilder();
        for (Token t : tokens) {
            x.append(t);
            x.append(" ");
        }
        x.deleteCharAt(x.length() - 1);
        x.append("\n");
        return x.toString();
    }

    public static String ParseString(String fileName)
            throws IOException, ParseException, TypeCheckException {
        return App.Parse(App.Scan(fileName)).getAST();
    }

    public static void ParseAndTypeCheck(String fileName)
            throws IOException, ParseException, TypeCheckException {
        App.TypeCheck(App.Parse(App.Scan(fileName)));
    }

    private static List<Token> Scan(String fileName) throws IOException {
        String fileText = Files.readString(Path.of(fileName));
        Lexer scanner = new Lexer();
        return scanner.scan(fileText);
    }

    private static ASTNode Parse(List<Token> tokens) throws IOException, ParseException {
        var parser = new Parser(tokens);
        var astRoot = parser.parse();
        return astRoot;
    }

    private static void TypeCheck(ASTNode astRoot) throws TypeCheckException {
        var typeChecker = new TypeChecker(astRoot);
        typeChecker.checkProgram();
    }

    private static void Compile(
            String fileName, boolean tokensFlag, boolean astFlag, boolean interpretFlag)
            throws IOException, LexException, ParseException, TypeCheckException {

        // Scan source into tokens
        if (tokensFlag) {
            System.out.print(App.TokensString(fileName));
        }

        var tokens = App.Scan(fileName);
        if (tokens.size() > 0 && tokens.get(tokens.size() - 1).getType() == TokenType.ERROR) {
            throw new LexException("Scanning tokens failed");
        }

        // Parse tokens into AST
        var astRoot = App.Parse(tokens);
        if (astFlag) {
            System.out.println(astRoot.getAST());
        }

        // Type Check the AST
        App.TypeCheck(astRoot);

        // Generate Intermediate Code
        // ArrayList<String> IR = ILGenerator.generateCode(astRoot);
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
            App.Compile(filename, printTokens, printAst, interpret);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (LexException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println("Parse Failed: " + e.getMessage());
        } catch (TypeCheckException e) {
            System.err.println("Typechecking Failed: " + e.getMessage());
        }
    }
}
