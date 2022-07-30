package tiger.compiler.typechecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import tiger.compiler.parser.ASTNode;
import tiger.compiler.parser.Symbol;

public class TypeChecker {

    private static HashMap<String, String> getTypeAliasMap(ASTNode rootNode)
            throws TypeCheckException {
        var typeMap = new HashMap<String, String>();
        var typedeclsNode = rootNode.getDerivation().get(1).getFirst();

        while (typedeclsNode.childCount() > 1) {
            var typedeclNode = typedeclsNode.getFirst();
            var typeAlias = typedeclNode.getDerivation().get(1).getValue();
            var realTypeNode = typedeclNode.getDerivation().get(3);

            if (realTypeNode.childCount() == 1) {
                realTypeNode = realTypeNode.getFirst();
                if (realTypeNode.getSymbol() == Symbol.ID) {
                    if (!typeMap.containsKey(realTypeNode.getValue())) {
                        throw new TypeCheckException(
                                "Could not generate type alias for '" + typeAlias + "' as type '"
                                        + realTypeNode.getValue() + "' was not found!");
                    }
                    typeMap.put(typeAlias, realTypeNode.getValue());
                } else {
                    typeMap.put(typeAlias, realTypeNode.getSymbol().getName());
                }
            }

            typedeclsNode = typedeclsNode.getLast();
        }

        return typeMap;
    }

    /*
     * 1. Construct the type-alias map A of the program’s type declarations. 2. Using A, compute the
     * type context ΓV of the variable declarations. 3. Using A, compute the type context ΓF of the
     * function declarations. 4. Using A, ΓV , and ΓF , determine if each declared function is
     * well-typed. 5. Using A, ΓV , and ΓF , determine if the main statement of the program is
     * well-typed.
     * 
     */
    public static void TypeCheck(ASTNode rootNode) throws TypeCheckException {

        var typeMap = getTypeAliasMap(rootNode);

    }
}
