import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Scanner {

    public static void main(String[] args) {
        // String fileName = args[0];
        String fileName = "resources/tests/test4.tgr";
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
        String wordlist[] = {"array", "begin", "break", "do", "else", "end", "enddo", "endif", "float",
                "for", "func", "if", "in", "int", "let", "of", "return", "then", "to", "type", "var",
                "while", ",", ":", ";", "(", ")", "[", "]", "{", "}", ".", "+", "-", "*", "/", "=", "<>",
                "<", ">", "<=", ">=", "&", "|", ":="};


        //String fullFileText1[] = (fullFileText.split("((?<=,)|(?=,)|(?<=;)|(?=;)|(?<=:)|(?=:))"));
        String fullFileText_token[] = (fullFileText.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))"));

        //	System.out.println(fullFileText_token.length);
        //for (int i=0; i< fullFileText_token.length; i++)
        for (int i = 0; i < fullFileText_token.length; i++) {
            //System.out.println(fullFileText_token[i]);

            String token = fullFileText_token[i];
// Removing remaining Whitespaces	   
            Boolean whitespace_match = false;
            if (token.trim().length() <= 0) {
                //	System.out.println("Whitespace:" +token);
                whitespace_match = true;
            }

// Comparing the token with keywords
            Boolean keyword_match = false;
            if (whitespace_match == false) {
                for (String word : wordlist) {
                    if ((token.length() == word.length()) & keyword_match == false) {
                        for (int j = 0; j < word.length(); j++) {
                            // System.out.println(token.charAt(j));
                            if ((word.charAt(j)) == (token.charAt(j))) {
                                // 	System.out.println("char: "+word.charAt(j));
                                if (j == word.length() - 1) {
                                    keyword_match = true;
                                    //	System.out.println(keyword_match);
                                    System.out.println("keyword: " + token);
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }

                // Comparing token for identifier
                Boolean identifier_match = false;
                if ((keyword_match == false) & (whitespace_match == false)) {
                    if (Character.toString(token.charAt(0)).matches("[A-Z?]|[a-z?]")) {

                        for (int j = 0; j < token.length(); j++) {
                            if (Character.toString(token.charAt(j)).matches("[_]+|[A-Z?]+|[a-z?]+|[0-9?]+")) {
                                if (j == token.length() - 1) {
                                    System.out.println("Id: " + token);
                                    identifier_match = true;
                                }
                            }
                        }

                    } else if (Character.toString(token.charAt(0)).matches("_")) {
                        for (int j = 1; j < token.length(); j++) {
                            if (Character.toString(token.charAt(j)).matches("([A-Z?]|[a-z?]|[0-9?])+")) {
                                if (j == token.length() - 1) {
                                    System.out.println("Id: " + token);
                                    identifier_match = true;
                                }
                            }
                        }
                    } else {
                        // Error
                    }
                }


                // Comparing token for integer literals
                Boolean intliteral_match = false;
                if ((keyword_match == false) & (whitespace_match == false) & (identifier_match == false)) {
                    if (token.matches("[0-9?]+")) {
                        System.out.println("Intlit: " + token);
                        intliteral_match = true;
                    }
                }

                // Comparing token to float Literals
                Boolean floatliteral_match = false;
                if ((keyword_match == false) & (whitespace_match == false) & (identifier_match == false)
                        & (intliteral_match == false)) {
                    if (token.matches("([0-9]*)\\.[0]")) {
                        System.out.println("floatlit: " + token);
                    }
                }


            }
        }
    }
}


