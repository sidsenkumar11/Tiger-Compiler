package tiger.compiler.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tiger.compiler.interpreter.instructions.AllocArray;
import tiger.compiler.interpreter.instructions.ArithmeticInstruction;
import tiger.compiler.interpreter.instructions.MoveConst;
import tiger.compiler.interpreter.instructions.ArithmeticOp;
import tiger.compiler.interpreter.instructions.Branch;
import tiger.compiler.interpreter.instructions.Call;
import tiger.compiler.interpreter.instructions.Cmp;
import tiger.compiler.interpreter.instructions.ComparisonOp;
import tiger.compiler.interpreter.instructions.Goto;
import tiger.compiler.interpreter.instructions.Increment;
import tiger.compiler.interpreter.instructions.Instruction;
import tiger.compiler.interpreter.instructions.Label;
import tiger.compiler.interpreter.instructions.Load;
import tiger.compiler.interpreter.instructions.Move;
import tiger.compiler.interpreter.instructions.MoveOp;
import tiger.compiler.interpreter.instructions.Pop;
import tiger.compiler.interpreter.instructions.Push;
import tiger.compiler.interpreter.instructions.Return;
import tiger.compiler.interpreter.instructions.Store;
import tiger.compiler.parser.ASTNode;
import tiger.compiler.parser.Symbol;
import tiger.compiler.typechecker.FunctionTableEntry;
import tiger.compiler.typechecker.ParamEntry;
import tiger.compiler.typechecker.RegEntry;
import tiger.compiler.typechecker.VariableTableEntry;
import tiger.compiler.typechecker.types.ArrayType;
import tiger.compiler.typechecker.types.FloatType;
import tiger.compiler.typechecker.types.IntType;
import tiger.compiler.typechecker.types.Type;

public class IRGenerator {
    private ASTNode rootNode;
    private Map<String, VariableTableEntry> varMap;
    private Map<String, FunctionTableEntry> funcMap;
    private List<Instruction> instructions;
    private int intRegCount;
    private int floatRegCount;
    private int labelno;
    private String funcName;

    public IRGenerator(ASTNode rootNode, Map<String, VariableTableEntry> varMap,
            Map<String, FunctionTableEntry> funcMap) {
        this.rootNode = rootNode;
        this.varMap = varMap;
        this.funcMap = funcMap;

        this.instructions = new ArrayList<>();
        this.intRegCount = 1; // r0 is always zero
        this.floatRegCount = 1; // r0 is always zero
        this.labelno = 0;
    }

    public int getIntRegCount() {
        return this.intRegCount;
    }

    public int getFloatRegCount() {
        return this.floatRegCount;
    }

    private int newIntReg() {
        return this.intRegCount++;
    }

    private int newFloatReg() {
        return this.floatRegCount++;
    }

    private String newLabel() {
        return "label_" + this.labelno++;
    }

    private void emit(Instruction inst) {
        this.instructions.add(inst);
    }

    private int getVariable(String variable) {
        if (!this.funcName.equals("main")) {
            var params = this.funcMap.get(this.funcName).getParams();
            if (params.containsKey(variable)) {
                return params.get(variable).getRegNum();
            }
        }

        return this.varMap.get(variable).getRegNum();
    }

    private int generateConst(ASTNode constNode) throws IRGenException {
        int destReg = 0;
        var literalStr = constNode.getFirst().getValue();

        // Allocate register based on const type
        var literalSymbol = constNode.getFirst().getSymbol();
        if (literalSymbol == Symbol.INTLIT) {
            destReg = this.newIntReg();
            var intlit = Integer.parseInt(literalStr);
            this.emit(new MoveConst(destReg, intlit));
        } else if (literalSymbol == Symbol.FLOATLIT) {
            destReg = this.newFloatReg();
            var floatLit = Float.parseFloat(literalStr);
            this.emit(new MoveConst(destReg, floatLit));
        } else {
            throw new IRGenException("Unexpected error");
        }

        return destReg;
    }

    private int generateFactor(ASTNode factorNode) throws IRGenException {
        var factorChild = factorNode.getFirst();
        var factorChildSymbol = factorChild.getSymbol();

        if (factorChildSymbol == Symbol.constNt) {
            return this.generateConst(factorChild);
        } else if (factorChildSymbol == Symbol.LEFT_PAREN) {
            return this.generateNumexpr(factorNode.get(1));
        } else if (factorChildSymbol == Symbol.ID && factorNode.childCount() == 1) {
            return this.getVariable(factorChild.getValue());
        } else if (factorChildSymbol == Symbol.ID && factorNode.childCount() > 1) {

            // Array indexing
            var isFloat = factorNode.getType().equals(new FloatType());
            var destReg = (isFloat) ? this.newFloatReg() : this.newIntReg();
            var baseReg = this.getVariable(factorChild.getValue());
            var offsetReg = this.generateNumexpr(factorNode.get(2));
            this.emit(new Load(destReg, baseReg, offsetReg, isFloat));
            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private int generateTerm(ASTNode termNode) throws IRGenException {
        var termChild = termNode.getFirst();
        var termChildSymbol = termChild.getSymbol();

        if (termChildSymbol == Symbol.factor) {
            return this.generateFactor(termChild);
        } else if (termChildSymbol == Symbol.term) {
            var subTermNode = termNode.getFirst();
            var factorNode = termNode.getLast();
            var opSymbol = termNode.get(1).getFirst().getSymbol();

            var termReg = this.generateTerm(subTermNode);
            var factorReg = this.generateFactor(factorNode);
            var op = (opSymbol == Symbol.STAR) ? ArithmeticOp.MUL : ArithmeticOp.DIV;

            var isFloat = termNode.getType().equals(new FloatType());
            var destReg = (isFloat) ? this.newFloatReg() : this.newIntReg();
            this.emit(new ArithmeticInstruction(op, destReg,
                    new RegEntry(termReg, subTermNode.getType().equals(new FloatType())),
                    new RegEntry(factorReg, factorNode.getType().equals(new FloatType()))));
            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private int generateNumexpr(ASTNode numexprNode) throws IRGenException {
        var numexprChild = numexprNode.getFirst();
        var numexprChildSymbol = numexprChild.getSymbol();

        if (numexprChildSymbol == Symbol.term) {
            return this.generateTerm(numexprChild);
        } else if (numexprChildSymbol == Symbol.numexpr) {
            var subNumexprNode = numexprNode.getFirst();
            var termNode = numexprNode.getLast();
            var opSymbol = numexprNode.get(1).getFirst().getSymbol();

            var numexprReg = this.generateNumexpr(subNumexprNode);
            var termReg = this.generateTerm(termNode);
            var op = (opSymbol == Symbol.PLUS) ? ArithmeticOp.ADD : ArithmeticOp.SUB;

            var isFloat = numexprNode.getType().equals(new FloatType());
            var destReg = (isFloat) ? this.newFloatReg() : this.newIntReg();

            this.emit(new ArithmeticInstruction(op, destReg,
                    new RegEntry(numexprReg, subNumexprNode.getType().equals(new FloatType())),
                    new RegEntry(termReg, termNode.getType().equals(new FloatType()))));

            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private int generatePred(ASTNode predNode) throws IRGenException {
        var numexprReg1 = this.generateNumexpr(predNode.getFirst());
        var numexprReg2 = this.generateNumexpr(predNode.getLast());
        var numexprIsFloat1 = predNode.getFirst().getType().equals(new FloatType());
        var numexprIsFloat2 = predNode.getLast().getType().equals(new FloatType());
        var operand1Entry = new RegEntry(numexprReg1, numexprIsFloat1);
        var operand2Entry = new RegEntry(numexprReg2, numexprIsFloat2);

        var destReg = this.newIntReg();
        var opSymbol = predNode.get(1).getFirst().getSymbol();

        if (opSymbol == Symbol.EQUALS) {
            this.emit(new Cmp(ComparisonOp.EQ, destReg, operand1Entry, operand2Entry));
        } else if (opSymbol == Symbol.NOT_EQUALS) {
            this.emit(new Cmp(ComparisonOp.NEQ, destReg, operand1Entry, operand2Entry));
        } else if (opSymbol == Symbol.LESS_OR_EQ) {
            this.emit(new Cmp(ComparisonOp.LEQ, destReg, operand1Entry, operand2Entry));
        } else if (opSymbol == Symbol.GREATER_OR_EQ) {
            this.emit(new Cmp(ComparisonOp.GEQ, destReg, operand1Entry, operand2Entry));
        } else if (opSymbol == Symbol.LESS_THAN) {
            this.emit(new Cmp(ComparisonOp.LT, destReg, operand1Entry, operand2Entry));
        } else if (opSymbol == Symbol.GREATER_THAN) {
            this.emit(new Cmp(ComparisonOp.GT, destReg, operand1Entry, operand2Entry));
        } else {
            throw new IRGenException("Unexpected error");
        }

        // Pred result is 1 (true) or 0 (false)
        return destReg;
    }

    private int generateClause(ASTNode clauseNode) throws IRGenException {
        var clauseChild = clauseNode.getFirst();
        var clauseChildSymbol = clauseChild.getSymbol();

        if (clauseChildSymbol == Symbol.pred) {
            return this.generatePred(clauseChild);
        } else if (clauseChildSymbol == Symbol.clause) {
            var subClauseNode = clauseNode.getFirst();
            var predNode = clauseNode.getLast();

            var subClauseReg = this.generateClause(subClauseNode);
            var predReg = this.generatePred(predNode);
            var destReg = this.newIntReg();

            this.emit(new ArithmeticInstruction(ArithmeticOp.AND, destReg,
                    new RegEntry(subClauseReg, subClauseNode.getType().equals(new FloatType())),
                    new RegEntry(predReg, predNode.getType().equals(new FloatType()))));

            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private int generateBoolexpr(ASTNode boolexprNode) throws IRGenException {
        var boolexprChild = boolexprNode.getFirst();
        var boolexprChildSymbol = boolexprChild.getSymbol();

        if (boolexprChildSymbol == Symbol.clause) {
            return this.generateClause(boolexprChild);
        } else if (boolexprChildSymbol == Symbol.boolexpr) {
            var subBoolexprNode = boolexprNode.getFirst();
            var clauseNode = boolexprNode.getLast();

            var subBoolexprReg = this.generateBoolexpr(subBoolexprNode);
            var clauseReg = this.generateClause(clauseNode);
            var destReg = this.newIntReg();

            this.emit(new ArithmeticInstruction(ArithmeticOp.OR, destReg,
                    new RegEntry(subBoolexprReg, subBoolexprNode.getType().equals(new FloatType())),
                    new RegEntry(clauseReg, clauseNode.getType().equals(new FloatType()))));

            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private void generateLvalueAssign(ASTNode lvalueNode, Type rhsType, int rhsReg)
            throws IRGenException {

        // Get LHS Reg
        var lvalueId = lvalueNode.getFirst().getValue();
        var lvalueReg = this.getVariable(lvalueId);
        var isFloat = lvalueNode.getType().equals(new FloatType());

        // Set LHS
        var optOffsetNode = lvalueNode.getLast();
        if (optOffsetNode.childCount() == 1) {
            if (isFloat && rhsType.equals(new IntType())) {
                var tempFloatReg = this.newFloatReg();
                this.emit(new Move(MoveOp.INT_TO_FLOAT, tempFloatReg, rhsReg));
                rhsReg = tempFloatReg;
            }

            var opType = (isFloat) ? MoveOp.FLOAT_TO_FLOAT : MoveOp.INT_TO_INT;
            this.emit(new Move(opType, lvalueReg, rhsReg));
        } else {
            var offsetNode = optOffsetNode.get(1);
            var offsetReg = this.generateNumexpr(offsetNode);

            // Store into int-mem or float-mem
            // isFloat tells us the type of id[offset]
            // assigning directly to a 2D array would delete the allocated memory; that would be a
            // badly written program
            this.emit(new Store(rhsReg, lvalueReg, offsetReg, isFloat));
        }
    }

    private void generateStatement(ASTNode stmtNode, String breakLabel)
            throws IRGenException {
        var stmtType = stmtNode.getFirst().getSymbol();
        if (stmtType == Symbol.lvalue) {
            var numexprNode = stmtNode.get(2);
            var rhsReg = this.generateNumexpr(numexprNode);
            var rhsType = numexprNode.getType();
            var lvalueNode = stmtNode.getFirst();
            this.generateLvalueAssign(lvalueNode, rhsType, rhsReg);
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 5) {
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, 0, boolexprReg, 0));
            var conditionFalse = this.newLabel();
            this.emit(new Branch(conditionFalse));
            this.generateStatements(stmtNode.get(3), conditionFalse);
            this.emit(new Label(conditionFalse));
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 7) {
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, 0, boolexprReg, 0));
            var conditionFalse = this.newLabel();
            var afterLabel = this.newLabel();
            this.emit(new Branch(conditionFalse));
            this.generateStatements(stmtNode.get(3), afterLabel);
            this.emit(new Goto(afterLabel));
            this.emit(new Label(conditionFalse));
            this.generateStatements(stmtNode.get(5), afterLabel);
            this.emit(new Label(afterLabel));
        } else if (stmtType == Symbol.WHILE) {
            var loopStart = this.newLabel();
            this.emit(new Label(loopStart));
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, 0, boolexprReg, 0));
            var loopEnd = this.newLabel();
            this.emit(new Branch(loopEnd));
            this.generateStatements(stmtNode.get(3), loopEnd);
            this.emit(new Goto(loopStart));
            this.emit(new Label(loopEnd));
        } else if (stmtType == Symbol.FOR) {
            // Set up loop bound variables
            var indexReg = this.getVariable(stmtNode.get(1).getValue());
            var leftBoundReg = this.generateNumexpr(stmtNode.get(3));
            var rightBoundReg = this.generateNumexpr(stmtNode.get(5));
            this.emit(new Move(MoveOp.INT_TO_INT, indexReg, leftBoundReg));

            // Start the loop
            var loopStart = this.newLabel();
            var loopEnd = this.newLabel();
            this.emit(new Label(loopStart));
            this.emit(new Cmp(ComparisonOp.GEQ, 0, indexReg, rightBoundReg));
            this.emit(new Branch(loopEnd));
            this.generateStatements(stmtNode.get(7), loopEnd);
            this.emit(new Increment(indexReg));
            this.emit(new Goto(loopStart));
            this.emit(new Label(loopEnd));
        } else if (stmtType == Symbol.optstore) {

            // Generate arguments and place into arg registers
            var argRegs = new ArrayList<RegEntry>();
            var numexprsNode = stmtNode.get(3);
            if (numexprsNode.getFirst().getSymbol() == Symbol.neexprs) {
                var neexprsNode = numexprsNode.getFirst();
                while (true) {
                    var numexprNode = neexprsNode.getFirst();
                    var regNum = this.generateNumexpr(numexprNode);
                    var isFloat = numexprNode.getType().equals(new FloatType());
                    argRegs.add(new RegEntry(regNum, isFloat));

                    if (neexprsNode.childCount() == 1) {
                        break;
                    }

                    neexprsNode = neexprsNode.get(2);
                }
            }

            // Move args to expected param registers
            var funcId = stmtNode.get(1).getValue();
            var funcEntry = this.funcMap.get(funcId);
            var funcParams = new ArrayList<ParamEntry>(funcEntry.getParams().values());

            for (int i = 0; i < funcParams.size(); i++) {

                // Push old param reg to support recursive calls
                var paramReg = funcParams.get(i).getRegNum();
                var paramIsFloat = funcParams.get(i).isFloat();
                this.emit(new Push(paramReg, paramIsFloat));

                // Move new arg value into param reg
                var opType = MoveOp.INT_TO_INT;
                if (paramIsFloat) {
                    if (argRegs.get(i).isFloat()) {
                        opType = MoveOp.FLOAT_TO_FLOAT;
                    } else {
                        opType = MoveOp.INT_TO_FLOAT;
                    }
                }

                this.emit(new Move(opType, paramReg, argRegs.get(i).regNum()));
            }

            // Call the function
            this.emit(new Call(funcId, funcEntry.getReturnReg(), argRegs));

            // Pop old param regs
            for (int i = funcParams.size() - 1; i >= 0; i--) {
                var paramReg = funcParams.get(i).getRegNum();
                var paramIsFloat = funcParams.get(i).isFloat();
                this.emit(new Pop(paramReg, paramIsFloat));
            }

            // Optionally store the result
            var optStoreNode = stmtNode.getFirst();
            if (optStoreNode.childCount() > 1) {
                var lvalueNode = optStoreNode.getFirst();
                this.generateLvalueAssign(lvalueNode, funcEntry.getReturnType(),
                        funcEntry.getReturnReg());
            }
        } else if (stmtType == Symbol.BREAK) {
            this.emit(new Goto(breakLabel));
        } else if (stmtType == Symbol.RETURN) {
            var numexprNode = stmtNode.getLast();
            var resultReg = this.generateNumexpr(numexprNode);
            var funcEntry = this.funcMap.get(this.funcName);

            // Figure out the MoveOp
            var srcIsFloat = numexprNode.getType().equals(new FloatType());
            var op = MoveOp.INT_TO_INT;
            if (funcEntry.isFloat()) {
                if (srcIsFloat) {
                    op = MoveOp.FLOAT_TO_FLOAT;
                } else {
                    op = MoveOp.INT_TO_FLOAT;
                }
            }

            // Move numexpr to function's return value register
            this.emit(new Move(op, funcEntry.getReturnReg(), resultReg));
            this.emit(new Return());
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private void generateStatements(ASTNode stmtsNode, String breakLabel) throws IRGenException {
        while (true) {
            var stmtNode = stmtsNode.get(0).get(0);
            this.generateStatement(stmtNode, breakLabel);
            if (stmtsNode.childCount() == 1) {
                break;
            }

            stmtsNode = stmtsNode.get(1);
        }
    }

    private void generateFuncdecls(ASTNode funcdecls) throws IRGenException {

        // Generate library funcdecls
        this.emit(new Label(this.funcMap.get("readi").getLabel()));
        this.emit(new Label(this.funcMap.get("readf").getLabel()));
        this.emit(new Label(this.funcMap.get("printi").getLabel()));
        this.emit(new Label(this.funcMap.get("printf").getLabel()));

        // Generate code for given functions
        while (funcdecls.childCount() > 1) {
            var funcdecl = funcdecls.getFirst();
            this.funcName = funcdecl.get(1).getValue();
            this.emit(new Label(this.funcMap.get(funcName).getLabel()));
            this.generateStatements(funcdecl.get(7), "");
            this.emit(new Return());
            funcdecls = funcdecls.getLast();
        }
    }

    private int allocateArrays(ArrayType current) throws IRGenException {
        // Get array size into a register
        var arraySizeReg = this.newIntReg();
        this.emit(new MoveConst(arraySizeReg, current.getSize()));

        // Allocate the array based on subtype
        var baseReg = this.newIntReg();
        var subType = current.getSubType();
        if (subType.equals(new IntType())) {
            this.emit(new AllocArray(baseReg, arraySizeReg, 0, false));
        } else if (subType.equals(new FloatType())) {
            this.emit(new AllocArray(baseReg, arraySizeReg, 0, true));
        } else if (subType instanceof ArrayType subArrayType) {
            this.emit(new AllocArray(baseReg, arraySizeReg, 0, false));

            // Initialize the nested arrays
            for (var i = 0; i < current.getSize(); i++) {
                var pointerReg = this.allocateArrays(subArrayType);
                var offsetReg = this.newIntReg();
                this.emit(new MoveConst(offsetReg, i));
                this.emit(new Store(pointerReg, baseReg, offsetReg, false));
            }
        } else {
            throw new IRGenException("Unexpected error");
        }

        return baseReg;
    }

    private void generateVardecl(ASTNode vardecl) throws IRGenException {
        var optinit = vardecl.get(4);
        var idsNode = vardecl.get(1);
        while (true) {

            // Create register for variable
            var identifier = idsNode.getFirst().getValue();
            var varEntry = this.varMap.get(identifier);
            var varIsFloat = varEntry.isFloat();
            var destReg = (varIsFloat) ? this.newFloatReg() : this.newIntReg();
            varEntry.setRegNum(destReg);

            // If array of arrays, recursively allocate the array
            // and initialize the current variable with its pointer
            if (varEntry.getType() instanceof ArrayType arrayType) {
                var pointerReg = this.allocateArrays(arrayType);
                this.emit(new Move(MoveOp.INT_TO_INT, destReg, pointerReg));
            }

            // Initialize the variable if given
            if (optinit.childCount() > 1) {
                var literalNode = optinit.getLast().getFirst();
                var literalStr = literalNode.getValue();
                if (literalNode.getSymbol() == Symbol.INTLIT) {
                    var value = Integer.parseInt(literalStr);
                    if (varIsFloat) {
                        this.emit(new MoveConst(destReg, (float) value));
                    } else {
                        this.emit(new MoveConst(destReg, value));
                    }
                } else {
                    var value = Float.parseFloat(literalStr);
                    this.emit(new MoveConst(destReg, value));
                }
            }

            // Move to next ID or exit loop if none left
            if (idsNode.childCount() == 1) {
                break;
            }

            idsNode = idsNode.get(2);
        }
    }

    private void generateVardecls(ASTNode vardecls) throws IRGenException {
        while (vardecls.childCount() > 1) {
            this.generateVardecl(vardecls.getFirst());
            vardecls = vardecls.getLast();
        }
    }

    public List<Instruction> generateProgram() throws IRGenException {
        // Program will be laid out like this:
        // - vardecl initialization
        // - goto main
        // - function definitions
        // - main:
        // - main statements

        var mainLabel = this.newLabel();
        this.initFuncRegs();
        this.generateVardecls(this.rootNode.get(1).get(1));
        this.emit(new Goto(mainLabel));
        this.generateFuncdecls(this.rootNode.get(1).get(2));
        this.emit(new Label(mainLabel));
        this.funcName = "main";
        this.generateStatements(this.rootNode.get(3), "");
        this.fixLabels();
        return this.instructions;
    }

    /**
     * Allocate registers for the parameters and return value of each function. Also associates the
     * function with a label.
     */
    private void initFuncRegs() {
        for (var funcName : this.funcMap.keySet()) {
            var funcMapEntry = this.funcMap.get(funcName);

            // Can't use the function name as a label as it may conflict with other labels,
            // so we create a new label and associate it with the function.
            funcMapEntry.setLabel(this.newLabel());

            // Create reg for return value
            var returnReg =
                    (funcMapEntry.isFloat()) ? this.newFloatReg() : this.newIntReg();
            funcMapEntry.setReturnReg(returnReg);

            // Create regs for params
            var params = funcMapEntry.getParams().values();
            for (var param : params) {
                var paramReg = (param.isFloat()) ? this.newFloatReg() : this.newIntReg();
                param.setRegNum(paramReg);
            }
        }
    }

    /**
     * During instruction generation, CALL, BRANCH, and GOTO instructions do not know the exact
     * target address because the label for that address might not have been emitted yet. After all
     * labels have been emitted, these instructions should be fixed to contain the correct label
     * addresses.
     */
    private void fixLabels() throws IRGenException {

        // Get each label's address (their index in the instruction list)
        var labels = new HashMap<String, Integer>();
        for (int i = 0; i < this.instructions.size(); i++) {
            var inst = this.instructions.get(i);
            if (!(inst instanceof Label)) {
                continue;
            }

            var label = (Label) (inst);
            if (labels.put(label.getName(), i) != null) {
                throw new IRGenException("Unexpected error");
            }
        }

        // Apply the label addresses to each instruction
        for (var inst : this.instructions) {
            if (inst instanceof Branch branchInst) {
                branchInst.setDestAddr(labels.get(branchInst.getLabel()));
            } else if (inst instanceof Call callInst) {
                var funcLabel = this.funcMap.get(callInst.getLabel()).getLabel();
                callInst.setDestAddr(labels.get(funcLabel));
            } else if (inst instanceof Goto gotoInst) {
                gotoInst.setDestAddr(labels.get(gotoInst.getLabel()));
            }
        }
    }

    public void printIR() throws IRGenException {

        // Construct mapping of label -> function name
        var funcNames = new HashMap<String, String>();
        funcNames.put("label_0", "main");
        for (var func : this.funcMap.entrySet()) {
            var funcEntry = func.getValue();
            if (funcNames.put(funcEntry.getLabel(), func.getKey()) != null) {
                throw new IRGenException("Unexpected error");
            }
        }

        // When printing instructions:
        // - Indent code from functions / main
        // - Replace labels with functions when possible
        var output = new StringBuilder();
        var shouldIndent = false;
        for (var inst : this.instructions) {
            if (inst instanceof Label labelInst) {
                var name = labelInst.getName();
                if (funcNames.containsKey(name)) {
                    output.append(funcNames.get(name) + ":" + "\n");
                    shouldIndent = true;
                } else {
                    output.append(inst.toString() + "\n");
                }
            } else if (inst instanceof Goto gotoInst) {
                var name = gotoInst.getLabel();
                if (shouldIndent) {
                    output.append("\t");
                }

                if (funcNames.containsKey(name)) {
                    output.append("GOTO " + funcNames.get(name) + "\n");
                } else {
                    output.append(inst.toString() + "\n");
                }
            } else if (inst instanceof Branch branchInst) {
                var name = branchInst.getLabel();
                if (shouldIndent) {
                    output.append("\t");
                }

                if (funcNames.containsKey(name)) {
                    output.append("BRANCH " + funcNames.get(name) + "\n");
                } else {
                    output.append(inst.toString() + "\n");
                }
            } else {
                if (shouldIndent) {
                    output.append("\t");
                }
                output.append(inst.toString() + "\n");
            }
        }

        System.out.print(output.toString());
    }
}
