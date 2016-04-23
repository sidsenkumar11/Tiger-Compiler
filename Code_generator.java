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

	
public static String checkfactor(String[] fullFileText_token, int b,boolean RHSfound, String RHS_value, int LHS_value, ArrayList<SymbolTableEntry> FuncSymbolTable) {
		if (fullFileText_token[b + 3].equals("const")) {
			RHS_value = "#"+ fullFileText_token[b + 4];
			RHSfound = true;
			//LHS_value = Integer.toString(-1);
			return RHS_value;
		}
		else {
			for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
		      if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
		      	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
		      	System.out.println(LHS_value);
		      	RHSfound = false;
		      	return (Integer.toString(LHS_value));
		      	
		      }

		}
		}
		return null;
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
      else {
      	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
              // Need to check if type of this factor matches that of function.
              if (fullFileText_token[b + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name())) {
              	RHS_source = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
              	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
            	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
              	//System.out.println(LHS_value);
              	//System.out.println(LHS_source);

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
         	//System.out.println("Entered call1 loop"); 
        	//reg_counter++;
    	  if(linop!= null || nonlinop != null) reg_counter = semicolon_comm_print(reg_counter, null, linop, nonlinop,linopFound, nonlinopFound, isInteger_RHS, imm_value, isInteger_LHS, RHS_value, LHS_value);
            if(linop!= null || nonlinop != null) LHS_value = reg_counter;
    	  if(func_call_var != -1 && func_call_label!=null) System.out.printf("call_ret r%d %s(r%d)\n",func_call_var,func_call_label, LHS_value);
            else System.out.printf("call %s(r%d)\n",func_call_label, LHS_value);
            //else System.out.printf("here chu");
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
    boolean isInteger_RHS1=true;
	boolean isInteger_RHS2=true;
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
                                                                                 
        case "factor": {
        	//System.out.printf(fullFileText_token[l + 1]);
      if (fullFileText_token[l + 3].equals("const")) {
      	//System.out.printf(fullFileText_token[l + 4]);
      	RHS_imm = "#"+ fullFileText_token[l + 4];
      	imm_value = true;
      	//System.out.println(RHS_imm);
        isInteger_RHS = !RHS_imm.contains(".");
        //System.out.println(f_scope);                                           	
      }
      else {
    	  //System.out.println(f_scope); 
      	for (int funcSymbolTableIndex = 0; funcSymbolTableIndex < FuncSymbolTable.size(); funcSymbolTableIndex++) {
             
              if (fullFileText_token[l + 1].equals(FuncSymbolTable.get(funcSymbolTableIndex).name()) && src1_written == false 
            		  && f_scope.equals(FuncSymbolTable.get(funcSymbolTableIndex).scope())) {
              	RHS_src1 = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
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
                semicolonFound = true;
                //System.out.println(RHS_src1);
                if(linopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                	//System.out.printf("%s r%d r%d r%d \n",linop, LHS_value, RHS_src1, RHS_src2);
                	if(isInteger_RHS1 == false || isInteger_RHS2 == false) System.out.printf("%sf r%d r%d r%d \n",linop,LHS_value, RHS_src1, RHS_src2);
                	else System.out.printf("%si r%d r%d r%d \n",linop, LHS_value, RHS_src1, RHS_src2);
                 
                	}
                else if(nonlinopFound == true && RHS_src1!=-1 && RHS_src2!=-1 && LHS_value !=-1) {
                	//System.out.printf("%s r%d r%d r%d \n",nonlinop, LHS_value, RHS_src1, RHS_src2);
                	if(isInteger_RHS1 == false || isInteger_RHS2 == false) System.out.printf("%sf r%d r%d r%d \n",nonlinop, LHS_value,  RHS_src1, RHS_src2);
                	else System.out.printf("%si r%d r%d r%d \n",nonlinop, LHS_value,  RHS_src1, RHS_src2);

                }
                else if((nonlinopFound == true || linopFound== true) && RHS_src1!=-1 && imm_value==true && LHS_value !=-1) {
                	//System.out.printf("%s r%d r%d r%d \n",nonlinop, LHS_value, RHS_src1, RHS_src2);
                	if(isInteger_RHS1 == false || RHS_imm.contains(".")) System.out.printf("%simmf r%d r%d %s \n",nonlinop, LHS_value,  RHS_src1, RHS_imm);
                	else System.out.printf("%simmi r%d r%d %s \n",nonlinop, LHS_value,  RHS_src1, RHS_imm);

                }
                else {
                	//System.out.println("Entered 1\n");
                	if(imm_value == true) {
                		if(isInteger_LHS == false || RHS_imm.contains(".")) System.out.printf("movimmf r%d %s \n", LHS_value, RHS_imm);
                		else System.out.printf("movimmi r%d %s \n", LHS_value, RHS_imm);
                	} else {
                		if(isInteger_LHS == false || isInteger_RHS1 == false) System.out.printf("movf r%d r%d \n", LHS_value, RHS_src1);
                		else System.out.printf("movi r%d r%d \n", LHS_value, RHS_src1);
                	}
                	
                }
                RHS_src1 = -1;
                RHS_src2 = -1;
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
                        	LHS_value = FuncSymbolTable.get(funcSymbolTableIndex).reg_no();
                          	isInteger_LHS = (FuncSymbolTable.get(funcSymbolTableIndex).type()).contains("int");
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
            
            case "then":{
            	//System.out.println(f_scope);
//            	System.out.println(LHS_value);
//            	System.out.println(RHS_value);
            	if(both_imm == false) System.out.printf("%s r%d %s %s%d \n",boolop, LHS_value, RHS_value,f_scope, label_counter);
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














	
	
	public static void main(String[] args) {
		// String fileName = args[0];
		String fileName = "test3.ast";
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
                                 //   		System.out.printf("Entered while loop \n");
                                    		break;

                                    	}
                                    	case "for": {
                                   // 		System.out.printf("Entered for loop \n");
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
                	//System.out.println("Entered in");
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
