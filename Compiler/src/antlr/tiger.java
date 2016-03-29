import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.Trees;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class tiger {

    private static String readFile(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    private static String helpMessage =
            "Tiger Phase I Reference Implementation for CS 4240\n" +
                    "Flags:\n" +
                    "    -h, --help    :    shows help message\n" +
                    "    --ast         :    outputs syntax tree to stdout\n" +
                    "    --tokens      :    prints token stream (unsupported by reference\n" +
                    "                       implementation, but you need to support it)\n" +
                    "    -g, --gui     :    displays graphical image of tree";



    public static void main(String[] args){

        String sourceCode = null;
        String sourceFile = null;
        boolean helpFlag = false;
        boolean tokensFlag = false;
        boolean astFlag = false;
        boolean guiFlag = false;

        for (String arg : args){
            if (arg.matches("(.*).tgr")){
                sourceFile = arg;
                try {
                    sourceCode = readFile(arg);
                }
                catch(IOException e) {
                    System.out.println("Aborting: " + e);
                    System.exit(1);
                }
            }
            else if (arg.matches("-h|--help")){
                helpFlag = true;
            }
            else if (arg.matches("--tokens")){
                tokensFlag = true;
            }
            else if (arg.matches("--ast")){
                astFlag = true;
            }
            else if (arg.matches("-g|--gui")){
                guiFlag = true;
            }
            else {
                System.out.println("Unrecognized argument: '" + arg + "'");
            }

        }

        if (helpFlag || sourceCode == null || args.length == 0){
            System.out.println(helpMessage);
            System.exit(0);
        }

        TigerLexer lexer = new TigerLexer(new ANTLRInputStream(sourceCode));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        TigerParser parser = new TigerParser(tokens);
        TigerParser.ProgramContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        TigerProgramListener listener = new TigerProgramListener();
        walker.walk(listener, tree);

        TypeChecker.typeCheck(tree.toStringTree(parser).toLowerCase());

        if (tokensFlag){

            DFAScanner scanner = new DFAScanner();
            List<Token> tok = scanner.scan(sourceFile);
            for(Token t : tok) {
                if(t.getType().equalsIgnoreCase("intlit") || t.getType().equalsIgnoreCase("floatlit")) {
                    System.out.print(t.getContent() + ":" + t.getType().toLowerCase());
                } else {
                    System.out.print(t + " ");
                }
            }
            System.out.println();
//            System.out.println("--tokens unsupported by reference implementation. You still have to support it.");
        }
        if (astFlag){
            System.out.println(tree.toStringTree(parser).toLowerCase());
        }
        if (guiFlag){
            org.antlr.v4.gui.Trees.inspect(tree, parser);
        }

    }
}
