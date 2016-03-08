import java.io.File;
import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class ScannerTest {
    
    public static void main(String[] args) throws ParseException {
        DFAScanner scanner = new DFAScanner();
        
        boolean scanTest = false, allPrograms = false;
        for(String s : args) {
            if(s.equals("-all")) {
                allPrograms = true;
            } else if(s.equals("-scantest")) {
                scanTest = true;
            }
        }
        
        if(allPrograms) {
            String filepath = "./resources/tests";
            File dir = new File(filepath);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String filename = f.getName();

                    if (filename.substring(filename.length() - 4, filename.length()).equals(".tgr")) {

                        String fullFilename = filepath + "/" + filename;
                        System.out.println("\n" + fullFilename + ":");
                        List<Token> tokenList = scanner.scan(fullFilename);

                        if (tokenList != null) {
                            Token[] tokenL = new Token[tokenList.size()];
                            for(int i = 0; i < tokenList.size(); i++) {
                                tokenL[i] = tokenList.get(i);
                            }

                            Parser parser = new Parser(tokenL);
                            try {
                                if(parser.parse()) {
                                    System.out.println("Successfully parsed " + filename);
                                }
                            } catch(ParseException pe) {
                                System.err.println(pe.getMessage());
                            }
//                            for (Token t : tokenList) {
//                                System.out.print(t + " ");
//                            }
//                            System.out.println("\n");
                        } else {
                            System.out.println("No tokens for " + filename);
                        }
                    }
                }
            }
        }
        
        if(scanTest) {
            List<Token> tokenList = scanner.scan("scantest");

            if (tokenList != null) {
                for (Token t : tokenList) {
                    System.out.print(t + " ");
                }
                System.out.println("\n");
            } else {
                System.out.println("No tokens for scantest");
            }
        }
    }
}
