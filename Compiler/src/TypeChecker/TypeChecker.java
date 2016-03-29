import java.util.ArrayList;
import java.util.Arrays;

public class TypeChecker {

    public static void typeCheck(String ast) {
        String fullFileText_token[] = ast.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))");
        int k = 0;
        int p = 0;
        int funcvar_no = 0;
        int typevar_no = 0;
        int T_index = 0;
        String scope = "program";
        String T_type;
        ArrayList<SymbolTableEntry> MainSymbolTable = new ArrayList<>();
        ArrayList<TypeTableEntry> TypeSymbolTable = new ArrayList<>();
        String[] m_var_name = new String[100];
        String[] type_name = { "int", "float", "int array", "float array" };

        // Assigning int and float type to the object
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

                    // Checking if the type is present in the type table
                    for (int j = 0; j < typevar_no && !Type_match; j++) {
                        if (T_type.equals(TypeSymbolTable.get(j).name())) {
                            // T_type = fullFileText_token[i+8];
                            Type_match = true;
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
                        System.out.println("Type Mismatch\n");
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

                                    for (int r = 0; r < typevar_no && !var_type_match; r++) {
                                        if (fullFileText_token[j + 1].equals(TypeSymbolTable.get(r).name())) {
                                            // T_type = fullFileText_token[i+8];
                                            var_type_match = true;
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
                                                System.out.println("Initialized variable Type Mismatch\n");
                                            }
                                        }

                                        MainSymbolTable.add(
                                                new SymbolTableEntry(scope, m_var_name[p], fullFileText_token[j + 1], "id"));

                                        System.out.printf("Scope: %s  M_Var_Name: %s \tType: %s \t"

                                                        + "Attr: %s\n", MainSymbolTable.get(p).scope(),

                                                MainSymbolTable.get(p).name(), MainSymbolTable.get(p).type(),
                                                MainSymbolTable.get(p).attr());
                                        // break;
                                    } else {
                                        System.out.println("Variable Type Mismatch\n");
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

                    for (int j = i; !f_loop_stop; j++) {
                        switch (fullFileText_token[j]) {
                            case "func": {
                                // System.out.println(fullFileText_token[j]);
                                m_var_name[k] = fullFileText_token[j + 1];
                                i = j;
                                break;
                            }
                            // Checking for variable declaration in a function
                            case "param": {

                                // System.out.println("Entered Param");

                                String f_scope = m_var_name[k];

                                ArrayList<SymbolTableEntry> FuncSymbolTable = new ArrayList<>();

                                // Checking if the type is correct for function

                                boolean funcvar_type_match = false;
                                for (int r = 0; r < typevar_no && !funcvar_type_match; r++) {
                                    if (fullFileText_token[j + 7].equals(TypeSymbolTable.get(r).name())) {
                                        // T_type = fullFileText_token[i+8];
                                        funcvar_type_match = true;
                                    }
                                }

                                if (funcvar_type_match) {
                                    FuncSymbolTable.add(new SymbolTableEntry(f_scope, fullFileText_token[j + 1],
                                            fullFileText_token[j + 7], "id"));

                                    System.out.printf("Scope: %s  \tF_Var_Name: %s  \tType: %s \t"

                                                    + "Attr: %s\n", FuncSymbolTable.get(funcvar_no).scope(),

                                            FuncSymbolTable.get(funcvar_no).name(), FuncSymbolTable.get(funcvar_no).type(),

                                            FuncSymbolTable.get(funcvar_no).attr());
                                    j = j + 7;
                                } else {
                                    System.out.println("Function Variable Type Mismatch\n");
                                }
                                break;
                            }

                            // Checking type for the function declared

                            case "optrettype": {
                                MainSymbolTable.add(new SymbolTableEntry(scope, m_var_name[k], "", "func"));

                                if (fullFileText_token[j + 5].matches("type")) {
                                    boolean func_type_match = false;
                                    for (int r = 0; r < typevar_no && !func_type_match; r++) {
                                        if (fullFileText_token[j + 6].equals(TypeSymbolTable.get(r).name())) {
                                            // T_type = fullFileText_token[i+8];
                                            func_type_match = true;
                                        }
                                    }
                                    if (func_type_match) {
                                        MainSymbolTable.get(k).setType(fullFileText_token[j + 6]);
                                        j = j + 6;
                                    } else {
                                        System.out.println("Function Type Mismatch\n");
                                    }
                                } else {
                                    MainSymbolTable.get(k).setType("void");
                                }
                                System.out.printf("Scope: %s  M_Var_Name: %s "
                                                + "Type: %s \tAttr: %s\n", MainSymbolTable.get(k).scope(),
                                        MainSymbolTable.get(k).name(), MainSymbolTable.get(k).type(),
                                        MainSymbolTable.get(k).attr());
                                // else
                                {
                                    //
                                    System.out.println("Variable Type Mismatch");
                                }
                                break;
                            }

                            // Checking the end of function declaration
                            case "begin": {
                                f_loop_stop = true;
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
    }
}