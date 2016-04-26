import java.util.ArrayList;

public class EvaluateExpression {

    public static ArrayList<String> evaluateExpression(String expression, int origReg)  {

        int reg = origReg;
        ArrayList<String> instructions = new ArrayList<>();

        // Get the expression from the ast
        String[] tokens = expression.split(" ");
        System.out.println(java.util.Arrays.toString(tokens));
        String trueExpression = "";
        int i = 0;
        while (i < tokens.length && !tokens[i].contains(";")) {
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
                    String theVariableName = "";
                    for (int j = 0; j < tokens[i+1].length() && tokens[i+1].charAt(j) != ')'; j++) {
                        theVariableName += tokens[i+1].charAt(j);
                    }

                    // Either a pure variable or an array variable
                    if (i + 2 < tokens.length && tokens[i+2].equals("[")) {
                        // TODO: Replace array variable with value
                        // It is an array variable
                        // Get the index
                        String indexExpression = "";
                        int currentIndex = i+2;
                        while (!tokens[currentIndex].contains("]")) {
                            indexExpression += tokens[currentIndex++] + " ";
                        }

                        // Add instructions to calculate index
//                        System.out.println("Index expression: " + indexExpression);
                        ArrayList<String> indexCalculations = evaluateExpression(indexExpression, reg);
                        instructions.addAll(indexCalculations);
                        reg++; // We just used a register to hold the index
//                        System.out.println(indexCalculations);

                        // If array holds ints, put into int register, else float
                        int regOfArrayBase = 0; // TODO: Fill with array's base register
                        if (true) {
                            instructions.add("loadarri r" + regOfArrayBase + "i " + "r" + (reg - 1) + "i r" + (reg++) + "i");
                        } else {
                            instructions.add("loadarrf r" + regOfArrayBase + "i " + "r" + (reg - 1) + "i r" + (reg++) + "f");
                        }
                    }
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
        String[] symbols = trueExpression.split(" ");

        // Evaluate parenthesized expressions
        // TODO:


        boolean notJustOne = false;
        for (i = 0; i < symbols.length; i++) {
            // Find any mults/divs
            if (symbols[i].equals("*")) {
                notJustOne = true;
                String termOne = symbols[prevSymbol(symbols, i)];
                String termTwo = symbols[nextSymbol(symbols, i)];

                if (termOne.contains("r") && termTwo.contains("r")) {
                    // Both are registers
                    if (termOne.contains("i") && termTwo.contains("i")) {
                        // Both are int registers so int operation
                        instructions.add("multi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float register so result is a float reg.
                        instructions.add("multf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termOne.contains("r")) {
                    // One is a register
                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("multimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("multimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termTwo.contains("r")) {
                    // Two is a register
                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
                        // Both are ints so int operation
                        instructions.add("multimmi r" + (reg++) + "i " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("multimmf r" + (reg++) + "f " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else {
                    // Both are numbers
                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("multdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("multdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                }
            } else if (symbols[i].equals("/")) {
                notJustOne = true;
                String termOne = symbols[prevSymbol(symbols, i)];
                String termTwo = symbols[nextSymbol(symbols, i)];

                if (termOne.contains("r") && termTwo.contains("r")) {
                    // Both are registers
                    if (termOne.contains("i") && termTwo.contains("i")) {
                        // Both are int registers so int operation
                        instructions.add("divi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float register so result is a float reg.
                        instructions.add("divf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termOne.contains("r")) {
                    // One is a register
                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("divimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("divimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else if (termTwo.contains("r")) {
                    // Two is a register
                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
                        // Both are ints so int operation
                        instructions.add("divimmi r" + (reg++) + "i " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("divimmf r" + (reg++) + "f " + termTwo + " " + termOne);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                } else {
                    // Both are numbers
                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
                        // Both are ints so int operation
                        instructions.add("divdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
                    } else {
                        // One is a float so result is a float register
                        instructions.add("divdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
                    }
                    symbols[prevSymbol(symbols, i)] = "";
                    symbols[i] = "";
                }
            }
//            if (symbols[i].equals("*")) {
//                try {
//                    Integer.parseInt(symbols[i - 1]);
//                    Integer.parseInt(symbols[i + 1]);
//                    instructions.add("multdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
//                    symbols[i-1] = "r" + (reg - 1) + "i";
//                } catch (Exception e) {
//                    instructions.add("multdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
//                    symbols[i-1] = "r" + (reg - 1) + "f";
//                }
//                symbols[i] = "";
//                symbols[i + 1] = "";
//            } else if (symbols[i].equals("/")) {
//                try {
//                    Integer.parseInt(symbols[i - 1]);
//                    Integer.parseInt(symbols[i + 1]);
//                    instructions.add("divdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
//                    symbols[i-1] = "r" + (reg - 1) + "i";
//                } catch (Exception e) {
//                    instructions.add("divdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
//                    symbols[i-1] = "r" + (reg - 1) + "f";
//                }
//                symbols[i] = "";
//                symbols[i + 1] = "";
//            }
        }

        // Just go in order
        for (i = 0; i < symbols.length; i++) {
            if (symbols[i].equals("+")) {
                notJustOne = true;
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
                notJustOne = true;
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
        if (notJustOne && symbols[symbols.length - 1].contains("i") ) {
            instructions.add("movi r" + origReg + "i " + symbols[symbols.length - 1]);
        } else if (notJustOne){
            instructions.add("movf r" + origReg + "f " + symbols[symbols.length - 1]);
        } else {
            // Only had one term in expression
            if ((Double.parseDouble(symbols[0]) == (int) Double.parseDouble(symbols[0]))) {
                // It was an int
                instructions.add("movi r" + origReg + "i " + symbols[symbols.length - 1]);
            } else {
                // It was a double
                instructions.add("movf r" + origReg + "f " + symbols[symbols.length - 1]);
            }
        }

        reg = origReg + 1;
//        System.out.println(trueExpression);
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
        String ast = "lvalue sum optoffset) := (numexpr (numexpr (term (factor sum))) (linop +) (term (term (factor x [ (numexpr (term (factor i))) ])) (nonlinop *) (factor y [ (numexpr (term (factor i))) ])))) ;))";
        String expression = ast.substring(ast.indexOf("lvalue"));
        ArrayList<String> instructions = evaluateExpression(expression, 0);
        System.out.println(instructions);
    }
}