import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TypeChecker {

    public static void typeCheck(String ast) {
        //String fullFileText1[] = (fullFileText.split("((?<=,)|(?=,)|(?<=;)|(?=;)|(?<=:)|(?=:))"));
        String fullFileText_token[] = (ast.split("(\\s+)|((?=,)|(?=;)|(?=:)|(?<=\\()|(?=\\()|(?=\\))|(?<=\\)))"));

        int k = 0;
        int p = 0;
        int funcvar_no = 0;
        String scope = "program";
        for (int i = 0; i < fullFileText_token.length; i++) {
            ArrayList<SymbolTableEntry> MainSymbolTable = new ArrayList<>();
            String[] m_var_name=new String[100];
            // System.out.println(fullFileText_token[i]);
            switch(fullFileText_token[i]) {

                //Checking for variable Declaration
                case "vardecl": {

                    //System.out.println("Entered vardecl");
                    boolean var_loop_stop = false;
                    for(int j = i; !var_loop_stop; j++) {
                        // System.out.println(fullFileText_token[j]);
                        switch(fullFileText_token[j]) {
                            case "ids": {
                                m_var_name[k] = fullFileText_token[j+1];
                                //System.out.println(var_name[k]);
                                k++;
                                break;
                            }

                            case "type": {
                                for(; p<k;p++) {
                                    while(k >= MainSymbolTable.size())
                                        MainSymbolTable.add(null);
                                    MainSymbolTable.set(k, new SymbolTableEntry(scope, m_var_name[p], fullFileText_token[j+1], "id"));
                                    System.out.printf("Scope: %s  M_Var_Name: %s \tType: %s \t"
                                                    + "Attr: %s\n",MainSymbolTable.get(k).scope(),
                                            MainSymbolTable.get(k).name(), MainSymbolTable.get(k).type(),
                                            MainSymbolTable.get(k).attr());
                                }
                                break;
                            }

                            case "optinit": {
                                var_loop_stop = true;
                                //System.out.println(fullFileText_token[j]);
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

                //Checking for function Declaration
                case "funcdecl": {
                    //System.out.println("Entered funcdecl");
                    boolean f_loop_stop = false;
                    for(int j = i; !f_loop_stop;j++) {
                        switch(fullFileText_token[j]) {
                            case "func": {
                                //System.out.println(fullFileText_token[j]);
                                m_var_name[k] = fullFileText_token[j+1];
                                i = j;
                                break;
                            }

                            //Checking for variable declaration in a function
                            case "param": {
                                //System.out.println("Entered Param");
                                String f_scope = m_var_name[k];

                                ArrayList<SymbolTableEntry> FuncSymbolTable = new ArrayList<>();
                                while(funcvar_no >= FuncSymbolTable.size())
                                    FuncSymbolTable.add(null);
                                FuncSymbolTable.set(funcvar_no, new SymbolTableEntry(f_scope, fullFileText_token[j+1], fullFileText_token[j+7], "id"));
                                System.out.printf("Scope: %s  \tF_Var_Name: %s  \tType: %s \t"
                                                + "Attr: %s\n",FuncSymbolTable.get(funcvar_no).scope(),
                                        FuncSymbolTable.get(funcvar_no).name(), FuncSymbolTable.get(funcvar_no).type(),
                                        FuncSymbolTable.get(funcvar_no).attr());

                                j = j + 7;
                                break;
                            }
                            //Checking type for the function declared
                            case "optrettype": {
                                while(k >= MainSymbolTable.size())
                                    MainSymbolTable.add(null);
                                MainSymbolTable.set(k, new SymbolTableEntry(scope, m_var_name[k], "", "func"));

                                if(fullFileText_token[j+5].matches("type")) {
                                    MainSymbolTable.get(k).setType(fullFileText_token[j+6]);
                                    j = j + 6;
                                }
                                else {
                                    MainSymbolTable.get(k).setType("void");
                                }

                                System.out.printf("Scope: %s  M_Var_Name: %s "
                                                + "Type: %s \tAttr: %s\n",MainSymbolTable.get(k).scope(),
                                        MainSymbolTable.get(k).name(), MainSymbolTable.get(k).type(),
                                        MainSymbolTable.get(k).attr());
                                k++;
                                p++;
                                break;
                            }

                            //Checking the end of function declaration
                            case "begin": {
                                f_loop_stop = true;
                                break;
                            }
                        }
                        i = j;
                    }
                    break;
                }
                default : {
                    //System.out.println(i);
                    break;
                }
            }
        }
    }
}
