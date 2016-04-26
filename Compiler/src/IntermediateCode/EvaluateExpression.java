import java.util.ArrayList;

public class EvaluateExpression {

    public static ArrayList<String> evaluateExpression(String expression, int origReg)  {

        int reg = origReg;

        // Get the expression from the ast
        String[] tokens = expression.split(" ");
        String trueExpression = "";
        int i = 0;
        while (!tokens[i].contains(";")) {
            if (tokens[i].contains("factor")) {
                // Next token is either "const" or is variable name
                if (tokens[i+1].contains("const")) {
                    // Direct value
                    String theNum = "";
                    for (int j = 0; tokens[i+2].charAt(j) != ')'; j++) {
                        theNum += tokens[i+2].charAt(j);
                    }
                    trueExpression += theNum + " ";
                } else {
                    // Variable name
                    // TODO: Replace variable name with value
                    String num = "1";
                    trueExpression += num + " ";
                }
            } else if (tokens[i].contains("linop") || tokens[i].contains("nonlinop")) {
                // Addition / Subtraction
                String theOp = "";
                for (int j = 0; tokens[i+1].charAt(j) != ')'; j++) {
                    theOp += tokens[i+1].charAt(j);
                }
                trueExpression += theOp + " ";
            }
            i++;
        }

        // Evaluate multiplication and division, store results
        ArrayList<String> instructions = new ArrayList<>();
        String[] symbols = trueExpression.split(" ");

        // Evaluate parenthesized expressions
        // TODO:

        for (i = 0; i < symbols.length; i++) {
            // Find any mults/divs
            if (symbols[i].equals("*")) {
                try {
                    Integer.parseInt(symbols[i - 1]);
                    Integer.parseInt(symbols[i + 1]);
                    instructions.add("multdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
                    symbols[i-1] = "r" + (reg - 1) + "i";
                } catch (Exception e) {
                    instructions.add("multdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
                    symbols[i-1] = "r" + (reg - 1) + "f";
                }
                symbols[i] = "";
                symbols[i + 1] = "";
            } else if (symbols[i].equals("/")) {
                try {
                    Integer.parseInt(symbols[i - 1]);
                    Integer.parseInt(symbols[i + 1]);
                    instructions.add("divdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
                    symbols[i-1] = "r" + (reg - 1) + "i";
                } catch (Exception e) {
                    instructions.add("divdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
                    symbols[i-1] = "r" + (reg - 1) + "f";
                }
                symbols[i] = "";
                symbols[i + 1] = "";
            }
        }

        // Just go in order
        for (i = 0; i < symbols.length; i++) {
            if (symbols[i].equals("+")) {
                String termOne = symbols[prevSymbol(symbols, i)];
                String termTwo = symbols[nextSymbol(symbols, i)];

                if (termOne.contains("r") && termTwo.contains("r")) {
                    // Both are registers
                    if (termOne.contains("i") && termTwo.contains("i")) {
                        // Both are int registers so int operation
                        instructions.add("addi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float register so result is a float reg.
                        instructions.add("addf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termOne.contains("r")) {
                    // One is a register
                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("addimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("addimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termTwo.contains("r")) {
                    // Two is a register
                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
                        // Both are ints so int operation
                        instructions.add("addimmi r" + (reg++) + "i " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("addimmf r" + (reg++) + "f " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else {
                    // Both are numbers
                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("adddubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("adddubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                }
            } else if (symbols[i].equals("-")) {
                String termOne = symbols[prevSymbol(symbols, i)];
                String termTwo = symbols[nextSymbol(symbols, i)];

                if (termOne.contains("r") && termTwo.contains("r")) {
                    // Both are registers
                    if (termOne.contains("i") && termTwo.contains("i")) {
                        // Both are int registers so int operation
                        instructions.add("subi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float register so result is a float reg.
                        instructions.add("subf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termOne.contains("r")) {
                    // One is a register
                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("subimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("subimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termTwo.contains("r")) {
                    // Two is a register
                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
                        // Both are ints so int operation
                        instructions.add("subimmi r" + (reg++) + "i " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("subimmf r" + (reg++) + "f " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else {
                    // Both are numbers
                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("subdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("subdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                }
            }
        }
        // Free up intermediate registers
        if (symbols[symbols.length - 1].contains("i")) {
            instructions.add("movi r" + origReg + "i " + symbols[symbols.length - 1]);
        } else {
            instructions.add("movf r" + origReg + "f " + symbols[symbols.length - 1]);
        }

        reg = origReg + 1;
        // TODO: Remove print
        System.out.println(trueExpression);
        return instructions;

    }

    public static int prevSymbol(String[] symbols, int currentIndex) {
        currentIndex--;
        while (symbols[currentIndex].equals("")) {
            currentIndex--;
        }
        return currentIndex;
    }

    public static int nextSymbol(String[] symbols, int currentIndex) {
        currentIndex++;
        while (symbols[currentIndex].equals("")) {
            currentIndex++;
        }
        return currentIndex;
    }

    public static void main (String[] args) throws Exception {
        String ast = "(program let (declseg typedecls (vardecls (vardecl var (ids a , (ids b)) : (type int) (optinit := (const 0)) ;) vardecls) funcdecls) in (stmts (fullstmt (stmt if (boolexpr (clause (pred ( (boolexpr (clause (pred (numexpr (term (factor a))) (boolop =) (numexpr (term (factor b)))))) )))) then (stmts (fullstmt (stmt (lvalue a optoffset) := (numexpr (numexpr (numexpr (numexpr (term (factor b))) (linop +) (term (term (factor (const 2))) (nonlinop *) (factor (const 3)))) (linop +) (term (factor (const 4)))) (linop -) (term (factor (const 5))))) ;)) endif) ;) (stmts (fullstmt (stmt optstore printi ( (numexprs (neexprs (numexpr (term (factor a))))) )) ;))) end)";
        String expression = ast.substring(ast.indexOf("lvalue"));
        ArrayList<String> instructions = evaluateExpression(expression, 0);
        System.out.println(instructions);
    }
}