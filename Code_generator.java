import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Runner {
		
	public static String checklinop(String[] fullFileText_token, int b, String linop, boolean linopFound) {

    	linopFound = true;
		switch(fullFileText_token[b+1]) {
		case "+": {
			linop = "add";
			return linop;
		}
		case "-": {
			linop = "sub";
			return linop;
		}
		default: {
			System.out.println("Error at linop function\n");
			return null;
			//System.exit(0);
		}
		}
    }

//	//////////////////////////////////
//	 public ArrayList<String> evaluateExpression(String expression, int origReg, ArrayList<SymbolTableEntry> MainSymbolTable)  {
//
//	        int reg = origReg;
//	        ArrayList<String> instructions = new ArrayList<>();
//
//	        // Get the expression from the ast
//	        String[] tokens = expression.split(" ");
//	        System.out.println(java.util.Arrays.toString(tokens));
//	        String trueExpression = "";
//	        int i = 0;
//	        while (i < tokens.length && !tokens[i].contains(";")) {
//	            if (tokens[i].contains("factor")) {
//	                // Next token is either "const" or is variable name
//	                if (tokens[i+1].contains("const")) {
//	                    // Direct value
//	                    String theNum = "";
//	                    for (int j = 0; tokens[i+2].charAt(j) != ')'; j++) {
//	                        theNum += tokens[i+2].charAt(j);
//	                    }
//	                    trueExpression += theNum + " ";
//	                } else {
//	                    // Variable name
//	                    String theVariableName = "";
//	                    for (int j = 0; j < tokens[i+1].length() && tokens[i+1].charAt(j) != ')'; j++) {
//	                        theVariableName += tokens[i+1].charAt(j);
//	                    }
//	                    /// Get the regno for the variable and type///////////
//	                    
//	                    // Either a pure variable or an array variable
//	                    if (i + 2 < tokens.length && tokens[i+2].equals("[")) {
//	                        // TODO: Replace array variable with value
//	                        // It is an array variable
//	                        // Get the index
//	                        String indexExpression = "";
//	                        int currentIndex = i+2;
//	                        while (!tokens[currentIndex].contains("]")) {
//	                            indexExpression += tokens[currentIndex++] + " ";
//	                        }
//
//	                        // Add instructions to calculate index
////	                        System.out.println("Index expression: " + indexExpression);
//	                        ArrayList<String> indexCalculations = evaluateExpression(indexExpression, reg,MainSymbolTable);
//	                        instructions.addAll(indexCalculations);
//	                        reg++; // We just used a register to hold the index
////	                        System.out.println(indexCalculations);
//
//	                        // If array holds ints, put into int register, else float
//	                        int regOfArrayBase = 0; // TODO: Fill with array's base register /////
//	                       	   boolean  isInteger_regOfArrayBase = false;                  
//	                    	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
//	                          
//	                          if (theVariableName.equals(MainSymbolTable.get(mainSymbolTableIndex).name())) {
//	                        	  regOfArrayBase = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//	                        	  isInteger_regOfArrayBase = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//	                      	
//	                          }
//	                    	}
//	                        
//	                        
//	                        if (isInteger_regOfArrayBase) {
//	                            instructions.add("loadarri r" + regOfArrayBase + "i " + "r" + (reg - 1) + "i r" + (reg++) + "i");
//	                        } else {
//	                            instructions.add("loadarrf r" + regOfArrayBase + "i " + "r" + (reg - 1) + "i r" + (reg++) + "f");
//	                        }
//	                    }
//	                    // TODO: Replace variable name with value
//	                    String num = "1";
//	                    trueExpression += num + " ";
//	                }
//	            } else if (tokens[i].contains("linop") || tokens[i].contains("nonlinop")) {
//	                // Addition / Subtraction
//	                String theOp = "";
//	                for (int j = 0; tokens[i+1].charAt(j) != ')'; j++) {
//	                    theOp += tokens[i+1].charAt(j);
//	                }
//	                trueExpression += theOp + " ";
//	            }
//	            i++;
//	        }
//
//	        // Evaluate multiplication and division, store results
//	        String[] symbols = trueExpression.split(" ");
//
//	        // Evaluate parenthesized expressions
//	        // TODO:
//
//
//	        boolean notJustOne = false;
//	        for (i = 0; i < symbols.length; i++) {
//	            // Find any mults/divs
//	            if (symbols[i].equals("*")) {
//	                notJustOne = true;
//	                String termOne = symbols[prevSymbol(symbols, i)];
//	                String termTwo = symbols[nextSymbol(symbols, i)];
//
//	                if (termOne.contains("r") && termTwo.contains("r")) {
//	                    // Both are registers
//	                    if (termOne.contains("i") && termTwo.contains("i")) {
//	                        // Both are int registers so int operation
//	                        instructions.add("multi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float register so result is a float reg.
//	                        instructions.add("multf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termOne.contains("r")) {
//	                    // One is a register
//	                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("multimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("multimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termTwo.contains("r")) {
//	                    // Two is a register
//	                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
//	                        // Both are ints so int operation
//	                        instructions.add("multimmi r" + (reg++) + "i " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("multimmf r" + (reg++) + "f " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else {
//	                    // Both are numbers
//	                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("multdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("multdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                }
//	            } else if (symbols[i].equals("/")) {
//	                notJustOne = true;
//	                String termOne = symbols[prevSymbol(symbols, i)];
//	                String termTwo = symbols[nextSymbol(symbols, i)];
//
//	                if (termOne.contains("r") && termTwo.contains("r")) {
//	                    // Both are registers
//	                    if (termOne.contains("i") && termTwo.contains("i")) {
//	                        // Both are int registers so int operation
//	                        instructions.add("divi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float register so result is a float reg.
//	                        instructions.add("divf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termOne.contains("r")) {
//	                    // One is a register
//	                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("divimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("divimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termTwo.contains("r")) {
//	                    // Two is a register
//	                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
//	                        // Both are ints so int operation
//	                        instructions.add("divimmi r" + (reg++) + "i " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("divimmf r" + (reg++) + "f " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else {
//	                    // Both are numbers
//	                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("divdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("divdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                }
//	            }
////	            if (symbols[i].equals("*")) {
////	                try {
////	                    Integer.parseInt(symbols[i - 1]);
////	                    Integer.parseInt(symbols[i + 1]);
////	                    instructions.add("multdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
////	                    symbols[i-1] = "r" + (reg - 1) + "i";
////	                } catch (Exception e) {
////	                    instructions.add("multdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
////	                    symbols[i-1] = "r" + (reg - 1) + "f";
////	                }
////	                symbols[i] = "";
////	                symbols[i + 1] = "";
////	            } else if (symbols[i].equals("/")) {
////	                try {
////	                    Integer.parseInt(symbols[i - 1]);
////	                    Integer.parseInt(symbols[i + 1]);
////	                    instructions.add("divdubimmi r" + (reg++) + "i " + symbols[i - 1] + " " + symbols[i + 1]);
////	                    symbols[i-1] = "r" + (reg - 1) + "i";
////	                } catch (Exception e) {
////	                    instructions.add("divdubimmf r" + (reg++) + "f " + symbols[i - 1] + " " + symbols[i + 1]);
////	                    symbols[i-1] = "r" + (reg - 1) + "f";
////	                }
////	                symbols[i] = "";
////	                symbols[i + 1] = "";
////	            }
//	        }
//
//	        // Just go in order
//	        for (i = 0; i < symbols.length; i++) {
//	            if (symbols[i].equals("+")) {
//	                notJustOne = true;
//	                String termOne = symbols[prevSymbol(symbols, i)];
//	                String termTwo = symbols[nextSymbol(symbols, i)];
//
//	                if (termOne.contains("r") && termTwo.contains("r")) {
//	                    // Both are registers
//	                    if (termOne.contains("i") && termTwo.contains("i")) {
//	                        // Both are int registers so int operation
//	                        instructions.add("addi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float register so result is a float reg.
//	                        instructions.add("addf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termOne.contains("r")) {
//	                    // One is a register
//	                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("addimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("addimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termTwo.contains("r")) {
//	                    // Two is a register
//	                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
//	                        // Both are ints so int operation
//	                        instructions.add("addimmi r" + (reg++) + "i " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("addimmf r" + (reg++) + "f " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else {
//	                    // Both are numbers
//	                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("adddubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("adddubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                }
//	            } else if (symbols[i].equals("-")) {
//	                notJustOne = true;
//	                String termOne = symbols[prevSymbol(symbols, i)];
//	                String termTwo = symbols[nextSymbol(symbols, i)];
//
//	                if (termOne.contains("r") && termTwo.contains("r")) {
//	                    // Both are registers
//	                    if (termOne.contains("i") && termTwo.contains("i")) {
//	                        // Both are int registers so int operation
//	                        instructions.add("subi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float register so result is a float reg.
//	                        instructions.add("subf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termOne.contains("r")) {
//	                    // One is a register
//	                    if (termOne.contains("i") && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("subimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("subimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else if (termTwo.contains("r")) {
//	                    // Two is a register
//	                    if (termTwo.contains("i") && (Double.parseDouble(termOne) == (int) Double.parseDouble(termOne))) {
//	                        // Both are ints so int operation
//	                        instructions.add("subimmi r" + (reg++) + "i " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("subimmf r" + (reg++) + "f " + termTwo + " " + termOne);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                } else {
//	                    // Both are numbers
//	                    if ((Double.parseDouble(termOne) == (int) Double.parseDouble(termOne)) && (Double.parseDouble(termTwo) == (int) Double.parseDouble(termTwo))) {
//	                        // Both are ints so int operation
//	                        instructions.add("subdubimmi r" + (reg++) + "i " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "i";
//	                    } else {
//	                        // One is a float so result is a float register
//	                        instructions.add("subdubimmf r" + (reg++) + "f " + termOne + " " + termTwo);
//	                        symbols[nextSymbol(symbols, i)] = "r" + (reg - 1) + "f";
//	                    }
//	                    symbols[prevSymbol(symbols, i)] = "";
//	                    symbols[i] = "";
//	                }
//	            }
//	        }
//	        // Free up intermediate registers
//	        if (notJustOne && symbols[symbols.length - 1].contains("i") ) {
//	            instructions.add("movi r" + origReg + "i " + symbols[symbols.length - 1]);
//	        } else if (notJustOne){
//	            instructions.add("movf r" + origReg + "f " + symbols[symbols.length - 1]);
//	        } else {
//	            // Only had one term in expression
//	            if ((Double.parseDouble(symbols[0]) == (int) Double.parseDouble(symbols[0]))) {
//	                // It was an int
//	                instructions.add("movi r" + origReg + "i " + symbols[symbols.length - 1]);
//	            } else {
//	                // It was a double
//	                instructions.add("movf r" + origReg + "f " + symbols[symbols.length - 1]);
//	            }
//	        }
//
//	        reg = origReg + 1;
////	        System.out.println(trueExpression);
//	        return instructions;
//
//	    }
//
//	    public static int prevSymbol(String[] symbols, int currentIndex) {
//	        currentIndex--;
//	        while (symbols[currentIndex].equals("")) {
//	            currentIndex--;
//	        }
//	        return currentIndex;
//	    }
//
//	    public static int nextSymbol(String[] symbols, int currentIndex) {
//	        currentIndex++;
//	        while (symbols[currentIndex].equals("")) {
//	            currentIndex++;
//	        }
//	        return currentIndex;
//	    }
//	
//	
//	
//	
//	//////////////////////////////
	
	
	
public static String checknonlinop(String[] fullFileText_token, int b, String nonlinop, boolean nonlinopFound) {

	nonlinopFound = true;
	switch(fullFileText_token[b+1]) {
	case "*": {
		nonlinop = "mult";
		return nonlinop;
	}
	case "/": {
		nonlinop = "div";
		return nonlinop;
	}
	default: {
		System.out.println("Error at linop function\n");
		return null;
	}
	}

}

	
	
	
public static String checkboolop(String[] fullFileText_token, int b, String boolop, boolean boolopFound) {

	boolopFound = true;
	switch(fullFileText_token[b+1]) {
	case "=": {
		boolop = "brneq";
		//breq,brneq
		return boolop;
	}
	case ">": {
		boolop = "brleq";
		//brgt
		return boolop;
	}
	case "<": {
		boolop = "brgeq";
		//brlt
		return boolop;
	}
	case ">=": {
		boolop = "brlt";
		//brgeq
		return boolop;
	}
	case "<=": {
		boolop = "brgt";
		//brleq
		return boolop;
	}
	case "<>": {
		boolop = "breq";
		// Need to check this operator
		return boolop;
	}
	default: {
		System.out.println("Error in boolop function \n ");
		return null;
	}
	}
}	
	

public static int semicolon_comm_print(int reg_counter, String boolop,String linop,String nonlinop,	boolean linopFound, boolean nonlinopFound, 
		boolean isInteger_RHS,boolean imm_value, boolean isInteger_LHS,String RHS_value, int LHS_value) {
//System.out.println(imm_value);
    if(linopFound == true && RHS_value!=null && LHS_value !=-1) {
    	reg_counter++;
    	if(imm_value == false) {
    	if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("%sf r%d r%d %s \n",linop, reg_counter, LHS_value, RHS_value);
    	else System.out.printf("%si r%d r%d %s \n", linop,reg_counter, LHS_value, RHS_value);
    	return reg_counter;
    	} else {
    		if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("%simmf r%d r%d %s \n",linop, reg_counter, LHS_value, RHS_value);
        	else System.out.printf("%simmi r%d r%d %s \n", linop,reg_counter, LHS_value, RHS_value);
        	return reg_counter;
    	}

    	}
    else if(nonlinopFound == true && RHS_value!=null && LHS_value !=-1) {
    	reg_counter++;
    	if(imm_value == false) {
    	if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("%sf r%d r%d %s \n",nonlinop, reg_counter, LHS_value, RHS_value);
    	else System.out.printf("%si r%d r%d %s \n",nonlinop,reg_counter, LHS_value, RHS_value);
    	return reg_counter;
    	} else {
        	if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("%sf r%d r%d %s \n",nonlinop, reg_counter, LHS_value, RHS_value);
        	else System.out.printf("%si r%d r%d %s \n",nonlinop,reg_counter, LHS_value, RHS_value);
        	return reg_counter;
    	}
    	}
    
    else {
    	if(imm_value == true) {
    		if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("movimmf r%d %s \n", LHS_value, RHS_value);
    		else System.out.printf("movimmi r%d %s \n", LHS_value, RHS_value);
    	}
    	if(imm_value == false) {
    		if(isInteger_RHS == false || isInteger_LHS == false) System.out.printf("movf r%d %s \n", LHS_value, RHS_value);
    		else System.out.printf("movi r%d %s \n", LHS_value, RHS_value);
    	}    	return reg_counter;
    }
}	
	


public static int checkoptstore(String[] fullFileText_token, int l, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

//System.out.println("Entered optstore");
// Check if RHS of assignment is valid
int func_call_var = -1;
String func_call_label = null;
String variableName = null;
int LHS_value = -1;
String RHS_value = null;
boolean isInteger_RHS = true;

boolean isInteger_LHS = true;
boolean imm_value = false;
boolean nonlinopFound = false;
//String nonlinop = null;
//String linop = null;
boolean linopFound = false;
int RHS_src1 =-1;
int RHS_src2 = -1;
boolean isInteger_RHS1 = true;
boolean isInteger_RHS2 = true;
boolean src1_written = false;
boolean src2_written = false;
int RHS_src3 = -1;
boolean isInteger_RHS3 = true;
boolean src3_written = false;
boolean src2_written1 = false;
boolean src1_written1 = false;

if(fullFileText_token[l+8].matches(":=")) func_call_label = fullFileText_token[l+10];
else func_call_label = fullFileText_token[l+1];
//System.out.println(func_call_label);
int RHS_source = -1;
boolean semicolonFound = false;
String linop = null;
String nonlinop = null;
//	System.out.println(linop); System.out.println(nonlinop);                                                                    

for (int b = l; !semicolonFound; b++) {
	
    switch (fullFileText_token[b]) {
    case "lvalue": {
    	variableName = fullFileText_token[b+1];
    	//System.out.println(fullFileText_token[b+1]);
        // String LHSType = null;
         for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size(); symbolIndex++) {
             if (MainSymbolTable.get(symbolIndex).name().equals(variableName)) {
                 LHS_value = MainSymbolTable.get(symbolIndex).reg_no();
                 func_call_var = LHS_value;
                 //System.out.println(func_call_var);
             }
         }
         break;
    }
                                                               
    	

    case "factor": {
      if (fullFileText_token[b + 3].equals("const")) {
      	//System.out.println(fullFileText_token[b + 4]);
      	RHS_value = "#"+ fullFileText_token[b + 4];  
    	imm_value = true;
        isInteger_RHS = !RHS_value.contains(".");
      }
//      else {
//      	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
//              // Need to check if type of this factor matches that of function.
//              if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
//              	RHS_source = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//              	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//            	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
//              	//System.out.println(LHS_value);
//              	//System.out.println(LHS_source);
//
//              }	  
//      	}
//      	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
//            // Need to check if type of this factor matches that of function.
//            if (fullFileText_token[b + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name())) {
//            	RHS_source = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//            	LHS_value = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//          	isInteger_LHS = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//            	//System.out.println(LHS_value);
//            	//System.out.println(LHS_source);
//            }	  
//    	}
//      }
//      
      
      
      /////////////////

  else {
	  //System.out.println(f_scope); 
  	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
         
        if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src1_written1==false) {
          	RHS_src1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
          	isInteger_RHS1 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
          	src1_written1 = true;
          } else if(fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src2_written1 == false) {
            RHS_src2 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
          	isInteger_RHS2 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
            src2_written1 = true;
	    } else if(fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src2_written1 == true) {
	        RHS_src3 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
	      	isInteger_RHS3 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
	        src3_written = true;
	      }
  	}

  	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
        
        if (fullFileText_token[b + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name())&& src1_written1==false) {
        	RHS_src1 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
          	isInteger_RHS1 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
        	src1_written1 = true;
        	//System.out.println(src1_written1); 
         	//System.out.println(src2_written1);
        } else if(fullFileText_token[b + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written1 == false && src1_written1 == true){
          RHS_src2 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
        	isInteger_RHS2 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
          src2_written1 = true;
        } else if(fullFileText_token[b + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written1 == true && src1_written1 == true){
            RHS_src3 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
          	isInteger_RHS3 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
            src3_written = true;
          }

}
  }

  	
      
      
		break;
    }

                                                                                                                                                 
  	 case "linop":{
     	//System.out.println(linop); System.out.println(nonlinop);                                                                    
     	linop = checklinop(fullFileText_token, b, linop,linopFound);
    	linopFound = true;
     	break;
      }
      
      case "nonlinop":{
    	  //System.out.println(nonlinop);                                                                    
      	nonlinop = checknonlinop(fullFileText_token, b, nonlinop,nonlinopFound);                                                    	
      	break;
      }	
 	
      case ";": {
//       	//System.out.println(imm_value); 
//    	  System.out.println(RHS_value);
//         	System.out.println(RHS_src1); 
//         	System.out.println(RHS_src2);
//         	System.out.println(src1_written1); 
//         	System.out.println(src2_written1); 

         	//System.out.println(RHS_src2);

        	//reg_counter++;
    	  if(linop!= null || nonlinop != null) reg_counter = semicolon_comm_print(reg_counter, null, linop, nonlinop,linopFound, nonlinopFound, isInteger_RHS, imm_value, isInteger_RHS1, RHS_value, RHS_src1);
            if(linop!= null || nonlinop != null)  {RHS_value = "r"+reg_counter; RHS_src1 = -1;}
    	  if(!imm_value) {
             	//System.out.println("Entered Here1");
//             	System.out.println(func_call_var);
             	//System.out.println(RHS_value); 
//             	System.out.println(RHS_src2);
    		  
            if(func_call_var != -1 && func_call_label!=null && RHS_src1!=-1 && RHS_src2==-1) System.out.printf("call_ret r%d %s(r%d)\n",func_call_var,func_call_label, RHS_src1);
            else if(func_call_var != -1 && func_call_label!=null&& RHS_src1==-1 && RHS_src2!=-1) System.out.printf("call_ret r%d %s(r%d)\n",func_call_var,func_call_label, RHS_src2);
            else if(func_call_var != -1 && func_call_label!=null&& RHS_src1!=-1 && RHS_src2!=-1) System.out.printf("call_ret r%d %s(r%d,r%d)\n",func_call_var,func_call_label, RHS_src1,RHS_src2);
            else if(func_call_var == -1 && func_call_label!=null && RHS_src1!=-1 && RHS_src2==-1) System.out.printf("call %s(r%d)\n",func_call_label, RHS_src1);
            else if(func_call_var == -1 && func_call_label!=null&& RHS_src1==-1 && RHS_src2!=-1) System.out.printf("call %s(r%d)\n",func_call_label, RHS_src2);
            else if(func_call_var == -1 && func_call_label!=null&& RHS_src1!=-1 && RHS_src2!=-1) System.out.printf("call %s(r%d,r%d)\n",func_call_label, RHS_src1,RHS_src2);
            //else if(func_call_var == -1 && func_call_label!=null && RHS_value!=null) System.out.printf("call r%d %s(%s)\n",func_call_var,func_call_label, RHS_value);
            else  System.out.printf("call %s()\n",func_call_label);
    	  } else {
           	//System.out.println(RHS_value); 
           	//System.out.println("Entered Here2");
           //	System.out.println(RHS_src1);

             if(func_call_var != -1 && func_call_label!=null) System.out.printf("call_ret r%d %s(%s)\n",func_call_var,func_call_label, RHS_value);
             else if(RHS_src1 == -1) System.out.printf("call %s(%s)\n",func_call_label, RHS_value);
             else System.out.printf("call %s(%s,r%d)\n",func_call_label, RHS_value, RHS_src1);
 
    	  } 
    	  RHS_src1 =-1;
    	  RHS_src2 = -1;
    	  RHS_src3 = -1;
    	  isInteger_RHS3 = true;
    	  src3_written = false;
    	  isInteger_RHS1 = true;
    	  isInteger_RHS2 = true;
    	  src1_written1 = false;
    	  src2_written1 = false;
    	  func_call_label = null;
            func_call_var = -1;
            semicolonFound = true;
            isInteger_RHS = true;
            isInteger_LHS = true;
            imm_value = false;
        	nonlinopFound = false;
        	nonlinop = null;
        	linop = null;
            LHS_value = -1;
            RHS_value = null;
            linopFound = false;
            nonlinopFound = false;

            break;
        }
        
        default: {
            break;
        }
    }
    l=b;
}
return l;
}

public static int checkreturn(String[] fullFileText_token, int l, boolean semicolonFound, String f_scope, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

    int func_call_var = -1;
    String func_call_label = null;
    String RHS_value = null;
    boolean imm_value = false;
    boolean isInteger_RHS = true;
    boolean isInteger_LHS = true;
    int LHS_value = -1;      	
    String linop = null;
	boolean linopFound=false;
	String nonlinop=null;
	boolean nonlinopFound=false;
    
    for (int b = l; !semicolonFound; b++) {

		//System.out.printf(b + " "+fullFileText_token[b]);
        switch (fullFileText_token[b]) {
      
        case "factor": {
      if (fullFileText_token[b + 3].equals("const")) {
      	RHS_value = "#"+ fullFileText_token[b + 4];
      	imm_value = true;
        isInteger_RHS = !RHS_value.contains(".");
      }
      else {
      	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
              // Need to check if type of this factor matches that of function.
              if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
              	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
              	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
               
              }

      }
      }
		//System.out.printf("Entered numexpr loop \n");
		break;
  }
      	 case "linop":{
         	linop = checklinop(fullFileText_token, b, linop,linopFound);
      		break;
          }
          
          case "nonlinop":{
          	nonlinop = checknonlinop(fullFileText_token, b, nonlinop,nonlinopFound);                                                    	
      		break;
          }	
     	
            case ";": {
            	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
                    if (f_scope.equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
                    	LHS_value = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                    }
            	}
            	//System.out.println("Enters 1");
               reg_counter = semicolon_comm_print(reg_counter, null, linop, nonlinop,linopFound, nonlinopFound, isInteger_RHS, imm_value, isInteger_LHS, RHS_value, LHS_value);
               //System.out.printf("call_ret r%d %s(r%d)\n",func_call_var,func_call_label, reg_counter);
               func_call_label = null;
               func_call_var = -1;
                semicolonFound = true;                                                                    
                System.out.printf("B end%s \n",f_scope);
                //label_counter++;
                isInteger_RHS = true;
                isInteger_LHS = true;
                imm_value = false;                                                                     	nonlinopFound = false;
            	nonlinop = null;
                LHS_value = -1;
                RHS_value = null;
                linopFound = false;
            	linop = null;
                

                break;
            }
            default: {
                break;
            }

        }
        l=b;        
        }
    return l;
    }	

public static int checklvalue(String[] fullFileText_token, int m, boolean semicolonFound, String f_scope, String var_name, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

	//System.out.println("Entered Lvalue");
    boolean src1_written = false;
    boolean src2_written = false;
    String RHS_imm = null;
    boolean imm_value = false;
    boolean isInteger_RHS = true;
    boolean isInteger_LHS = true;
    int LHS_value = -1;      	
    String linop = null;
	boolean linopFound=false;
	String nonlinop=null;
	boolean nonlinopFound=false;
    int RHS_src1 =-1;
    int RHS_src2 = -1;
    String LHS_imm = null;
    boolean isInteger_RHS1=true;
	boolean isInteger_RHS2=true;
	boolean isArray = false;
	boolean both_imm= false;
	boolean isInteger_LHS_imm = true;
	int linop_counter =0 ;
	int nonlinop_counter =0;
	//String[] linop_array = null;
	//String[] nonlinop_array = null;
    //ArrayList<OpTableEntry> OpSymbolTable = new ArrayList<>();
    String[] linop_array = new String[100];
    String[] nonlinop_array = new String[100];
    String dest_var = null;
    int dest_regno=-1;
    boolean isInteger_dest = true;
    boolean Entered_RHS =false;
  	String RHS_var1 = null;
  	int RHS_var1_regno =-1;
  	boolean isInteger_RHS_var1 = true;
    int loop_index = m;
	//System.out.println(fullFileText_token[m + 1]);
    for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size(); symbolIndex++) {
        if (MainSymbolTable.get(symbolIndex).name().equals(var_name)) {
            LHS_value = MainSymbolTable.get(symbolIndex).reg_no();
          	isInteger_LHS = (MainSymbolTable.get(symbolIndex).type()).contains("int");
        }
    }
    for (int symbolIndex = 0; symbolIndex < FuncSymbolTable.size(); symbolIndex++) {
        if (FuncSymbolTable.get(symbolIndex).name().equals(var_name)) {
            LHS_value = FuncSymbolTable.get(symbolIndex).reg_no();
          	isInteger_LHS = (FuncSymbolTable.get(symbolIndex).type()).contains("int");
        }
    }
    
    
    for (int l = m; !semicolonFound; l++) {

		switch (fullFileText_token[l]) {
		////////////////////////////////////////////////////////////////
		case "[" : {
			isArray = true;
//			if(dest_var==null) {
//				dest_var = fullFileText_token[l-4];
//              	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
//                    
//                    if (dest_var.equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src1_written == false) {
//                    	dest_regno = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//                      	isInteger_dest = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//                    	//src1_written = true;
//                    } else if(dest_var.equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written == false && src1_written == true){
//                    	dest_regno = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//                    	isInteger_dest = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//                      //src2_written = true;
//                    }
//
//            }                         	
//			}
//		
//        	System.out.println(dest_var);
//            for (int n = l; !semicolonFound; n++) {
//
//        		switch (fullFileText_token[n]) {
//                case "factor": {
//                    if (fullFileText_token[n + 3].equals("const")) {
//                    	//System.out.println(fullFileText_token[l + 4]);
//                    	//System.out.println("factor");
//
//        				if(imm_value==true) {
//        					if(isArray) LHS_imm = "#"+fullFileText_token[n + 4];
//                    		else LHS_imm = fullFileText_token[n + 4];
//                          	isInteger_LHS_imm = !((LHS_imm).contains("."));
//                          	both_imm = true;
//                    	} else {
//                    		if(isArray) RHS_imm = fullFileText_token[n + 4];
//                    		else RHS_imm = "#"+ fullFileText_token[n + 4];
//                            isInteger_RHS = !RHS_imm.contains(".");
//                            both_imm = false;
//                    	}
//                    	imm_value = true;
//         
//
//                    }	
//
//              else {
//            	  //System.out.println(f_scope); 
//              	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
//                     
//                      if (fullFileText_token[n + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src1_written == false 
//                    		  && f_scope.equals(FuncSymbolTable.get(funcSymbolTableIndex).scope())) {
//                      	//Dest = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//                      	isInteger_RHS1 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
//                      	src1_written = true;
//                      } else if(fullFileText_token[n + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src2_written == false 
//                    		  && src1_written == true && f_scope.equals(FuncSymbolTable.get(funcSymbolTableIndex).scope())){
//                        if(isArray) RHS_src1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//                    	  RHS_src2 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//                      	isInteger_RHS2 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
//                        src2_written = true;
//                      }
//
//              }
//              	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
//                    
//                    if (fullFileText_token[n + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src1_written == false) {
//                    	RHS_src1 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//                      	isInteger_RHS1 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//                    	src1_written = true;
//                    } else if(fullFileText_token[n + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written == false && src1_written == true){
//                      RHS_src2 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
//                    	isInteger_RHS2 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
//                      src2_written = true;
//                    }
//
//            }
//
//              	
//              }
//                    System.out.printf("r%d[r%s] \n", dest_regno, RHS_src1);
//                    if(Entered_RHS && linop!=null) { System.out.printf(":="); //Entered_RHS = false;
//                    System.out.printf("%s r%d[r%s] \n",linop, RHS_src2, RHS_src1) ;}
// 
//                    System.out.println(RHS_imm); 
//                    System.out.println(RHS_src1); 
//                    System.out.println(RHS_src2); 
//
//        		//System.out.printf("Entered numexpr loop \n");
//        		break;
//          }
//                case ":=": {
//    				System.out.println(RHS_var1);
//
//                	//System.out.println("Entered_RHS");
//                	Entered_RHS = true;
//                	break;
//                }
//              	 case "linop":{
//              		
//                 	linop = checklinop(fullFileText_token, n, linop,linopFound);
//                 	if(linop != null) {
//                 		linop_array[linop_counter] = linop;
//                     	linop_counter++;
//                 	}
//
//                 	linopFound=true;
//              		break;
//                  }
//                  
//                  case "nonlinop":{
//                	  
//                  	nonlinop = checknonlinop(fullFileText_token, n, nonlinop,nonlinopFound);
//                  	if(nonlinop != null) {
//                  		nonlinop_array[nonlinop_counter] = nonlinop;
//                      	nonlinop_counter++;
//                  	}
//
//                  	nonlinopFound=true;
//              		break;
//                  }	
//
//        		}
//        		   l=n;
//        		
//        		}
//         
        	
        	
        	
        	
        	
        	break;
		}
		
		case "]" : {
			//isArray = false;
        	//System.out.println("ArrayEnd");
        	break;
		}
           ///////////////////////////////////////
		
        case "factor": {
        	
            if (fullFileText_token[l + 3].equals("const")) {
            	//System.out.println(fullFileText_token[l + 4]);
            	
            	
				if(imm_value==true) {
					if(isArray) LHS_imm = "#"+fullFileText_token[l + 4];
            		else LHS_imm = fullFileText_token[l + 4];
                  	isInteger_LHS_imm = !((LHS_imm).contains("."));
                  	both_imm = true;
            	} else {
            		if(isArray) RHS_imm = fullFileText_token[l + 4];
            		else RHS_imm = "#"+ fullFileText_token[l + 4];
                    isInteger_RHS = !RHS_imm.contains(".");
                    both_imm = false;
            	}
            	imm_value = true;
 

            }	
        	
        	
        	
        	
        	
//        	//System.out.printf(fullFileText_token[l + 1]);
//      if (fullFileText_token[l + 3].equals("const")) {
//      	//System.out.printf(fullFileText_token[l + 4]);
//      	RHS_imm = "#"+ fullFileText_token[l + 4];
//      	imm_value = true;
//      	//System.out.println(RHS_imm);
//        isInteger_RHS = !RHS_imm.contains(".");
//        //System.out.println(f_scope);                                           	
//      }
      else {
    	  //System.out.println(f_scope); 
      	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
             
              if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src1_written == false 
            		  && f_scope.equals(FuncSymbolTable.get(funcSymbolTableIndex).scope())) {
              	RHS_src1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
              	//else index1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
              	isInteger_RHS1 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
              	src1_written = true;
              } else if(fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src2_written == false 
            		  && src1_written == true && f_scope.equals(FuncSymbolTable.get(funcSymbolTableIndex).scope())){
                RHS_src2 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
              	isInteger_RHS2 = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
                src2_written = true;
              }

      }
      	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
            
            if (fullFileText_token[l + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src1_written == false) {
            	RHS_src1 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
              	isInteger_RHS1 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
            	src1_written = true;
            } else if(fullFileText_token[l + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written == false && src1_written == true){
              RHS_src2 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
            	isInteger_RHS2 = (MainSymbolTable.get(mainSymbolTableIndex).type()).contains("int");
              src2_written = true;
            }

    }

      	
      }

//		//System.out.printf("Entered numexpr loop \n");
		break;
  }
      	 case "linop":{
      		
         	linop = checklinop(fullFileText_token, l, linop,linopFound);
         	if(linop != null) {
         		linop_array[linop_counter] = linop;
             	linop_counter++;
         	}

         	linopFound=true;
      		break;
          }
          
          case "nonlinop":{
        	  
          	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);
          	if(nonlinop != null) {
          		nonlinop_array[nonlinop_counter] = nonlinop;
              	nonlinop_counter++;
          	}

          	nonlinopFound=true;
      		break;
          }	
     	
            case ";": {
                semicolonFound = true;

                if(linopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                	//System.out.printf("Entered Here1 \n");
                	if(isArray == false && (isInteger_RHS1 == false || isInteger_RHS2 == false)) System.out.printf("%sf r%d r%d r%d \n",linop,LHS_value, RHS_src1, RHS_src2);
                	else System.out.printf("%si r%d r%d r%d \n",linop, LHS_value, RHS_src1, RHS_src2);
                 
                	}
                else if(isArray == false && nonlinopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                	//System.out.printf("Entered Here2 \n");
                	if(isInteger_RHS1 == false || isInteger_RHS2 == false) System.out.printf("%sf r%d r%d r%d \n",nonlinop, LHS_value,  RHS_src1, RHS_src2);
                	else System.out.printf("%si r%d r%d r%d \n",nonlinop, LHS_value,  RHS_src1, RHS_src2);

                }
                else if(isArray == false && (nonlinopFound == true || linopFound== true) && RHS_src1!=-1 && imm_value==true && LHS_value !=-1) {
                	//System.out.printf("Entered Here3 \n");
                	if(nonlinopFound == true) {
                	if(isInteger_RHS1 == false || RHS_imm.contains(".")) System.out.printf("%simmf r%d r%d %s \n",nonlinop, LHS_value,  RHS_src1, RHS_imm);
                	else System.out.printf("%simmi r%d r%d %s \n",nonlinop, LHS_value,  RHS_src1, RHS_imm);
                	} else if(linopFound == true) {
                    	if(isInteger_RHS1 == false || RHS_imm.contains(".")) System.out.printf("%simmf r%d r%d %s \n",linop, LHS_value,  RHS_src1, RHS_imm);
                    	else System.out.printf("%simmi r%d r%d %s \n",linop, LHS_value,  RHS_src1, RHS_imm);
                	}

                }
                else if(isArray) {
//                    System.out.println(linop);
//                    System.out.println(nonlinop);
//                    System.out.println(RHS_imm);
//                    System.out.println(LHS_imm);
//    
//                    System.out.println(RHS_src1);
//                    System.out.println(RHS_src2);
//                    System.out.println(LHS_value);
                	if(isInteger_LHS_imm || isInteger_LHS) {
                		if(LHS_value!=-1 && RHS_imm!=null && LHS_imm!=null && RHS_src1==-1 && RHS_src2==-1){
                			System.out.printf("assigni r%d %s %s \n", LHS_value, RHS_imm, LHS_imm);
                		} else {
                			System.out.printf("assigni r%d r%d r%d #%s \n", LHS_value, RHS_src1, RHS_src2, RHS_imm);
                		}
                	} else if(LHS_value!=-1 && RHS_imm!=null && LHS_imm!=null && RHS_src1==-1 && RHS_src2==-1){
            			System.out.printf("assignf r%d %s %s \n", LHS_value, RHS_imm, LHS_imm);
            		} else {
            			System.out.printf("assignf r%d r%d r%d #%s \n", LHS_value, RHS_src1, RHS_src2, RHS_imm);
            		}
                }
                else {
                	//System.out.printf("Entered Here4 \n");
                	if(imm_value == true) {

                		if(isInteger_LHS == false || RHS_imm.contains(".")) System.out.printf("movimmf r%d %s \n", LHS_value, RHS_imm);
                		else System.out.printf("movimmi r%d %s \n", LHS_value, RHS_imm);
                	} else {
                		if(isInteger_LHS == false || isInteger_RHS1 == false) System.out.printf("movf r%d r%d \n", LHS_value, RHS_src1);
                		else System.out.printf("movi r%d r%d \n", LHS_value, RHS_src1);
                	}
                	
                }
                linop_counter = 0;
                nonlinop_counter = 0;
                RHS_imm = null;
                LHS_imm = null;
                RHS_src1 = -1;
                RHS_src2 = -1;
                isArray = false;
                nonlinopFound = false;
                isInteger_RHS1 = true;
                isInteger_RHS2 = true;
                isInteger_LHS = true;
                imm_value = false;                                                                     	
            	nonlinop = null;
                LHS_value = -1;
                //RHS_value = null;
                linopFound = false;
            	linop = null;
           

                break;
            }
            
            default: {
                break;
            }
        }m=l;
        }
    return m;
    }

public static int checkif(String[] fullFileText_token, int m,int label_counter, String f_scope, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

	//System.out.printf("Entered if loop \n");
	String RHS_value = null;
	boolean semicolonFound;
	int LHS_value= -1;
    boolean endifFound = false;
    boolean imm_value = false;
    boolean isInteger_LHS =true;;
	boolean both_imm=false;
	boolean isInteger_RHS=true;
	String boolop=null;
	String linop=null;
	boolean linopFound=false;
	String nonlinop=null;
	boolean nonlinopFound=false;
	int endif_counter=0;
	boolean endif_label=false;
	boolean boolopFound=false;
	int opcounter = 0;
	int LHS_value2 = -1;
    for (int l = m; !endifFound; l++) {

		//System.out.println(l+ " " + fullFileText_token[l]);
        switch (fullFileText_token[l]) {
            case "factor": {
                if (fullFileText_token[l + 3].equals("const")) {
                	//System.out.println(imm_value);
                	
                	if(imm_value==true) {
                		LHS_value = Integer.valueOf(fullFileText_token[l + 4]);
                      	isInteger_LHS = (Integer.toString(LHS_value).contains("."));
                      	both_imm = true;
                	} else {
                		RHS_value = "#"+ fullFileText_token[l + 4];
                        isInteger_RHS = !RHS_value.contains(".");
                        both_imm = false;
                	}
                	imm_value = true;
     

                }
                else {
                	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                        	if(LHS_value==-1) LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                        	else LHS_value2 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                          	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
                        }
                	}
                	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
                        	if(LHS_value==-1) LHS_value = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                        	else LHS_value2 = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                        	isInteger_LHS = (MainSymbolTable.get(MainSymbolTableIndex).type()).contains("int");
                        }
                	}
                }
        		//System.out.printf("Entered numexpr loop \n");
        		break;
            }
            case "boolop":{
            	opcounter++;
				boolop = checkboolop(fullFileText_token, l, boolop,boolopFound);                                                    	
            	boolopFound = true;
            	break;
            }
            
            case "linop":{
            	opcounter++;
            	linop = checklinop(fullFileText_token, l, linop,linopFound);                                                    	
        		break;
            }
            
            case "nonlinop":{
            	opcounter++;
            	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);                                                    	
        		break;
            }
            
            case "then":{
            	//System.out.println("Entered then");
//            	System.out.println(LHS_value);
//            	System.out.println(LHS_value2);
//            	System.out.println(RHS_value);
            	if(both_imm == false) {
            		if(RHS_value!=null) System.out.printf("%s r%d %s %s%d \n",boolop, LHS_value, RHS_value,f_scope, label_counter);
            		else System.out.printf("%s r%d r%d %s%d \n",boolop, LHS_value, LHS_value2,f_scope, label_counter);
            	}
            	else System.out.printf("%s %s #%d %s%d \n",boolop, RHS_value, LHS_value, f_scope,label_counter); 
            	if(linopFound == true) System.out.printf(linop);
            	if(nonlinopFound == true) System.out.printf(nonlinop);            	
            	RHS_value = null;
            	LHS_value = -1;
            	both_imm = false;
            	isInteger_RHS = true;
                isInteger_LHS = true;
                imm_value = false;
                break;
            }
            
////////////////////////////////////////////////////////////////////////////////////////////////                                                   
            case "optstore": {
            	//System.out.println("Entered optstore1");
            	l = checkoptstore(fullFileText_token, l, MainSymbolTable, FuncSymbolTable, reg_counter);                                                      
            	break;
            }
            
            case "lvalue": {
        		//System.out.printf("Entered lvalue loop \n");
            	String var_name = fullFileText_token[l+1];
            	semicolonFound = false;
            	l=checklvalue(fullFileText_token, l, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                break;
                }
//////////    ////////////////////////////////////////////////////////////////////////
         
            case "return": {
                // Make sure return type matches function type
        		//System.out.printf("Entered return loop \n");
                semicolonFound = false;
                boolean functionFinished = false;
                l=checkreturn(fullFileText_token, l, semicolonFound, f_scope, MainSymbolTable, FuncSymbolTable,reg_counter);
                break;
                }	
            
            case "else": {
                System.out.printf("B endif%s%d \n",f_scope,endif_counter);
                
                endif_label =true;
                System.out.printf("%s%d: ",f_scope, label_counter);
              label_counter++;
                //System.out.println("Entered else loop");
                //System.out.println(fullFileText_token[l+10]);
                label_counter++;
                break;
            	
            }
            case "endif": {
                //System.out.println("Enters endif loop");
                if(endif_label == true) System.out.printf("endif%s%d: \n",f_scope,endif_counter);
                else System.out.printf("%s%d: ", f_scope,label_counter);
                //endif_counter++;
                endifFound = true;
                endif_label = false;               
                label_counter++;
                break;
            }
            	
        }
        
        m=l;

    }

	return m;
}

public static int checkfor(String[] fullFileText_token, int m,int label_counter, String f_scope, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

	//System.out.printf("Entered if loop \n");
	String RHS_value = null;
	boolean semicolonFound;
	int LHS_value= -1;
    boolean enddoFound = false;
    boolean imm_value = false;
    boolean isInteger_LHS =true;;
	boolean both_imm=false;
	boolean isInteger_RHS=true;
	String boolop=null;
	String linop=null;
	boolean linopFound=false;
	String nonlinop=null;
	boolean nonlinopFound=false;
	int endif_counter=0;
	boolean endif_label=false;
	boolean boolopFound=false;
	String forvar = fullFileText_token[m+1];
	int forvar_regno =-1;
	String scope = null;
	if(f_scope.equals("main")) scope = "program";

	//isInteger_LHS
	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
        // Need to check if type of this factor matches that of function.
        if (fullFileText_token[m + 1].equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
        	forvar_regno = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
          	//isInteger_LHS = (MainSymbolTable.get(MainSymbolTableIndex).type()).contains("int");
        }
	}
	//System.out.println(reg_counter);
	if(forvar_regno==-1) {
		reg_counter++;
    	MainSymbolTable.add(new SymbolTableEntry(scope, forvar, "int", "id",reg_counter));
    	forvar_regno = reg_counter;
	}

    for (int l = m; !enddoFound; l++) {

		//System.out.println(l+ " " + fullFileText_token[l]);
        switch (fullFileText_token[l]) {
            case "factor": {
                if (fullFileText_token[l + 3].equals("const")) {
                	//System.out.println(imm_value);
                	
                	if(imm_value==true) {
                		LHS_value = Integer.valueOf(fullFileText_token[l + 4]);
                      	isInteger_LHS = (Integer.toString(LHS_value).contains("."));
                      	both_imm = true;
                	} else {
                		RHS_value = "#"+ fullFileText_token[l + 4];
                        isInteger_RHS = !RHS_value.contains(".");
                        both_imm = false;
                	}
                	imm_value = true;
     

                }
                else {
                	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                        	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                          	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
                        }
                	}
                	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
                        	LHS_value = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                          	isInteger_LHS = (MainSymbolTable.get(MainSymbolTableIndex).type()).contains("int");
                        }
                	}
                }
        		//System.out.printf("Entered numexpr loop \n");
        		break;
            }
            case "boolop":{                                              	
				boolop = checkboolop(fullFileText_token, l, boolop,boolopFound);                                                    	
            	boolopFound = true;
            	break;
            }
            
            case "linop":{
            	linop = checklinop(fullFileText_token, l, linop,linopFound);                                                    	
        		break;
            }
            
            case "nonlinop":{
            	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);                                                    	
        		break;
            }
            
            case "to":{
            	//System.out.println("Entered then");
//            	System.out.println(LHS_value);
//            	System.out.println(RHS_value);
            	System.out.printf("movi r%d %s \n", forvar_regno, RHS_value);
            	//System.out.printf("movi r%d %s \n", forvar_regno, RHS_value);

//            	if(both_imm == false) System.out.printf("%s r%d %s %s%d \n",boolop, LHS_value, RHS_value,f_scope, label_counter);
//            	else System.out.printf("%s %s #%d %s%d \n",boolop, RHS_value, LHS_value, f_scope,label_counter); 
//            	if(linopFound == true) System.out.printf(linop);
//            	if(nonlinopFound == true) System.out.printf(nonlinop);            	
            	RHS_value = null;
            	LHS_value = -1;
            	both_imm = false;
            	isInteger_RHS = true;
                isInteger_LHS = true;
                imm_value = false;
                break;
            }
            
            case "do": {
            	//System.out.println("ENtered do");
            	//System.out.println(RHS_value);
            	reg_counter++;
            	if(!imm_value) {
       
            	if(LHS_value!= -1 && (linop!= null && nonlinop==null)) System.out.printf("%si r%d r%d %s\n", linop, reg_counter, LHS_value,RHS_value);
            	else if(LHS_value!= -1 && (linop== null && nonlinop!=null)) System.out.printf("%si r%d r%d %s\n", nonlinop, reg_counter, LHS_value,RHS_value);
            	else System.out.printf("movi r%d %s\n", reg_counter,RHS_value);
            	} else {
                	if(LHS_value!= -1 && (linop!= null && nonlinop==null)) System.out.printf("%simmi r%d r%d %s\n", linop, reg_counter, LHS_value,RHS_value);
                	else if(LHS_value!= -1 && (linop== null && nonlinop!=null)) System.out.printf("%simmi r%d r%d %s\n", nonlinop, reg_counter, LHS_value,RHS_value);
                	else System.out.printf("movi r%d %s\n", reg_counter,RHS_value);
            	}
            	//else System.out.printf("movi r%d %s\n", reg_counter,RHS_value);
            	System.out.printf("for%s%d:", f_scope, label_counter);
            	System.out.printf("brgt r%d r%d endfor%s%d \n", forvar_regno, reg_counter,f_scope, label_counter);
                //System.out.printf("B endif%s%d \n",f_scope,endif_counter);
            	RHS_value = null;
            	LHS_value = -1;
            	both_imm = false;
            	isInteger_RHS = true;
                isInteger_LHS = true;
                imm_value = false;
                endif_label =true;
               // System.out.printf("%s%d: ",f_scope, label_counter);
              //label_counter++;
                //System.out.println("Entered else loop");
                //System.out.println(fullFileText_token[l+10]);
               // label_counter++;
                break;
            	
            }
            
////////////////////////////////////////////////////////////////////////////////////////////////                                                   
            case "optstore": {
            	//System.out.println("Entered optstore1");
            	l = checkoptstore(fullFileText_token, l, MainSymbolTable, FuncSymbolTable, reg_counter);                                                      
            	break;
            }
            
            case "lvalue": {
        		//System.out.printf("Entered lvalue loop \n");
            	String var_name = fullFileText_token[l+1];
            	semicolonFound = false;
            	l=checklvalue(fullFileText_token, l, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                break;
                }
//////////    ////////////////////////////////////////////////////////////////////////
         
            case "return": {
                // Make sure return type matches function type
        		//System.out.printf("Entered return loop \n");
                semicolonFound = false;
                boolean functionFinished = false;
                l=checkreturn(fullFileText_token, l, semicolonFound, f_scope, MainSymbolTable, FuncSymbolTable,reg_counter);
                break;
                }	
            

            case "enddo": {
                System.out.printf("inc r%d \n",forvar_regno);
                System.out.printf("b for%s%d \n",f_scope,label_counter);
                if(endif_label == true) System.out.printf("endfor%s%d: ",f_scope,endif_counter);
                //else System.out.printf("%s%d: ", f_scope,label_counter);
                //endif_counter++;
                enddoFound = true;
                endif_label = false;               
                label_counter++;
                break;
            }
            	
        }
        
        m=l;

    }

	return m;
}


public static int checkwhile(String[] fullFileText_token, int m,int label_counter, String f_scope, ArrayList<SymbolTableEntry> MainSymbolTable, ArrayList<SymbolTableEntry> FuncSymbolTable, int reg_counter) {

	//System.out.printf("Entered if loop \n");
	String RHS_value = null;
	boolean semicolonFound;
	int LHS_value= -1;
    boolean enddoFound = false;
    boolean imm_value = false;
    boolean isInteger_LHS =true;;
	boolean both_imm=false;
	boolean isInteger_RHS=true;
	String boolop=null;
	String linop=null;
	boolean linopFound=false;
	String nonlinop=null;
	boolean nonlinopFound=false;
	int endif_counter=0;
	boolean endif_label=false;
	boolean boolopFound=false;
	String forvar = fullFileText_token[m+1];
	int forvar_regno =-1;
	String scope = null;
	if(f_scope.equals("main")) scope = "program";
	System.out.printf("whilemain%d: \n",label_counter);
	//isInteger_LHS
	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
        // Need to check if type of this factor matches that of function.
        if (fullFileText_token[m + 1].equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
        	forvar_regno = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
          	//isInteger_LHS = (MainSymbolTable.get(MainSymbolTableIndex).type()).contains("int");
        }
	}
	//System.out.println(reg_counter);
//	if(forvar_regno==-1) {
//		reg_counter++;
//    	MainSymbolTable.add(new SymbolTableEntry(scope, forvar, "int", "id",reg_counter));
//    	forvar_regno = reg_counter;
//	}

    for (int l = m; !enddoFound; l++) {

		//System.out.println(l+ " " + fullFileText_token[l]);
        switch (fullFileText_token[l]) {
            case "factor": {
                if (fullFileText_token[l + 3].equals("const")) {
                	//System.out.println(imm_value);
                	
                	if(imm_value==true) {
                		LHS_value = Integer.valueOf(fullFileText_token[l + 4]);
                      	isInteger_LHS = (Integer.toString(LHS_value).contains("."));
                      	both_imm = true;
                	} else {
                		RHS_value = "#"+ fullFileText_token[l + 4];
                        isInteger_RHS = !RHS_value.contains(".");
                        both_imm = false;
                	}
                	imm_value = true;
     

                }
                else {
                	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                        	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                          	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
                        }
                	}
                	for (int MainSymbolTableIndex = 0; MainSymbolTableIndex < MainSymbolTable.size(); MainSymbolTableIndex++) {
                        // Need to check if type of this factor matches that of function.
                        if (fullFileText_token[l + 1].equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
                        	LHS_value = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                          	isInteger_LHS = (MainSymbolTable.get(MainSymbolTableIndex).type()).contains("int");
                        }
                	}
                }
        		//System.out.printf("Entered numexpr loop \n");
        		break;
            }
            case "boolop":{                                              	
				boolop = checkboolop(fullFileText_token, l, boolop,boolopFound);                                                    	
            	boolopFound = true;
            	break;
            }
            
            case "linop":{
            	linop = checklinop(fullFileText_token, l, linop,linopFound);                                                    	
        		break;
            }
            
            case "nonlinop":{
            	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);                                                    	
        		break;
            }
            

            case "do": {
            	//System.out.println(LHS_value);
            	//System.out.println(RHS_value);
            	reg_counter++;
            	if(LHS_value!= -1 && (linop!= null || nonlinop!=null)) System.out.printf("%s r%d r%d %s\n", linop, reg_counter, LHS_value,RHS_value);
            	else System.out.printf("movi r%d %s\n", reg_counter,RHS_value);
            	//System.out.printf("for%s%d:", f_scope, label_counter);
            	System.out.printf("brgt r%d r%d endwhile%s%d \n", LHS_value, reg_counter,f_scope, label_counter);
                //System.out.printf("B endif%s%d \n",f_scope,endif_counter);
            	RHS_value = null;
            	LHS_value = -1;
            	both_imm = false;
            	isInteger_RHS = true;
                isInteger_LHS = true;
                imm_value = false;
                endif_label =true;
               // System.out.printf("%s%d: ",f_scope, label_counter);
              //label_counter++;
                //System.out.println("Entered else loop");
                //System.out.println(fullFileText_token[l+10]);
               // label_counter++;
                break;
            	
            }
            
////////////////////////////////////////////////////////////////////////////////////////////////                                                   
            case "optstore": {
            	//System.out.println("Entered optstore1");
            	l = checkoptstore(fullFileText_token, l, MainSymbolTable, FuncSymbolTable, reg_counter);                                                      
            	break;
            }
            
            case "lvalue": {
        		//System.out.printf("Entered lvalue loop \n");
            	String var_name = fullFileText_token[l+1];
            	semicolonFound = false;
            	l=checklvalue(fullFileText_token, l, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                break;
                }
//////////    ////////////////////////////////////////////////////////////////////////
         
            case "return": {
                // Make sure return type matches function type
        		//System.out.printf("Entered return loop \n");
                semicolonFound = false;
                boolean functionFinished = false;
                l=checkreturn(fullFileText_token, l, semicolonFound, f_scope, MainSymbolTable, FuncSymbolTable,reg_counter);
                break;
                }	
            

            case "enddo": {
               // System.out.printf("inc r%d \n",forvar_regno);
                System.out.printf("b while%s%d \n",f_scope,label_counter);
                if(endif_label == true) System.out.printf("endwhile%s%d: ",f_scope,endif_counter);
                //else System.out.printf("%s%d: ", f_scope,label_counter);
                //endif_counter++;
                enddoFound = true;
                endif_label = false;               
                label_counter++;
                break;
            }
            	
        }
        
        m=l;

    }

	return m;
}









	
	
	public static void main(String[] args) {
		// String fileName = args[0];
		String fileName = "test1.ast";
		String fullFileText = "";
        String line = null;

	    try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                fullFileText += line + "\n";
            }   
            bufferedReader.close();         
        } catch(FileNotFoundException ex) {
        	System.out.println("Unable to open file '" + fileName + "'");                
        } catch(IOException ex) {
        	System.out.println("Error reading file '" + fileName + "'");                  
            // ex.printStackTrace();
        }   
	  
	    String fullFileText_token[] = (fullFileText.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))"));
        int k = 4;
        int p = 4;
        int LHS_value = -1;
        boolean linopFound=false;
        boolean nonlinopFound = false;
        boolean boolopFound = false;
        //int label_counter = 0;
        int reg_counter=0;
        int funcvar_no = 0;
        int typevar_no = 0;
        int T_index = 0;
        int label_counter =0;
        String var_type = null;
        String scope = "program";
        String f_scope= null;
        String T_type;
        ArrayList<SymbolTableEntry> MainSymbolTable = new ArrayList<>();
        ArrayList<TypeTableEntry> TypeSymbolTable = new ArrayList<>();
        ArrayList<SymbolTableEntry> FuncSymbolTable = new ArrayList<>();
        ArrayList<reg_no> reg_no = new ArrayList<>();
        String[] m_var_name = new String[100];
        String[] type_name = { "int", "float", "int array", "float array" };

        // Assigning int, float, int array and float arrray type to the object
        if (typevar_no < 4) {
            for (int j = 0; j < 4; j++) {
                TypeSymbolTable.add(new TypeTableEntry(scope, type_name[j], type_name[j], "type", 0));
                typevar_no++;
            }
        }

        // Assigning int, float, int array and float array type to the object

        	reg_no.add(new reg_no(reg_counter, 0));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "readi", "int", "func",reg_no.get(reg_counter).reg_index()));
        	reg_counter++;
        	reg_no.add(new reg_no(reg_counter, 0));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "readf", "float", "func",reg_no.get(reg_counter).reg_index()));
        	reg_counter++;
        	reg_no.add(new reg_no(reg_counter, 0));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "printi", "void", "func",reg_no.get(reg_counter).reg_index()));
        	reg_counter++;
        	reg_no.add(new reg_no(reg_counter, 0));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "printf", "void", "func",reg_no.get(reg_counter).reg_index()));
        	reg_counter++;
        	reg_no.add(new reg_no(reg_counter, 0));
        	FuncSymbolTable.add(new SymbolTableEntry("printi", "input", "int", "id",reg_no.get(reg_counter).reg_index()));

            funcvar_no++;
            reg_counter++;
        	reg_no.add(new reg_no(reg_counter, 0));
        	FuncSymbolTable.add(new SymbolTableEntry("printf", "input", "float", "id",reg_no.get(reg_counter).reg_index()));

            funcvar_no++;

        
        for (int i = 0; i < fullFileText_token.length; i++) {


            // System.out.println(fullFileText_token[i]);
            switch (fullFileText_token[i]) {

                // Checking for type declaration
                case "typedecl": {
                    T_type = fullFileText_token[i + 8];
                    boolean Type_match = false;

                    // Checking if the data type is array
                    if (T_type.matches("array")) {
                        T_type = fullFileText_token[i + 16] + " " + fullFileText_token[i + 8];
                        T_index = Integer.valueOf(fullFileText_token[i + 10]);
                    }

                    // Checking if the type is present in the type table and changing to default data type
                    for (int j = 0; j < typevar_no && !Type_match; j++) {
                        if (T_type.equals(TypeSymbolTable.get(j).name())) {
                            Type_match = true;                              
                            if(!(T_type.equals("int") || T_type.equals("float")
                            		|| T_type.equals("int array") || T_type.equals("float array"))) {
                                for (int u = 0; u < j; u++) {
                                if (T_type.equals(TypeSymbolTable.get(j).name())) {
                                T_type = TypeSymbolTable.get(j).type();
                                }
                                }
                            }
                        }
                    }


                    
                    // System.out.println(typevar_no);
                    // Assigning new type to the object
                    if (Type_match) {
                        TypeSymbolTable.add(new TypeTableEntry(scope, fullFileText_token[i + 2], T_type, "type", T_index));
                        typevar_no++;
                        i = i + 8;
                    } else {
                        System.out.println(T_type + " not defined");
                        System.exit(0);
                    }
                    break;
                }

                // Checking for variable Declaration
                case "vardecl": {
                    boolean var_loop_stop = false;
                    boolean init_present = false;
                    int array_size = -1;
                    for (int j = i; !var_loop_stop; j++) {
                        switch (fullFileText_token[j]) {
                            case "ids": {
                                m_var_name[k] = fullFileText_token[j + 1];
                            	if( m_var_name[k].contains("_")) {
                            		 m_var_name[k] =  m_var_name[k].replace("_", "");
                            	}
                                k++;
                                break;
                            }
                            case "type": {
                                for (; p < k; p++) {                                  
                                    boolean var_type_match = false;                                 
                                    
                                    var_type = fullFileText_token[j + 1];

                                    for (int r = 0; r < typevar_no && !var_type_match; r++) {
                                        if (var_type.equals(TypeSymbolTable.get(r).name())) {
                                            var_type_match = true;
                                            
                                            if(!(var_type.equals("int") || var_type.equals("float")
                                            		|| var_type.equals("int array") || var_type.equals("float array"))) {
                                                for (int u = 0; u < r; u++) {
                                                if (var_type.equals(TypeSymbolTable.get(r).name())) {
                                                	var_type = TypeSymbolTable.get(r).type();
                                                }
                                                }
                                            }   
                                            
                                        }
                                    }

                                    if (var_type_match) {
                                        if (fullFileText_token[j + 1].matches("int")
                                                && fullFileText_token[j + 7].matches(":=")) {
                                            boolean isInteger = !fullFileText_token[j + 11].contains(".");
                                     
                                            if (!isInteger) {
                                                System.out.println("Initialized variable Type Mismatch: " + fullFileText_token[j + 1]);
                                                System.exit(0);
                                            }
                                        }
                                    	reg_counter++;
                                    	reg_no.add(new reg_no(reg_counter, 0));
                                        MainSymbolTable.add(
                                                new SymbolTableEntry(scope, m_var_name[p], var_type, "id",reg_no.get(reg_counter).reg_index()));
                                        if(var_type.matches("int"))
                                        {
                                            if(fullFileText_token[j + 7].matches(":="))	{
                                            	if(fullFileText_token[j + 11].contains(".")) System.out.printf("movimmf r%d #%s\n", reg_counter, fullFileText_token[j + 11]);
                                            	else System.out.printf("movimmi r%d #%s\n", reg_counter, fullFileText_token[j + 11]); 
                                            } else {
                                            	System.out.printf("loadi r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                            }
                                        }
                                        if(var_type.matches("float"))
                                        {
                                            if(fullFileText_token[j + 7].matches(":="))	System.out.printf("moveimmf r%d #%s\n", reg_counter, fullFileText_token[j + 11]);
                                            else System.out.printf("loadf r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());

                                        }
                                        if(var_type.matches("int array"))
                                        {
                                        	//System.out.printf("loadarri r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                        	for(int a=0;a<typevar_no;a++) {
                                                if(fullFileText_token[j + 7].matches(":=") && MainSymbolTable.get(p).type().matches(TypeSymbolTable.get(a).type()))	{
                                                	array_size = TypeSymbolTable.get(a).index(); init_present = true;                                            	
                                                } else if(MainSymbolTable.get(p).type().matches(TypeSymbolTable.get(a).type())) {
                                                	array_size = TypeSymbolTable.get(a).index(); init_present = false; 
                                                }

                                        	}
                                        	if(init_present == true) System.out.printf("movarrimmi r%d %d #%s\n", reg_counter,array_size, fullFileText_token[j + 11]);
                                        	if(init_present == false) System.out.printf("movarri r%d %d\n", reg_counter,array_size);
                                            init_present = false;
                                        	//reg_counter = reg_counter + array_size;
                                        }
                                        if(var_type.matches("float array"))
                                        {
                                        	
                                        	//System.out.printf("loadarrf r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                        	for(int a=0;a<typevar_no;a++) {
                                                if(fullFileText_token[j + 7].matches(":=") && MainSymbolTable.get(p).type().matches(TypeSymbolTable.get(a).type()))	{
                                                	array_size = TypeSymbolTable.get(a).index(); init_present = true;                                         	
                                                } else if(MainSymbolTable.get(p).type().matches(TypeSymbolTable.get(a).type())) {
                                                	array_size = TypeSymbolTable.get(a).index(); init_present = false; 
                                                }
                                        	}
                                        	if(init_present == true) System.out.printf("movarrimmf r%d %d #%s\n", reg_counter,array_size, fullFileText_token[j + 11]);
                                        	if(init_present == false) System.out.printf("movarrf r%d %d\n", reg_counter,array_size);
                                        	//reg_counter = reg_counter + array_size;
                                        }

                                    } else {
                                        System.out.println("Variable Type Mismatch: " + fullFileText_token[j + 1]);
                                        System.exit(0);
                                    }
                                }
                                break;
                            }

                            case "optinit": {
                                var_loop_stop = true;
                                //System.out.println(fullFileText_token[j + 6]);
                                break;
                            }

                            default: {
                                break;
                            }
                        }
                        i = j;
                    }
                    break;
                }

                // Checking for function Declaration

                case "funcdecl": {
                	//System.out.println("Entered Function Declaration");
                    boolean f_loop_stop = false;
                    boolean isVoid = false;
                    SymbolTableEntry functionEntry = null;
                    for (int j = i; !f_loop_stop; j++) {
                    	//System.out.println(fullFileText_token[j]);
                        switch (fullFileText_token[j]) {
                            case "func": {

                                m_var_name[k] = fullFileText_token[j + 1];
                                System.out.printf("label %s: ", m_var_name[k]);
                                f_scope = fullFileText_token[j + 1];
                                i = j;
                                break;
                            }
                            case "param": {
                             // Checking if the type is correct for function
                            	var_type = fullFileText_token[j + 7];
                                boolean funcvar_type_match = false;
                                for (int r = 0; r < typevar_no && !funcvar_type_match; r++) {
                                    if (fullFileText_token[j + 7].equals(TypeSymbolTable.get(r).name())) {
                                        funcvar_type_match = true;
                                        if(!(var_type.equals("int") || var_type.equals("float")
                                         		|| var_type.equals("int array") || var_type.equals("float array"))) {
                                             for (int u = 0; u < r; u++) {
                                             if (var_type.equals(TypeSymbolTable.get(r).name())) {
                                             	var_type = TypeSymbolTable.get(r).type();
                                             }
                                             }
                                         }   
                                
                                    }
                                }

                                if (funcvar_type_match) {
                                	reg_counter++;
                                	reg_no.add(new reg_no(reg_counter, 0));
                                    FuncSymbolTable.add(new SymbolTableEntry(f_scope, fullFileText_token[j + 1],
                                            fullFileText_token[j + 7], "id",reg_no.get(reg_counter).reg_index()));
                                    	if(fullFileText_token[j + 1].contains(".")) System.out.printf("movf r%d %s \n", reg_no.get(reg_counter).reg_index(),fullFileText_token[j + 1]);
                                    	else System.out.printf("movi r%d %s \n", reg_no.get(reg_counter).reg_index(),fullFileText_token[j + 1]);

                                    funcvar_no++;
                                    j = j + 7;
                                } else {
                                    System.out.println("Function Variable Type Mismatch: " + fullFileText_token[j + 7]);
                                    System.exit(0);
                                }
                                break;
                            }

                            // Checking type for the function declared

                            case "optrettype": {
                            	//System.out.println("Enters optrettype");
                            	reg_counter++;
                            	reg_no.add(new reg_no(reg_counter, 0));
                                MainSymbolTable.add(new SymbolTableEntry(scope, f_scope, "", "func",reg_no.get(reg_counter).reg_index()));
                                if(fullFileText_token[j+5].matches("type")) {

                                    MainSymbolTable.get(k).setType(fullFileText_token[j+6]);
                                } else {
                                    MainSymbolTable.get(k).setType("void");
                                    isVoid = true;
                                }

                                functionEntry = MainSymbolTable.get(k);
                                boolean func_type_match=false;

                                for(int r=0; r< typevar_no;r++) {

                                    if((MainSymbolTable.get(k).type().equals(TypeSymbolTable.get(r).name()) || (MainSymbolTable.get(k).type()).matches("void")) && func_type_match==false) {
                                        func_type_match = true;

                                        break;

                                    }

                                }

                                if(func_type_match == true) {

                                    MainSymbolTable.get(k).setName(f_scope);
                                    if(isVoid==false) j = j + 6;
                                    isVoid = false;
                                    k++;

                                } else {

                                    System.out.println("Function Type Mismatch: " + MainSymbolTable.get(k).name());
                                    System.exit(0);
                                }
                                break;
                            }

                            // Checking the end of function declaration
                            case "begin": {
                            	//System.out.println("Enters Begin");
                                f_loop_stop = true;
                            	boolopFound = false;
                            	linopFound = false;
                            	nonlinopFound = false;
                            	
                                int endif_counter = 0;
                                boolean functionFinished = false;
                                for (int m = i; !functionFinished; m++) {
                                    //System.out.println(fullFileText_token[m]);

                                    switch (fullFileText_token[m]) {
                                    	case "if": {   
                                    		//System.out.printf("Entered if loop \n");
                                    		m=checkif(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                                    		label_counter++;
                                    		break;
                                    	}                                    	                                        
                                        case "lvalue": {
                                    		//System.out.printf("Entered lvalue loop \n");
                                        	String var_name = fullFileText_token[m+1];
                                        	boolean semicolonFound = false;
                                        	m=checklvalue(fullFileText_token, m, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                                            break;
                                            }

                                    	
                                    	case "while": {
                                    		//System.out.printf("Entered while loop \n");
                                    		m=checkwhile(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                                    		label_counter++;
                                    		break;

                                    	}
                                    	case "for": {
                                    		//System.out.printf("Entered for loop \n");
                                    		//boolean semicolonFound = false;
                                    		m=checkfor(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                                    		label_counter++;
                                    		break;

                                    	}
                                 
                                    
                                        case "return": {
                                    		//System.out.printf("Entered return loop \n");
                                            boolean semicolonFound = false;
                                            String var_name = f_scope;                                            
                            				m=checklvalue(fullFileText_token, m, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                                            break;
                                            }

                                        
                                        case "optstore": {
                                        	//System.out.println("Entered optstore");
                                        	m=checkoptstore(fullFileText_token, m, MainSymbolTable, FuncSymbolTable, reg_counter);                                                      
                                            break;
                                        }
                                        
                                        case "end": {
                                            System.out.printf("end%s: pop\n", f_scope);
                                            functionFinished = true;
                                            break;
                                        }

                                        default: {
                                            break;
                                        }
                                    }
                                    i=m;
                                }
                                break;
                            }

                        }
                        i = j;
                    }
                    break;
                }
                case "in": {
//                	System.out.println(i);
//                	System.out.println(fullFileText_token[i+1]);
//                	System.out.println(fullFileText_token[i+2]);
//                	System.out.println(fullFileText_token[i+3]);
//                	System.out.println(fullFileText_token[i+4]);
//                	System.out.println(fullFileText_token[i+12]);
                	//System.out.println("Enters Begin");
                    //f_loop_stop = true;
                	boolopFound = false;
                	linopFound = false;
                	nonlinopFound = false;
                	System.out.println("main:");
                    int endif_counter = 0;
                    boolean functionFinished = false;
                    label_counter =0;
                    f_scope = "main";
                    for (int m = i; !functionFinished; m++) {
                        //System.out.println(fullFileText_token[m]);

                        switch (fullFileText_token[m]) {
                        	case "if": {   
                        		System.out.printf("Entered if loop \n");
                        		m=checkif(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                        		label_counter++;
                        		break;
                        	}                                    	                                        
                            case "lvalue": {
                        		//System.out.printf("Entered lvalue loop \n");
                            	String var_name = fullFileText_token[m+1];
                            	boolean semicolonFound = false;
                            	m=checklvalue(fullFileText_token, m, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                                break;
                                }

                        	
                        	case "while": {
                        		m=checkwhile(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                        		//System.out.printf("Entered while loop \n");
                        		break;

                        	}
                        	case "for": {
                        		//System.out.printf("Entered for loop \n");
                        		//boolean semicolonFound = false;
                        		m=checkfor(fullFileText_token, m,label_counter, f_scope, MainSymbolTable, FuncSymbolTable, reg_counter); 
                        		label_counter++;
                        		break;

                        	}
                     
                        
                            case "return": {
                        		System.out.printf("Entered return loop \n");
                                boolean semicolonFound = false;
                                String var_name = f_scope;                                            
                				m=checklvalue(fullFileText_token, m, semicolonFound, f_scope, var_name, MainSymbolTable, FuncSymbolTable, reg_counter);
                                break;
                                }

                            
                            case "optstore": {
                            	//System.out.println("Entered optstore");
                            	//System.out.println(m);
                            	m=checkoptstore(fullFileText_token, m, MainSymbolTable, FuncSymbolTable, reg_counter);                                                      
                                break;
                            }
                            
                            case "end": {
                                System.out.printf("end%s\n", f_scope);
                                functionFinished = true;
                                break;
                            }

                            default: {
                                break;
                            }
                        }
                        i=m;
                    }
                    break;
                }
                	
             
                default: {
                    break;
                }

                
            }
            

        }
        System.out.println("Main Symbol Table:");
        for (int w = 0; w < k; w++) {
            System.out.printf("S: %s  M_Var_N: %s  T: %s "
                            + "A: %s rno : %d\n", MainSymbolTable.get(w).scope(),
                    MainSymbolTable.get(w).name(), MainSymbolTable.get(w).type(),
                    MainSymbolTable.get(w).attr(), MainSymbolTable.get(w).reg_no());
        }
        
        System.out.println("Type Symbol Table:");
        for (int w = 0; w < typevar_no; w++) {
            System.out.printf("S: %s  T_Var_N: %s  T: %s "
                            + "A: %s In: %s\n", TypeSymbolTable.get(w).scope(),
                    TypeSymbolTable.get(w).name(), TypeSymbolTable.get(w).type(),
                    TypeSymbolTable.get(w).attr(), TypeSymbolTable.get(w).index());
        }
        
        System.out.println("Func Symbol Table:");
        for (int w = 0; w < funcvar_no; w++) {
            System.out.printf("S: %s  F_Var_N: %s  T: %s "
                            + "A: %s rno:%d \n", FuncSymbolTable.get(w).scope(),
                    FuncSymbolTable.get(w).name(), FuncSymbolTable.get(w).type(),
                    FuncSymbolTable.get(w).attr(), FuncSymbolTable.get(w).reg_no());
        }
        
    }
    
}
