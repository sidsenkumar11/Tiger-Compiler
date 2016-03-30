
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Runner {
	
	public static void main(String[] args) {
		String fileName = "test0.ast";
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
	    
	    Runner.typeCheck(fullFileText);
	}
	
	import sun.applet.Main;

	import java.util.ArrayList;
	import java.util.Arrays;

	public class TypeChecker {
	
    public static void typeCheck(String ast) {
        String fullFileText_token[] = ast.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))");
        int k = 4;
        int p = 4;
        int funcvar_no = 0;
        int typevar_no = 0;
        int T_index = 0;
        String var_type = null;
        String scope = "program";
        String f_scope= null;
        String T_type;
        ArrayList<SymbolTableEntry> MainSymbolTable = new ArrayList<>();
        ArrayList<TypeTableEntry> TypeSymbolTable = new ArrayList<>();
        ArrayList<SymbolTableEntry> FuncSymbolTable = new ArrayList<>();

        String[] m_var_name = new String[100];
        String[] type_name = { "int", "float", "int array", "float array" };

        // Assigning int, float, int array and float arrray type to the object
        if (typevar_no < 4) {
            for (int j = 0; j < 4; j++) {
                TypeSymbolTable.add(new TypeTableEntry(scope, type_name[j], type_name[j], "type", 0));
                System.out.printf("Scope: %s  T_Var_Name: %s  Type: %s "
                                + "Attr: %s  Index: %d\n", TypeSymbolTable.get(typevar_no).scope(),
                        TypeSymbolTable.get(typevar_no).name(), TypeSymbolTable.get(typevar_no).type(),
                        TypeSymbolTable.get(typevar_no).attr(), TypeSymbolTable.get(typevar_no).index());
                typevar_no++;
            }
        }

        // Assigning int, float, int array and float array type to the object
       // if (k < 4) {
        	MainSymbolTable.add(new SymbolTableEntry(scope, "readi", "int", "func"));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "readf", "float", "func"));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "printi", "void", "func"));
        	MainSymbolTable.add(new SymbolTableEntry(scope, "printf", "void", "func"));
        	
        	FuncSymbolTable.add(new SymbolTableEntry("printi", "input", "int", "id"));
            System.out.printf("Scope: %s  F_Var_Name: %s \tType: %s \t"
					+ "Attr: %s\n", FuncSymbolTable.get(funcvar_no).scope(),
							FuncSymbolTable.get(funcvar_no).name(), FuncSymbolTable.get(funcvar_no).type(),
							FuncSymbolTable.get(funcvar_no).attr());
            funcvar_no++;
        	FuncSymbolTable.add(new SymbolTableEntry("printf", "input", "float", "id"));
            System.out.printf("Scope: %s  F_Var_Name: %s \tType: %s \t"
            						+ "Attr: %s\n", FuncSymbolTable.get(funcvar_no).scope(),
            								FuncSymbolTable.get(funcvar_no).name(), FuncSymbolTable.get(funcvar_no).type(),
            								FuncSymbolTable.get(funcvar_no).attr());
            funcvar_no++;
            //for (int j = 0; j < 4; j++) {
                
              //  System.out.printf("Scope: %s  M_Var_Name: %s  Type: %s "
              //                  + "Attr: %s\n", MainSymbolTable.get(j).scope(),
              //          MainSymbolTable.get(j).name(), MainSymbolTable.get(j).type(),
              //          MainSymbolTable.get(j).attr());
                //k++;
          //  }
       // }
        
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
                        System.out.printf("Scope: %s  T_Var_Name: %s  Type: %s "

                                        + "Attr: %s  Index: %d\n", TypeSymbolTable.get(typevar_no).scope(),
                                TypeSymbolTable.get(typevar_no).name(), TypeSymbolTable.get(typevar_no).type(),
                                TypeSymbolTable.get(typevar_no).attr(), TypeSymbolTable.get(typevar_no).index());
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
                    // System.out.println("Entered vardecl");
                    boolean var_loop_stop = false;
                    for (int j = i; !var_loop_stop; j++) {
                        // System.out.println(fullFileText_token[j]);
                        switch (fullFileText_token[j]) {
                            case "ids": {
                                m_var_name[k] = fullFileText_token[j + 1];
                                // System.out.println(var_name[k]);
                                k++;
                                break;
                            }
                            case "type": {
                                for (; p < k; p++) {
                                    // System.out.println(fullFileText_token[j+1]);
                                    // int t =0;
                                    // Checking if the type is correct
                                    boolean var_type_match = false;                                 
                                    
                                    var_type = fullFileText_token[j + 1];

                                    //System.out.println(var_type);
                                    for (int r = 0; r < typevar_no && !var_type_match; r++) {
                                        if (var_type.equals(TypeSymbolTable.get(r).name())) {
                                            // T_type = fullFileText_token[i+8];
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
                                    // System.out.println(m_var_name[p]);
                                    // System.out.println(p);

                                    if (var_type_match) {
                                        if (fullFileText_token[j + 1].matches("int")
                                                && fullFileText_token[j + 7].matches(":=")) {
                                            // System.out.println(fullFileText_token[j+1]);
                                            boolean isInteger = !fullFileText_token[j + 11].contains(".");
                                            // System.out.println(isInteger);
                                            if (!isInteger) {
                                                System.out.println("Initialized variable Type Mismatch: " + fullFileText_token[j + 1]);
                                                System.exit(0);
                                            }
                                        }
                                        
                                        MainSymbolTable.add(
                                                new SymbolTableEntry(scope, m_var_name[p], var_type, "id"));

                                        System.out.printf("Scope: %s  M_Var_Name: %s \tType: %s \t"

                                                        + "Attr: %s\n", MainSymbolTable.get(p).scope(),

                                                MainSymbolTable.get(p).name(), MainSymbolTable.get(p).type(),
                                                MainSymbolTable.get(p).attr());
                                        // break;
                                    } else {
                                        System.out.println("Variable Type Mismatch: " + fullFileText_token[j + 1]);
                                        System.exit(0);
                                    }
                                    // }
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

                    // System.out.println("Entered funcdecl");

                    boolean f_loop_stop = false;
                    SymbolTableEntry functionEntry = null;
                    for (int j = i; !f_loop_stop; j++) {
                        switch (fullFileText_token[j]) {
                            case "func": {
                                // System.out.println(fullFileText_token[j]);
                                m_var_name[k] = fullFileText_token[j + 1];
                                f_scope = fullFileText_token[j + 1];
                                i = j;
                                break;
                            }
                            // Checking for variable declaration in a function
                            case "param": {

                                // System.out.println("Entered Param");
                             // Checking if the type is correct for function
                            	var_type = fullFileText_token[j + 7];
                                boolean funcvar_type_match = false;
                                for (int r = 0; r < typevar_no && !funcvar_type_match; r++) {
                                    if (fullFileText_token[j + 7].equals(TypeSymbolTable.get(r).name())) {
                                        // T_type = fullFileText_token[i+8];
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
                                    FuncSymbolTable.add(new SymbolTableEntry(f_scope, fullFileText_token[j + 1],
                                            fullFileText_token[j + 7], "id"));
                                    System.out.printf("Scope: %s  \tF_Var_Name: %s  \tType: %s \t"

                                                    + "Attr: %s\n", FuncSymbolTable.get(funcvar_no).scope(),
                                            FuncSymbolTable.get(funcvar_no).name(), FuncSymbolTable.get(funcvar_no).type(),
                                            FuncSymbolTable.get(funcvar_no).attr());
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

                                //System.out.println(f_scope);
                                MainSymbolTable.add(new SymbolTableEntry(scope, f_scope, "", "func"));
                                if(fullFileText_token[j+5].matches("type")) {

                                    MainSymbolTable.get(k).setType(fullFileText_token[j+6]);
                                } else {
                                    MainSymbolTable.get(k).setType("void");
                                }

                                functionEntry = MainSymbolTable.get(k);
                                boolean func_type_match=false;

                                //System.out.println(MainSymbolTable.get(k).type());

                                for(int r=0; r< typevar_no;r++) {

                                    if((MainSymbolTable.get(k).type().equals(TypeSymbolTable.get(r).name()) || (MainSymbolTable.get(k).type()).matches("void")) && func_type_match==false) {

                                        func_type_match = true;

                                        break;

                                    }

                                }

                                if(func_type_match == true) {

                                    MainSymbolTable.get(k).setName(f_scope);
                                    System.out.printf("Scope: %s  M_Var_Name: %s "
                                                    + "Type: %s \tAttr: %s\n",MainSymbolTable.get(k).scope(),
                                            MainSymbolTable.get(k).name(), MainSymbolTable.get(k).type(),
                                            MainSymbolTable.get(k).attr());
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
                                
                                boolean foundFactor = false;
                                boolean functionFinished = false;
                                for (int m = i; !functionFinished; m++) {
                                    switch (fullFileText_token[m]) {
                                        case "return": {
                                            // Make sure return type matches function type
                                            boolean semicolonFound = false;
                                            for (int l = m; !semicolonFound; l++) {
                                                switch (fullFileText_token[l]) {
                                                    case "factor": {
                                                        foundFactor = true;
                                                        if (fullFileText_token[l + 3].equals("const")) {
                                                            String theConstant = fullFileText_token[l + 4];
                                                            if (theConstant.contains(".")) {
                                                                // It is a float
                                                                if (!functionEntry.type().equals("float") && functionEntry.type().equals("int")) {
                                                                    System.out.println("Const " + theConstant + " must be a " + functionEntry.type());
                                                                    System.exit(0);
                                                                }
                                                            } else {
                                                                // It is an integer
                                                                if (!functionEntry.type().equals("int")) {
                                                                    System.out.println("Const " + theConstant + " must be a " + functionEntry.type());
                                                                    System.exit(0);
                                                                }
                                                            }
                                                        } else {

                                                            for (int mainSymbolTableIndex = 0; mainSymbolTableIndex < MainSymbolTable.size(); mainSymbolTableIndex++) {
                                                                // Need to check if type of this factor matches that of function.
                                                                if (fullFileText_token[l + 1].equals(MainSymbolTable.get(mainSymbolTableIndex).name())) {
                                                                    SymbolTableEntry entry = MainSymbolTable.get(mainSymbolTableIndex);
                                                                    if (entry.scope().equals(functionEntry.name()) ||
                                                                            entry.scope().equals("program")) {
                                                                        // Variable is in scope
                                                                        if (functionEntry.type().equals("float")) {
                                                                            // Accept both floats and ints
                                                                            if (!entry.type().equals("float") && !entry.type().equals("int")) {
                                                                                System.out.println("Variable " + fullFileText_token[l+1] + " does not match return type of function.");
                                                                                System.exit(0);
                                                                            }
                                                                        } else {
                                                                            if (!entry.type().equals(functionEntry.type())) {
                                                                                System.out.println("Variable " + fullFileText_token[l+1] + " does not match return type of function.");
                                                                                System.exit(0);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        System.out.println("Variable " + fullFileText_token[l+1] + " out of scope.");
                                                                        System.exit(0);
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    case ";": {
                                                        semicolonFound = true;
                                                        break;
                                                    }
                                                    default: {
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                        case "lvalue": {
                                            // Check if RHS of assignment is valid
                                            String variableName = fullFileText_token[m + 1];
                                            String LHSType = null;
                                            for (int symbolIndex = 0; symbolIndex < MainSymbolTable.size() && LHSType == null; symbolIndex++) {
                                                if (MainSymbolTable.get(symbolIndex).name().equals(variableName)) {
                                                    LHSType = MainSymbolTable.get(symbolIndex).type();
                                                }
                                            }

                                            if (LHSType == null) {
                                                System.out.println("Variable " + variableName + " not declared.");
                                                System.exit(0);
                                            }

                                            boolean semicolonFound = false;
                                            for (int l = m; !semicolonFound; l++) {
                                                switch (fullFileText_token[l]) {
                                                    case "": {

                                                    }
//                                                    case "": {
//
//                                                    }
                                                    case ";": {
                                                        semicolonFound = true;
                                                        break;
                                                    }
                                                    default: {
                                                        break;
                                                    }
                                                }
                                            }

                                            break;
                                        }
                                        case "end": {
                                            functionFinished = true;
                                            if (!foundFactor && !functionEntry.type().equals("void")) {
                                                System.out.println("Function " + functionEntry.name() + " never returns a(n) " + functionEntry.type());
                                                System.exit(0);
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
                    // System.out.println(fullFileText_token[i]);
                    break;
                }
                
                
            }
            

        }
        System.out.println("Main Symbol Table:");
        for (int w = 0; w < k; w++) {
            System.out.printf("Scope: %s  M_Var_Name: %s  Type: %s "
                            + "Attr: %s\n", MainSymbolTable.get(w).scope(),
                    MainSymbolTable.get(w).name(), MainSymbolTable.get(w).type(),
                    MainSymbolTable.get(w).attr());
        }
        
        System.out.println("Type Symbol Table:");
        for (int w = 0; w < typevar_no; w++) {
            System.out.printf("Scope: %s  T_Var_Name: %s  Type: %s "
                            + "Attr: %s Index: %s\n", TypeSymbolTable.get(w).scope(),
                    TypeSymbolTable.get(w).name(), TypeSymbolTable.get(w).type(),
                    TypeSymbolTable.get(w).attr(), TypeSymbolTable.get(w).index());
        }
        
        System.out.println("Func Symbol Table:");
        for (int w = 0; w < funcvar_no; w++) {
            System.out.printf("Scope: %s  F_Var_Name: %s  Type: %s "
                            + "Attr: %s\n", FuncSymbolTable.get(w).scope(),
                    FuncSymbolTable.get(w).name(), FuncSymbolTable.get(w).type(),
                    FuncSymbolTable.get(w).attr());
        }
        
    }
    
}
