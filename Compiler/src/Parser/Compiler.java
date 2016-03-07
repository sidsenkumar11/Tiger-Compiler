import java.util.Arrays;
import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class Compiler {
    
    public static void main(String[] args) {
//        System.out.println(Arrays.toString(args));
        
        String filename = args[0];
        
        boolean printTokens = false, printAst = false;
        
        for(String s : args) {
            if(s.equals("--tokens")) {
                printTokens = true;
            }
        }
        
        DFAScanner scanner = new DFAScanner(filename);
        
        List<Token> tokens = scanner.scan();
        
        if(printTokens) {
            for(Token t : tokens) {
                System.out.print(t + " ");
            }
        }
        System.out.println("");
        Token[] tokenList = new Token[tokens.size()];
        
        for(int i = 0; i < tokenList.length; i++) {
            tokenList[i] = tokens.get(i);
        }
        
        Parser parser = new Parser(tokenList);
        try {
            parser.parse();
        } catch(ParseException pe) {
            System.err.println(pe.getMessage());
        }
    }
}
