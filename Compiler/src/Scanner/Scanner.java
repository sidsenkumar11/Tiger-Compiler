import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {

    public static void main(String[] args) {

        /* Read in file into string of text */
        // String fileName = args[0];
        String fileName = "tests/factorial.tgr";
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

        /* Begin scanner code */
        String wordlist[] = {"array", "begin", "break", "do", "else", "end", "enddo", "endif", "float",
                "for", "func", "if", "in", "int", "let", "of", "return", "then", "to", "type", "var",
                "while", ",", ":", ";", "(", ")", "[", "]", "{", "}", ".", "+", "-", "*", "/", "=", "<>",
                "<", ">", "<=", ">=", "&", "|", ":="};


        //String fullFileText1[] = (fullFileText.split("((?<=,)|(?=,)|(?<=;)|(?=;)|(?<=:)|(?=:))"));
        String fullFileText_token[] = (fullFileText.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))"));

        //  String fullFileText2 = Arrays.toString(fullFileText1);
        //	System.out.println(Arrays.toString(fullFileText1));
        for (int i = 0; i < fullFileText_token.length; i++) {
            //System.out.println(fullFileText_token[i]);
            String token = fullFileText_token[i];
            //System.out.println(token);
            // Removing remaining Whitespaces
            boolean whitespace_match = false;
            if (token.trim().length() <= 0) {
                //	System.out.println("Whitespace:" +token);
                whitespace_match = true;
            }

            // Comparing the token with keywords
            boolean keyword_match = false;
            if (whitespace_match == false) {
                for (String word : wordlist) {
                    if (word.equals(token) == true) {
                        System.out.println("Keyword " + word);
                        keyword_match = true;
                        break;
                    }
                }
            }


            // Comparing token for identifier
            //System.out.println("Keyword match: " +keyword_match);
            //	System.out.println("Whitespace Match: "+whitespace_match);

            boolean identifier_match = false;
            if ((keyword_match == false) & (whitespace_match == false)) {
                if (Character.toString(token.charAt(0)).matches("[A-Z?]|[a-z?]")) {
                    if (token.matches("[_]+|[A-Z?]+|[a-z?]+|[0-9?]+")) {
                        System.out.println("Id " + token);
                        identifier_match = true;
                    }

                } else if (Character.toString(token.charAt(0)).matches("_")) {
                    if (token.matches("[_]&([A-Z?]|[a-z?]|[0-9?])")) {
                        System.out.println("Id " + token);
                        identifier_match = true;
                    }
                } else {
                    // Error
                }
            }

            // Comparing token for integer literals
            boolean intliteral_match = false;
            if ((keyword_match == false) & (whitespace_match == false) & (identifier_match == false)) {
                if (token.matches("[0-9?]+")) {
                    System.out.println("Intlit " + token);
                    intliteral_match = true;
                }
            }

            // Comparing token to float Literals
            boolean floatliteral_match = false;
            if ((keyword_match == false) & (whitespace_match == false) & (identifier_match == false)
                    & (intliteral_match == false)) {
                System.out.println("floatlit? Entered ");
                if (token.matches("[0-9?]+ | .")) {
                    System.out.println("floatlit " + token);
                }
            }
        }
    }
}