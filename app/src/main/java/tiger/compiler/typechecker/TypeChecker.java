package tiger.compiler.typechecker;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import tiger.compiler.parser.ASTNode;
import tiger.compiler.parser.Symbol;
import tiger.compiler.typechecker.types.ArrayType;
import tiger.compiler.typechecker.types.FloatType;
import tiger.compiler.typechecker.types.IntType;
import tiger.compiler.typechecker.types.Type;
import tiger.compiler.typechecker.types.VoidType;

public class TypeChecker {

    private ASTNode rootNode;
    private Map<String, Type> typeMap;
    private Map<String, VariableTableEntry> varMap;
    private Map<String, FunctionTableEntry> funcMap;

    public TypeChecker(ASTNode rootNode) throws TypeCheckException {
        this.rootNode = rootNode;
        this.typeMap = new HashMap<String, Type>();
        this.varMap = new HashMap<String, VariableTableEntry>();
        this.funcMap = new HashMap<String, FunctionTableEntry>();

        this.createTypeMap();
        this.createVarMap();
        this.createFuncMap();
    }

    public Map<String, VariableTableEntry> getVarMap() {
        return this.varMap;
    }

    public Map<String, FunctionTableEntry> getFuncMap() {
        return this.funcMap;
    }

    private ArrayType createArrayType(ASTNode typeNT) throws TypeCheckException {
        var childTypeNT = typeNT.getLast();
        var childTypeNTSymbol = childTypeNT.getFirst().getSymbol();
        var arraySize = Integer.parseInt(typeNT.get(2).getValue());

        if (childTypeNTSymbol == Symbol.INT) {
            return new ArrayType(new IntType(), arraySize);
        } else if (childTypeNTSymbol == Symbol.FLOAT) {
            return new ArrayType(new FloatType(), arraySize);
        } else if (childTypeNTSymbol == Symbol.ID) {
            var idValue = childTypeNT.getFirst().getValue();
            var idTypeObj = this.typeMap.get(idValue);
            if (idTypeObj == null) {
                throw new TypeCheckException(
                        "Alias for type '" + idValue + "' was not found!");
            }
            return new ArrayType(idTypeObj, arraySize);
        } else if (childTypeNTSymbol == Symbol.ARRAY) {
            return new ArrayType(this.createArrayType(childTypeNT), arraySize);
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private Type getTypeOfNode(ASTNode typeNode) throws TypeCheckException {
        var firstChildSymbol = typeNode.getFirst().getSymbol();
        if (firstChildSymbol == Symbol.INT) {
            return new IntType();
        } else if (firstChildSymbol == Symbol.FLOAT) {
            return new FloatType();
        } else if (firstChildSymbol == Symbol.ID) {
            var idValue = typeNode.getFirst().getValue();
            var idTypeObj = this.typeMap.get(idValue);
            if (idTypeObj == null) {
                throw new TypeCheckException(
                        "Type '" + idValue + "' was not found!");
            }
            return idTypeObj;
        } else if (firstChildSymbol == Symbol.ARRAY) {
            return this.createArrayType(typeNode);
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private void createTypeMap() throws TypeCheckException {
        var typedeclsNode = this.rootNode.get(1).getFirst();
        while (typedeclsNode.childCount() > 1) {
            var typedeclNode = typedeclsNode.getFirst();
            typedeclsNode = typedeclsNode.getLast();

            var alias = typedeclNode.get(1).getValue();
            var typeNT = typedeclNode.get(3);
            var aliased = typeNT.getFirst().getSymbol();

            if (this.typeMap.containsKey(alias)) {
                throw new TypeCheckException("Multiple type aliases with same name: " + alias);
            }

            if (aliased == Symbol.INT) {
                this.typeMap.put(alias, new IntType());
            } else if (aliased == Symbol.FLOAT) {
                this.typeMap.put(alias, new FloatType());
            } else if (aliased == Symbol.ID) {
                var idValue = typeNT.getFirst().getValue();
                var idTypeObj = this.typeMap.get(idValue);
                if (idTypeObj == null) {
                    throw new TypeCheckException(
                            "Could not generate type alias '" + alias + "' because type '"
                                    + idValue + "' was not found!");
                }
                this.typeMap.put(alias, idTypeObj);
            } else if (aliased == Symbol.ARRAY) {
                this.typeMap.put(alias, this.createArrayType(typeNT));
            } else {
                throw new TypeCheckException("Unexpected error");
            }
        }
    }

    private void createVarMap() throws TypeCheckException {
        var vardeclsNode = this.rootNode.get(1).get(1);
        while (vardeclsNode.childCount() > 1) {
            // Advance to next vardecl
            var vardeclNode = vardeclsNode.getFirst();
            vardeclsNode = vardeclsNode.getLast();

            // Determine if the RHS type matches the declared var type
            var varType = this.getTypeOfNode(vardeclNode.get(3));
            if (vardeclNode.get(4).childCount() > 1) {
                var initialValueType = this.getConstType(vardeclNode.get(4).get(1));
                if (!initialValueType.canBeAssignedTo(varType)) {
                    throw new TypeCheckException(
                            "Cannot assign " + initialValueType + " to " + varType);
                }
            }

            // Assign RHS type to each identifier
            var idsNode = vardeclNode.get(1);
            while (idsNode.childCount() > 1) {
                var identifier = idsNode.getFirst().getValue();
                var entry = new VariableTableEntry(varType);
                if (this.varMap.put(identifier, entry) != null) {
                    throw new TypeCheckException(
                            "Variable has been declared multiple times: " + identifier);
                }
                idsNode = idsNode.get(2);
            }

            var identifier = idsNode.getFirst().getValue();
            var entry = new VariableTableEntry(varType);
            if (this.varMap.put(identifier, entry) != null) {
                throw new TypeCheckException(
                        "Variable has been declared multiple times: " + identifier);
            }
        }
    }

    private void createFuncMap() throws TypeCheckException {

        // Add stdlib functions
        var stdlibSig =
                new FunctionTableEntry(new IntType(), new LinkedHashMap<String, ParamEntry>());
        this.funcMap.put("readi", stdlibSig);
        stdlibSig =
                new FunctionTableEntry(new FloatType(), new LinkedHashMap<String, ParamEntry>());
        this.funcMap.put("readf", stdlibSig);

        var printiArgs = new LinkedHashMap<String, ParamEntry>();
        printiArgs.put("input", new ParamEntry(new IntType()));
        stdlibSig = new FunctionTableEntry(new VoidType(), printiArgs);
        this.funcMap.put("printi", stdlibSig);

        var printfArgs = new LinkedHashMap<String, ParamEntry>();
        printfArgs.put("input", new ParamEntry(new FloatType()));
        stdlibSig = new FunctionTableEntry(new VoidType(), printfArgs);
        this.funcMap.put("printf", stdlibSig);

        // Iterate through funcdecls
        var funcdeclsNode = this.rootNode.get(1).get(2);
        while (funcdeclsNode.childCount() > 1) {
            // Advance to next funcdecl
            var funcdeclNode = funcdeclsNode.getFirst();
            funcdeclsNode = funcdeclsNode.getLast();

            // Determine the function identifier
            // The tests suggest that the language only wants 1 definition per name
            var identifier = funcdeclNode.get(1).getValue();
            if (this.funcMap.containsKey(identifier)) {
                throw new TypeCheckException(
                        "Function has been declared multiple times with same identifier: "
                                + identifier);
            }

            // Determine the function return type
            var funcTypeNT = funcdeclNode.get(5).getLast();
            var returnType =
                    (funcTypeNT.getSymbol() != Symbol.EPSILON)
                            ? this.getTypeOfNode(funcTypeNT)
                            : new VoidType();

            // Determine the function parameter types
            var paramsMap = new LinkedHashMap<String, ParamEntry>();
            var paramsNode = funcdeclNode.get(3);
            if (paramsNode.getFirst().getSymbol() != Symbol.EPSILON) {
                var neparamsNode = paramsNode.getFirst();
                while (neparamsNode.childCount() != 1) {
                    // Advance to next neparamsNode
                    var paramNode = neparamsNode.getFirst();
                    neparamsNode = neparamsNode.getLast();

                    // Insert param name:type into map
                    var paramId = paramNode.getFirst().getValue();
                    var paramType = this.getTypeOfNode(paramNode.get(2));
                    if (paramsMap.put(paramId, new ParamEntry(paramType)) != null) {
                        throw new TypeCheckException("Function declaration '" + identifier
                                + "' has multiple parameters with name '" + paramId + "'");
                    }
                }

                // Insert final param name:type into map
                var paramNode = neparamsNode.getFirst();
                var paramId = paramNode.getFirst().getValue();
                var paramType = this.getTypeOfNode(paramNode.get(2));
                if (paramsMap.put(paramId, new ParamEntry(paramType)) != null) {
                    throw new TypeCheckException("Function declaration '" + identifier
                            + "' has multiple parameters with name '" + paramId + "'");
                }
            }

            // Insert function definition
            var signatureEntry = new FunctionTableEntry(returnType, paramsMap);
            this.funcMap.put(identifier, signatureEntry);
        }
    }

    private Type getVarType(String identifier, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        if (paramsMap.containsKey(identifier)) {
            return paramsMap.get(identifier).getType();
        } else if (this.varMap.containsKey(identifier)) {
            return this.varMap.get(identifier).getType();
        } else {
            throw new TypeCheckException("Identifier not found: " + identifier);
        }
    }

    private Type getConstType(ASTNode constNode) throws TypeCheckException {
        var childSymbol = constNode.getFirst().getSymbol();
        if (childSymbol == Symbol.INTLIT) {
            return constNode.setType(new IntType());
        } else if (childSymbol == Symbol.FLOATLIT) {
            return constNode.setType(new FloatType());
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private Type getFactorType(ASTNode factorNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var childSymbol = factorNode.getFirst().getSymbol();
        if (childSymbol == Symbol.constNt) {
            return factorNode.setType(this.getConstType(factorNode.getFirst()));
        } else if (childSymbol == Symbol.ID) {
            if (factorNode.childCount() == 1) {
                var identifier = factorNode.getFirst().getValue();
                return factorNode.setType(this.getVarType(identifier, paramsMap));
            }

            if (!this.getNumexprType(factorNode.get(2), paramsMap).equals(new IntType())) {
                throw new TypeCheckException("Array index must be an int for array '"
                        + factorNode.getFirst().getValue() + "'");
            }

            var identifier = factorNode.getFirst().getValue();
            return factorNode
                    .setType(((ArrayType) (this.getVarType(identifier, paramsMap))).getSubType());
        } else if (childSymbol == Symbol.LEFT_PAREN) {
            return factorNode.setType(this.getNumexprType(factorNode.get(1), paramsMap));
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private Type getTermType(ASTNode termNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var childSymbol = termNode.getFirst().getSymbol();
        if (childSymbol == Symbol.factor) {
            return termNode.setType(this.getFactorType(termNode.getFirst(), paramsMap));
        } else if (childSymbol == Symbol.term) {
            var termType = this.getTermType(termNode.getFirst(), paramsMap);
            var factorType = this.getFactorType(termNode.getLast(), paramsMap);

            var termIsInt = termType.equals(new IntType());
            var termIsFloat = termType.equals(new FloatType());
            var factorIsInt = factorType.equals(new IntType());
            var factorIsFloat = factorType.equals(new FloatType());

            if (!termIsInt && !termIsFloat) {
                throw new TypeCheckException(
                        "RHS term must be int or float to perform nonlinop operation");
            }

            if (!factorIsInt && !factorIsFloat) {
                throw new TypeCheckException(
                        "RHS factor must be int or float to perform nonlinop operation");
            }

            if (termIsFloat || factorIsFloat) {
                return termNode.setType(new FloatType());
            } else {
                return termNode.setType(new IntType());
            }
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private Type getNumexprType(ASTNode numexprNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var childSymbol = numexprNode.getFirst().getSymbol();
        if (childSymbol == Symbol.term) {
            return numexprNode.setType(this.getTermType(numexprNode.getFirst(), paramsMap));
        } else if (childSymbol == Symbol.numexpr) {
            var numexprType = this.getNumexprType(numexprNode.getFirst(), paramsMap);
            var termType = this.getTermType(numexprNode.getLast(), paramsMap);

            var numexprIsInt = numexprType.equals(new IntType());
            var numexprIsFloat = numexprType.equals(new FloatType());
            var termIsInt = termType.equals(new IntType());
            var termIsFloat = termType.equals(new FloatType());

            if (!numexprIsInt && !numexprIsFloat) {
                throw new TypeCheckException(
                        "RHS numexpr must be int or float to perform linop operation");
            }

            if (!termIsInt && !termIsFloat) {
                throw new TypeCheckException(
                        "RHS term must be int or float to perform linop operation");
            }

            if (numexprIsFloat || termIsFloat) {
                return numexprNode.setType(new FloatType());
            } else {
                return numexprNode.setType(new IntType());
            }
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private Type getLvalueType(ASTNode lvalueNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var lvalueId = lvalueNode.getFirst().getValue();
        var arrayOffsetLvalue = lvalueNode.getLast().childCount() > 1;

        if (arrayOffsetLvalue && !this.getNumexprType(lvalueNode.getLast().get(1), paramsMap)
                .equals(new IntType())) {
            throw new TypeCheckException("Array index must be integer");
        }

        var lvalueType = this.getVarType(lvalueId, paramsMap);
        var retType = (!arrayOffsetLvalue) ? lvalueType
                : ((ArrayType) (lvalueType)).getSubType();
        return lvalueNode.setType(retType);
    }

    private void checkPred(ASTNode predNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var firstNumexprType = this.getNumexprType(predNode.getFirst(), paramsMap);
        var secondNumexprType = this.getNumexprType(predNode.getLast(), paramsMap);
        if ((!firstNumexprType.equals(new IntType()) && !firstNumexprType.equals(new FloatType()))
                || (!secondNumexprType.equals(new IntType())
                        && !secondNumexprType.equals(new FloatType()))) {
            throw new TypeCheckException("Boolean expression must be made using numerics");
        }

        predNode.setType(new IntType());
    }

    private void checkClause(ASTNode clauseNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var childSymbol = clauseNode.getFirst().getSymbol();
        if (childSymbol == Symbol.pred) {
            this.checkPred(clauseNode.getFirst(), paramsMap);
        } else if (childSymbol == Symbol.clause) {
            this.checkClause(clauseNode.getFirst(), paramsMap);
            this.checkPred(clauseNode.getLast(), paramsMap);
        } else {
            throw new TypeCheckException("Unexpected error");
        }

        clauseNode.setType(new IntType());
    }

    private void checkBoolexpr(ASTNode boolexprNode, Map<String, ParamEntry> paramsMap)
            throws TypeCheckException {
        var childSymbol = boolexprNode.getFirst().getSymbol();
        if (childSymbol == Symbol.clause) {
            this.checkClause(boolexprNode.getFirst(), paramsMap);
        } else if (childSymbol == Symbol.boolexpr) {
            this.checkBoolexpr(boolexprNode.getFirst(), paramsMap);
            this.checkClause(boolexprNode.getLast(), paramsMap);
        } else {
            throw new TypeCheckException("Unexpected error");
        }

        boolexprNode.setType(new IntType());
    }

    private void checkStatement(ASTNode stmtNode, FunctionTableEntry signature, boolean canBreak)
            throws TypeCheckException {
        var stmtType = stmtNode.getFirst().getSymbol();
        if (stmtType == Symbol.lvalue) {
            var lvalueNode = stmtNode.getFirst();
            var lvalueType = this.getLvalueType(lvalueNode, signature.getParams());

            var rhsType = this.getNumexprType(stmtNode.getLast(), signature.getParams());
            if (!rhsType.canBeAssignedTo(lvalueType)) {
                throw new TypeCheckException("Statement LHS != RHS");
            }
        } else if (stmtType == Symbol.optstore) {
            // optstore used
            if (stmtNode.getFirst().childCount() > 1) {
                var lvalueNode = stmtNode.getFirst().getFirst();
                var lhsType = this.getLvalueType(lvalueNode, signature.getParams());

                var funcId = stmtNode.get(1).getValue();
                if (!funcMap.containsKey(funcId)) {
                    throw new TypeCheckException("Function '" + funcId + "' not defined");
                }

                var funcSignature = funcMap.get(funcId);
                if (!funcSignature.getReturnType().canBeAssignedTo(lhsType)) {
                    throw new TypeCheckException("Function '" + funcId + "' returns a "
                            + funcSignature.getReturnType() + " but LHS expects a " + lhsType);
                }
            }

            // Check if arguments were passed correctly
            var funcId = stmtNode.get(1).getValue();
            if (!funcMap.containsKey(funcId)) {
                throw new TypeCheckException("Function '" + funcId + "' not defined");
            }

            var funcParams = funcMap.get(funcId).getParams().entrySet().iterator();

            var numexprsNode = stmtNode.get(3);
            if (numexprsNode.getFirst().getSymbol() != Symbol.EPSILON) {
                var neexprsNode = numexprsNode.getFirst();

                while (neexprsNode.childCount() > 1) {
                    var numexprNode = neexprsNode.getFirst();
                    neexprsNode = neexprsNode.getLast();
                    var numexprType = this.getNumexprType(numexprNode, signature.getParams());

                    if (!funcParams.hasNext()) {
                        throw new TypeCheckException(
                                "Too many arguments passed to function " + funcId);
                    }

                    var expectedType = funcParams.next().getValue().getType();
                    if (!numexprType.canBeAssignedTo(expectedType)) {
                        throw new TypeCheckException(
                                "Function " + funcId + " expected argument with type "
                                        + expectedType + " but got " + numexprType);
                    }
                }

                // Check last neexprsNode
                var numexprNode = neexprsNode.getFirst();
                var numexprType = this.getNumexprType(numexprNode, signature.getParams());

                if (!funcParams.hasNext()) {
                    throw new TypeCheckException(
                            "Too many arguments passed to function " + funcId);
                }

                var expectedType = funcParams.next().getValue().getType();
                if (!numexprType.canBeAssignedTo(expectedType)) {
                    throw new TypeCheckException(
                            "Function " + funcId + " expected argument with type "
                                    + expectedType + " but got " + numexprType);
                }
            }

            if (funcParams.hasNext()) {
                throw new TypeCheckException("Not enough arguments passed to function " + funcId);
            }
        } else if (stmtType == Symbol.BREAK) {
            if (!canBreak) {
                throw new TypeCheckException("Break statement outside of loop");
            }
        } else if (stmtType == Symbol.RETURN) {
            if (signature.getReturnType().equals(new VoidType())) {
                throw new TypeCheckException("Return statement not in function");
            }

            var actualReturnType = this.getNumexprType(stmtNode.getLast(), signature.getParams());
            if (!actualReturnType.canBeAssignedTo(signature.getReturnType())) {
                throw new TypeCheckException("Expected to return " + signature.getReturnType()
                        + " but instead returned " + actualReturnType);
            }
        } else if (stmtType == Symbol.FOR) {
            // Check range
            if (!this.getVarType(stmtNode.get(1).getValue(), signature.getParams())
                    .equals(new IntType())) {
                throw new TypeCheckException("For-Loop indices must be integers");
            }

            if (!this.getNumexprType(stmtNode.get(3), signature.getParams())
                    .equals(new IntType())) {
                throw new TypeCheckException("For-Loop indices must be integers");
            }

            if (!this.getNumexprType(stmtNode.get(5), signature.getParams())
                    .equals(new IntType())) {
                throw new TypeCheckException("For-Loop indices must be integers");
            }

            // Check nested statements
            this.checkStatements(stmtNode.get(7), signature, true);
        } else if (stmtType == Symbol.WHILE) {
            this.checkBoolexpr(stmtNode.get(1), signature.getParams());
            this.checkStatements(stmtNode.get(3), signature, true);
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 5) {
            this.checkBoolexpr(stmtNode.get(1), signature.getParams());
            this.checkStatements(stmtNode.get(3), signature, canBreak);
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 7) {
            this.checkBoolexpr(stmtNode.get(1), signature.getParams());
            this.checkStatements(stmtNode.get(3), signature, canBreak);
            this.checkStatements(stmtNode.get(5), signature, canBreak);
        } else {
            throw new TypeCheckException("Unexpected error");
        }
    }

    private void checkStatements(ASTNode stmtsNode, FunctionTableEntry signature, boolean canBreak)
            throws TypeCheckException {
        while (stmtsNode.childCount() != 1) {
            var stmtNode = stmtsNode.get(0).get(0);
            stmtsNode = stmtsNode.get(1);
            this.checkStatement(stmtNode, signature, canBreak);
        }

        var stmtNode = stmtsNode.get(0).get(0);
        this.checkStatement(stmtNode, signature, canBreak);
    }

    private void checkFunction(ASTNode stmtsNode, FunctionTableEntry signature)
            throws TypeCheckException {
        this.checkStatements(stmtsNode, signature, false);
    }

    public void checkProgram() throws TypeCheckException {
        // Determine if each declared function is well-typed.
        for (var func : this.funcMap.entrySet()) {
            var funcName = func.getKey();
            var signature = func.getValue();

            // No definitions provided for stdlib functions
            if (funcName.equals("readi") || funcName.equals("readf") || funcName.equals("printi")
                    || funcName.equals("printf")) {
                continue;
            }

            var funcdeclsNode = this.rootNode.get(1).get(2);
            var funcdeclNode = funcdeclsNode.get(0);
            while (!funcdeclNode.get(1).getValue().equals(funcName)) {
                funcdeclsNode = funcdeclsNode.get(1);
                funcdeclNode = funcdeclsNode.get(0);
            }
            this.checkFunction(funcdeclNode.get(7), signature);
        }

        // Determine if the main statement of the program is well-typed.
        this.checkFunction(this.rootNode.get(3),
                new FunctionTableEntry(new VoidType(), new LinkedHashMap<String, ParamEntry>()));
    }
}
