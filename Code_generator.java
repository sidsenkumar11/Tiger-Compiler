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

	
//	public static String checkfactor(String[] fullFileText_token, int b, String RHS_value, int LHS_value) {
//		if (fullFileText_token[b + 3].equals("const")) {
//			RHS_value = "#"+ fullFileText_token[b + 4];                                                    	
//		}
//		else {
//			for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
//		      if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
//		      	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
//		      }
//
//		}
//		}
//		//System.out.printf("Entered numexpr loop \n");
//		break;
//		}
	
	
	
	public static void main(String[] args) {
		// String fileName = args[0];
		String fileName = "factorial.ast";
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
        boolean linopFound=false;
        boolean nonlinopFound = false;
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
                    for (int j = i; !var_loop_stop; j++) {
                        switch (fullFileText_token[j]) {
                            case "ids": {
                                m_var_name[k] = fullFileText_token[j + 1];
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
                                        	System.out.printf("loadi r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                        }
                                        if(var_type.matches("float"))
                                        {
                                        	System.out.printf("loadf r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                        }
                                        if(var_type.matches("int array"))
                                        {
                                        	System.out.printf("loadarri r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
                                        }
                                        if(var_type.matches("float array"))
                                        {
                                        	System.out.printf("loadarrf r%d %s \n",MainSymbolTable.get(p).reg_no(),MainSymbolTable.get(p).name());
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
                    boolean f_loop_stop = false;
                    SymbolTableEntry functionEntry = null;
                    for (int j = i; !f_loop_stop; j++) {
                        switch (fullFileText_token[j]) {
                            case "func": {

                                m_var_name[k] = fullFileText_token[j + 1];
                                System.out.printf("label %s:\n", m_var_name[k]);
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
                                    System.out.printf("move r%d %s \n", reg_no.get(reg_counter).reg_index(),fullFileText_token[j + 1]);
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
                            	reg_counter++;
                            	reg_no.add(new reg_no(reg_counter, 0));
                                MainSymbolTable.add(new SymbolTableEntry(scope, f_scope, "", "func",reg_no.get(reg_counter).reg_index()));
                                if(fullFileText_token[j+5].matches("type")) {

                                    MainSymbolTable.get(k).setType(fullFileText_token[j+6]);
                                } else {
                                    MainSymbolTable.get(k).setType("void");
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
                                    j = j + 6;
                                    k++;

                                } else {

                                    System.out.println("Function Type Mismatch: " + MainSymbolTable.get(k).name());
                                    System.exit(0);
                                }
                                break;
                            }

                            // Checking the end of function declaration
                            case "begin": {
                                f_loop_stop = true;
                            	boolean boolopFound = false;
                            	linopFound = false;
                            	nonlinopFound = false;
                            	String RHS_value = null;
                            	int LHS_value = 0;
                                String boolop= null;
                                String linop= null;
                                String nonlinop= null;
                                boolean foundFactor = false;
                                boolean functionFinished = false;
                                for (int m = i; !functionFinished; m++) {
                                    //System.out.println(fullFileText_token[m]);

                                    switch (fullFileText_token[m]) {
                                    	case "if": {
                                    		//System.out.printf("Entered if loop \n");
                                            boolean endifFound = false;
                                            for (int l = m; !endifFound; l++) {
                                                //System.out.println(fullFileText_token[l]);
                                                switch (fullFileText_token[l]) {
                                                    case "factor": {
                                                        if (fullFileText_token[l + 3].equals("const")) {
                                                        	//System.out.printf(fullFileText_token[l + 4]);
                                                        	RHS_value = "#"+ fullFileText_token[l + 4];                                                    	
                                                        }
                                                        else {
                                                        	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                                                                // Need to check if type of this factor matches that of function.
                                                                if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                                                                	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                                                                }

                                                        }
                                                        }
                                                		//System.out.printf("Entered numexpr loop \n");
                                                		break;
                                                    }
                                                    case "boolop":{
                                                		//System.out.printf("Entered boolop loop \n");
                                                    	boolopFound = true;
                                                		switch(fullFileText_token[l+1]) {
                                                		case "=": {
                                                			boolop = "brneq";
                                                			//breq,brneq
                                                			break;
                                                		}
                                                		case ">": {
                                                			boolop = "brleq";
                                                			//brgt
                                                			break;
                                                		}
                                                		case "<": {
                                                			boolop = "brgeq";
                                                			//brlt
                                                			break;
                                                		}
                                                		case ">=": {
                                                			boolop = "brlt";
                                                			//brgeq
                                                			break;
                                                		}
                                                		case "<=": {
                                                			boolop = "brgt";
                                                			//brleq
                                                			break;
                                                		}
                                                		case "<>": {
                                                			// Need to check this operator
                                                			break;
                                                		}
                                                		}
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
                                                    
                                                    case "then":{
                                                    	System.out.printf("%s r%d %s label%d \n",boolop, LHS_value, RHS_value, label_counter);
                                                    	if(linopFound == true) System.out.printf(linop);
                                                    	if(nonlinopFound == true) System.out.printf(nonlinop);
                                                    }
                                                    
                                                    
                                                    case "return": {
                                                        // Make sure return type matches function type
                                                        boolean semicolonFound = false;
                                                        functionFinished = false;
                                                        for (int b = l; !semicolonFound; b++) {
                                                            switch (fullFileText_token[b]) {
                                                            

                                                            case "factor": {
                                                          if (fullFileText_token[b + 3].equals("const")) {
                                                          	//System.out.printf(fullFileText_token[l + 4]);
                                                          	RHS_value = "#"+ fullFileText_token[b + 4];                                                    	
                                                          }
                                                          else {
                                                          	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                                                                  // Need to check if type of this factor matches that of function.
                                                                  if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                                                                  	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
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
                                                                        // Need to check if type of this factor matches that of function.
                                                                		//System.out.println(f_scope);
                                                                		//System.out.println(MainSymbolTable.get(MainSymbolTableIndex).name());
                                                                        if (f_scope.equals(MainSymbolTable.get(MainSymbolTableIndex).name())) {
                                                                        	LHS_value = MainSymbolTable.get(MainSymbolTableIndex).reg_no();
                                                                        }
                                                                	}
                                                                    semicolonFound = true;
                                                                    if(linopFound == true && RHS_value!=null && LHS_value !=-1) {
                                                 
                                                                    	System.out.printf("%s r%d %s \n",linop, LHS_value, RHS_value);
                                                                    	System.out.printf("B end");
                                                                    	linopFound = false;
                                                                    	linop = null;
                                                                        LHS_value = -1;
                                                                        RHS_value = null;
                                                                    	}
                                                                    else if(nonlinopFound == true && RHS_value!=null && LHS_value !=-1) {
                                                                    	System.out.printf("%s r%d %s \n",nonlinop, LHS_value, RHS_value);
                                                                    	System.out.printf("B end");
                                                                    	nonlinopFound = false;
                                                                    	nonlinop = null;
                                                                        LHS_value = -1;
                                                                        RHS_value = null;
                                                                    }
                                                                    else {
                                                                    	System.out.printf("move r%d %s \n", LHS_value, RHS_value);
                                                                    	System.out.printf("B end \n");
                                                                    }
                                                                    
                                                                 
                                                               

                                                                    break;
                                                                }
                                                                default: {
                                                                    break;
                                                                }

                                                            }
                                                            l=b;
                                                            }
                                                      
                                                        }	
                                                    
                                                    case "else": {
                                                     // System.out.println("Enters else loop \n");

                                                    	
                                                    }
                                                    case "endif": {
                                                       // System.out.println("Enters endif loop \n");
                                                    	endifFound = true;
                                                        System.out.printf("label %s:\n", label_counter);
                                                        label_counter++;
                                                        break;
                                                    }
                                                    	
                                                }
                                                
                                                m=l;

                                            }
//                                            System.out.println(fullFileText_token[m-2]);
//                                            System.out.println(fullFileText_token[m-1]);
//                                            System.out.println(fullFileText_token[m]);
//                                            System.out.println(fullFileText_token[m+1]);
//                                            System.out.println(fullFileText_token[m+2]);
//                                            System.out.println(fullFileText_token[m+3]);
//                                            System.out.println(fullFileText_token[m+4]);
//                                            System.out.println(fullFileText_token[m+5]);
                                    		break;
                                    	}
                                    		
                                        case "end": {
                                            System.out.printf("label end\n");
                                            functionFinished = true;

                                   		//System.out.printf("Entered end loop \n");
//                                            if (!foundFactor && !functionEntry.type().equals("void")) {
//                                                System.out.println("Function " + functionEntry.name() + " never returns a(n) " + functionEntry.type());
//                                                System.exit(0);
//                                            }
                                            break;
                                        }
                                    	
                                    	case "while": {
                                 //   		System.out.printf("Entered while loop \n");
                                    		break;

                                    	}
                                    	case "for": {
                                   // 		System.out.printf("Entered for loop \n");
                                    		break;

                                    	}
                                 
                                    
                                        case "return": {
                                    		//System.out.printf("Entered return loop \n");
                                            // Make sure return type matches function type
                                            boolean semicolonFound = false;
                                            boolean src1_written = false;
                                            boolean src2_written = false;
                                            functionFinished = false;
                                            String RHS_imm = null;
                                            int RHS_src1 =-1;
                                            int RHS_src2 = -1;
                                            for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size(); symbolIndex++) {
                                                if (MainSymbolTable.get(symbolIndex).name().equals(f_scope)) {
                                                    LHS_value = MainSymbolTable.get(symbolIndex).reg_no();
                                                }
                                            }
                                            
                                            
                                            for (int l = m; !semicolonFound; l++) {
                                                switch (fullFileText_token[l]) {
                                                
                                                case "end": {
                                                   // functionFinished = true;
                                                    if (!foundFactor && !functionEntry.type().equals("void")) {
                                                        System.out.println("Function " + functionEntry.name() + " never returns a(n) " + functionEntry.type());
                                                        System.exit(0);
                                                    }
                                                    break;
                                                }
                                                case "factor": {
                                                	//System.out.printf(fullFileText_token[l + 1]);
                                              if (fullFileText_token[l + 3].equals("const")) {
                                              	//System.out.printf(fullFileText_token[l + 4]);
                                              	RHS_imm = "#"+ fullFileText_token[l + 4];                                                    	
                                              }
                                              else {
                                              	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                                                     
                                                      if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src1_written == false) {
                                                      	RHS_src1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                                                      	src1_written = true;
                                                      } else if(fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src2_written == false && src1_written == true){
                                                        RHS_src2 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                                                        src2_written = true;
                                                      }
  
                                              }
                                              	for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
                                                    
                                                    if (fullFileText_token[l + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src1_written == false) {
                                                    	RHS_src1 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
                                                    	src1_written = true;
                                                    } else if(fullFileText_token[l + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name()) && src2_written == false && src1_written == true){
                                                      RHS_src2 = MainSymbolTable.get(mainSymbolTableIndex).reg_no();
                                                      src2_written = true;
                                                    }

                                            }
                                              }
                                      		//System.out.printf("Entered numexpr loop \n");
                                      		break;
                                          }
                                              	 case "linop":{
                                                 	linop = checklinop(fullFileText_token, l, linop,linopFound);
                                                 	linopFound=true;
                                              		break;
                                                  }
                                                  
                                                  case "nonlinop":{
                                                  	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);
                                                  	nonlinopFound=true;
                                              		break;
                                                  }	
                                             	
                                                    case ";": {
                                                    	//System.out.println(linopFound);
                                                    	//System.out.println(RHS_src1);
                                                    	//System.out.println(RHS_src2);
                                                    	//System.out.println(LHS_value);
                                                    	//System.out.println(nonlinopFound);
                                                        semicolonFound = true;
                                                        if(linopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                                                        	System.out.printf("%s r%d r%d r%d \n",linop, LHS_value, RHS_src1, RHS_src2);
                                                        	linopFound = false;
                                                        	linop = null;
                                                            LHS_value = -1;
                                                            RHS_src1 = -1;
                                                            RHS_src2 = -1;
                                                        	}
                                                        else if(nonlinopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                                                        	System.out.printf("%s r%d r%d r%d \n",nonlinop, LHS_value, RHS_src1, RHS_src2);
                                                        	nonlinopFound = false;
                                                        	nonlinop = null;
                                                            LHS_value = -1;
                                                            RHS_src1 = -1;
                                                            RHS_src2 = -1;
                                                        }
                                                        else {
                                                        	System.out.printf("move r%d %s \n", LHS_value, RHS_imm);
                                                        }
                                                        
                                                     
                                                   

                                                        break;
                                                    }
                                                    default: {
                                                        break;
                                                    }
                                                }
                                                }
                                            }
                                        
                                        case "lvalue": {
                                    		//System.out.printf("Entered lvalue loop \n");
                                            // Check if RHS of assignment is valid
                                            String variableName = fullFileText_token[m + 1];
                                           // String LHSType = null;
                                            for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size(); symbolIndex++) {
                                                if (MainSymbolTable.get(symbolIndex).name().equals(variableName)) {
                                                    LHS_value = MainSymbolTable.get(symbolIndex).reg_no();
                                                }
                                            }
                                            String func_call_label = null;
                                            int RHS_source = -1;
                                            boolean semicolonFound = false;
                                            for (int l = m; !semicolonFound; l++) {
                                            	//System.out.println(fullFileText_token[l]);
                                                switch (fullFileText_token[l]) {
                                                case ":=": {
                                                	
                                              		
                                                    for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size(); symbolIndex++) {
                                                        if (MainSymbolTable.get(symbolIndex).name().equals(fullFileText_token[l+2])) {
                                                            func_call_label = MainSymbolTable.get(symbolIndex).name();
                                                        }
                                                    }

                                                }
                                                	
                                                case "end": {
                                                    //functionFinished = true;
                                                    break;
                                                }
                                                case "factor": {
                                              if (fullFileText_token[l + 3].equals("const")) {
                                              	//System.out.println(fullFileText_token[l + 4]);
                                              	RHS_value = "#"+ fullFileText_token[l + 4];                                                    	
                                              }
                                              else {
                                              	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
                                                      // Need to check if type of this factor matches that of function.
                                                      if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
                                                      	RHS_source = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                                                      	//System.out.println(RHS_source);

                                                      }
  
                                              }
                                              }
                                      		break;
                                          }
                                              	 case "linop":{
                                                   
                                                 	linop = checklinop(fullFileText_token, l, linop,linopFound);
                                                	linopFound = true;
                                                 	break;
                                                  }
                                                  
                                                  case "nonlinop":{
                                                  	nonlinop = checknonlinop(fullFileText_token, l, nonlinop,nonlinopFound);                                                    	
                                                  	break;
                                                  }	
                                             	
                                                    case ";": {

                                                    
                                                        semicolonFound = true;
                                                        if(linopFound == true && RHS_value!=null && LHS_value !=-1) {

                                                        	System.out.printf("%s r%d r%d %s \n",linop, LHS_value,RHS_source, RHS_value);
                                                        	System.out.printf("call %s\n",func_call_label);
                                                        	linopFound = false;
                                                        	linop = null;
                                                            LHS_value = -1;
                                                            RHS_value = null;
                                                        	}
                                                        if(nonlinopFound == true && RHS_value!=null && LHS_value !=-1) {
                                                        	System.out.printf("%s r%d r%d r%d \n",nonlinop, LHS_value, RHS_source, RHS_value);
                                                        	System.out.printf("call %s",func_call_label);
                                                        	nonlinopFound = false;
                                                        	nonlinop = null;
                                                            LHS_value = -1;
                                                            RHS_value = null;
                                                        }
                                                     
                                                   

                                                        break;
                                                    }
                                                    default: {
                                                        break;
                                                    }
                                                }
                                                m=l;
                                            }

                                            break;
                                        }

                                        default: {
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        i = j;
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
