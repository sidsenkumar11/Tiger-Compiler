import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class ScannerTest {
    
    public static void main(String[] args) {
        String filename = "scantest";
        
        DFAScanner scanner = new DFAScanner(filename);

        List<Token> tokenList = scanner.scan();
        
        if(tokenList != null) {
            for (Token t : tokenList) {
                System.out.println(t);
            }
        }
    }
}
