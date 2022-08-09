package tiger.compiler.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tiger.compiler.interpreter.instructions.AllocArray;
import tiger.compiler.interpreter.instructions.ArithmeticInstruction;
import tiger.compiler.interpreter.instructions.ArithmeticInstructionImmediate;
import tiger.compiler.interpreter.instructions.ArithmeticOp;
import tiger.compiler.interpreter.instructions.Branch;
import tiger.compiler.interpreter.instructions.Call;
import tiger.compiler.interpreter.instructions.Cmp;
import tiger.compiler.interpreter.instructions.ComparisonOp;
import tiger.compiler.interpreter.instructions.Goto;
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

        this.funcName = "";
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
        if (this.funcName.length() > 0) {
            var localsMap = this.funcMap.get(this.funcName).getParamRegs();
            if (localsMap.containsKey(variable)) {
                return localsMap.get(variable).regNum();
            }
        }

        return this.varMap.get(variable).getRegNum();
    }

    private int generateConst(ASTNode constNode) throws IRGenException {
        int destReg = 0;
        var literalSymbol = constNode.getFirst().getSymbol();
        if (literalSymbol == Symbol.INTLIT) {
            destReg = this.newIntReg();
            var intlit = Integer.parseInt(constNode.getFirst().getValue());
            this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                    intlit));
        } else if (literalSymbol == Symbol.FLOATLIT) {
            destReg = this.newFloatReg();
            var floatLit = Float.parseFloat(constNode.getFirst().getValue());
            this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                    floatLit));
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
            var floatDest = factorNode.getType().equals(new FloatType());
            var destReg = (floatDest) ? this.newFloatReg() : this.newIntReg();
            var baseReg = this.getVariable(factorChild.getValue());
            var offsetReg = this.generateNumexpr(factorNode.get(2));
            this.emit(new Load(destReg, baseReg, offsetReg, floatDest));
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
            var opNode = termNode.get(1).getFirst();

            var termReg = this.generateTerm(subTermNode);
            var factorReg = this.generateFactor(factorNode);
            var op = (opNode.getSymbol() == Symbol.STAR) ? ArithmeticOp.MUL : ArithmeticOp.DIV;

            var floatDest = termNode.getType().equals(new FloatType());
            var destReg = (floatDest) ? this.newFloatReg() : this.newIntReg();
            this.emit(new ArithmeticInstruction(op, destReg, termReg, factorReg, floatDest));
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
            var opNode = numexprNode.get(1).getFirst();

            var numexprReg = this.generateNumexpr(subNumexprNode);
            var termReg = this.generateTerm(termNode);
            var op = (opNode.getSymbol() == Symbol.PLUS) ? ArithmeticOp.ADD : ArithmeticOp.SUB;

            var floatDest = numexprNode.getType().equals(new FloatType());
            var destReg = (floatDest) ? this.newFloatReg() : this.newIntReg();
            this.emit(new ArithmeticInstruction(op, destReg, numexprReg, termReg, floatDest));
            return destReg;
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private int generatePred(ASTNode predNode) throws IRGenException {
        var numexprReg1 = this.generateNumexpr(predNode.getFirst());
        var numexprReg2 = this.generateNumexpr(predNode.getLast());
        var destReg = this.newIntReg();
        var opSymbol = predNode.get(1).getFirst().getSymbol();

        if (opSymbol == Symbol.EQUALS) {
            this.emit(new Cmp(ComparisonOp.EQ, numexprReg1, numexprReg2));
        } else if (opSymbol == Symbol.NOT_EQUALS) {
            this.emit(new Cmp(ComparisonOp.NEQ, numexprReg1, numexprReg2));
        } else if (opSymbol == Symbol.LESS_OR_EQ) {
            this.emit(new Cmp(ComparisonOp.LEQ, numexprReg1, numexprReg2));
        } else if (opSymbol == Symbol.GREATER_OR_EQ) {
            this.emit(new Cmp(ComparisonOp.GEQ, numexprReg1, numexprReg2));
        } else if (opSymbol == Symbol.LESS_THAN) {
            this.emit(new Cmp(ComparisonOp.LT, numexprReg1, numexprReg2));
        } else if (opSymbol == Symbol.GREATER_THAN) {
            this.emit(new Cmp(ComparisonOp.GT, numexprReg1, numexprReg2));
        } else {
            throw new IRGenException("Unexpected error");
        }

        // Pred result is 1 or 0
        var conditionFalse = this.newLabel();
        var endAssign = this.newLabel();
        this.emit(new Branch(conditionFalse));
        this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0, 0));
        this.emit(new Goto(endAssign));
        this.emit(new Label(conditionFalse));
        this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0, 1));
        this.emit(new Label(endAssign));
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
            this.emit(new ArithmeticInstruction(ArithmeticOp.AND, destReg, subClauseReg, predReg,
                    false));
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
            this.emit(new ArithmeticInstruction(ArithmeticOp.OR, destReg, subBoolexprReg, clauseReg,
                    false));
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
        var lvalueIsFloat = lvalueNode.getType().equals(new FloatType());

        // Set LHS
        var optOffsetNode = lvalueNode.getLast();
        if (optOffsetNode.childCount() == 1) {
            if (lvalueIsFloat && rhsType.equals(new IntType())) {
                var tempFloatReg = this.newFloatReg();
                this.emit(new Move(MoveOp.INT_TO_FLOAT, tempFloatReg, rhsReg));
                rhsReg = tempFloatReg;
            }

            var opType = (lvalueIsFloat) ? MoveOp.FLOAT_TO_FLOAT : MoveOp.INT_TO_INT;
            this.emit(new Move(opType, lvalueReg, rhsReg));
        } else {
            var offsetNumexprNode = optOffsetNode.get(1);
            var offsetNumexprReg = this.generateNumexpr(offsetNumexprNode);

            var lvalueIsFloatArray = lvalueNode.getType().equals(new FloatType());
            this.emit(new Store(rhsReg, lvalueReg, offsetNumexprReg, lvalueIsFloatArray));
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
        } else if (stmtType == Symbol.optstore) {

            // Generate arguments and place into arg registers
            var argRegs = new ArrayList<RegEntry>();
            var numexprsNode = stmtNode.get(3);
            if (numexprsNode.getFirst().getSymbol() == Symbol.neexprs) {
                var neexprsNode = numexprsNode.getFirst();
                while (neexprsNode.childCount() > 1) {
                    var numexprNode = neexprsNode.getFirst();
                    neexprsNode = neexprsNode.get(2);
                    var regNum = this.generateNumexpr(numexprNode);
                    var isFloat = numexprNode.getType().equals(new FloatType());
                    argRegs.add(new RegEntry(regNum, isFloat));
                }

                var numexprNode = neexprsNode.getFirst();
                var regNum = this.generateNumexpr(numexprNode);
                var isFloat = numexprNode.getType().equals(new FloatType());
                argRegs.add(new RegEntry(regNum, isFloat));
            }

            // Move args to expected param registers
            var funcId = stmtNode.get(1).getValue();
            var funcSig = this.funcMap.get(funcId);
            var funcReturnType = funcSig.getReturnType();
            var funcParamRegs = new ArrayList<RegEntry>(funcSig.getParamRegs().values());
            for (int i = 0; i < funcParamRegs.size(); i++) {
                // Push old param reg to support recursive calls
                var paramReg = funcParamRegs.get(i).regNum();
                var paramIsFloat = funcParamRegs.get(i).isFloat();
                this.emit(new Push(new RegEntry(paramReg, paramIsFloat)));

                // Move new arg value into param reg
                var argIsFloat = argRegs.get(i).isFloat();
                var opType = MoveOp.INT_TO_INT;
                if (paramIsFloat) {
                    if (argIsFloat) {
                        opType = MoveOp.FLOAT_TO_FLOAT;
                    } else {
                        opType = MoveOp.INT_TO_FLOAT;
                    }
                }

                this.emit(new Move(opType, paramReg, argRegs.get(i).regNum()));
            }

            // Call the function
            var funcResultReg = this.funcMap.get(funcId).getReturnReg().regNum();

            this.emit(new Call(funcId, funcResultReg, argRegs));

            // Pop old param regs
            for (int i = funcParamRegs.size() - 1; i >= 0; i--) {
                var paramReg = funcParamRegs.get(i).regNum();
                var paramIsFloat = funcParamRegs.get(i).isFloat();
                this.emit(new Pop(new RegEntry(paramReg, paramIsFloat)));
            }

            // Optionally store the result
            var optStoreNode = stmtNode.getFirst();
            if (optStoreNode.childCount() > 1) {
                var lvalueNode = optStoreNode.getFirst();
                this.generateLvalueAssign(lvalueNode, funcReturnType, funcResultReg);
            }
        } else if (stmtType == Symbol.BREAK) {
            this.emit(new Goto(breakLabel));
        } else if (stmtType == Symbol.RETURN) {
            var resultReg = this.generateNumexpr(stmtNode.getLast());
            var destReg = this.funcMap.get(this.funcName).getReturnReg();
            var destRegNum = destReg.regNum();
            var destIsFloat = destReg.isFloat();

            var op = MoveOp.INT_TO_INT;
            if (destIsFloat) {
                if (stmtNode.getLast().getType().equals(new FloatType())) {
                    op = MoveOp.FLOAT_TO_FLOAT;
                } else {
                    op = MoveOp.INT_TO_FLOAT;
                }
            }
            this.emit(new Move(op, destRegNum, resultReg));
            this.emit(new Return());
        } else if (stmtType == Symbol.FOR) {
            var leftBoundReg = this.generateNumexpr(stmtNode.get(3));
            var rightBoundReg = this.generateNumexpr(stmtNode.get(5));
            var indexReg = this.getVariable(stmtNode.get(1).getValue());
            this.emit(new Move(MoveOp.INT_TO_INT, indexReg, leftBoundReg));

            var loopStart = this.newLabel();
            this.emit(new Label(loopStart));
            this.emit(new Cmp(ComparisonOp.GEQ, indexReg, rightBoundReg));
            var loopEnd = this.newLabel();
            this.emit(new Branch(loopEnd));
            this.generateStatements(stmtNode.get(7), loopEnd);
            this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, indexReg, indexReg, 1));
            this.emit(new Goto(loopStart));
            this.emit(new Label(loopEnd));
        } else if (stmtType == Symbol.WHILE) {
            var loopStart = this.newLabel();
            this.emit(new Label(loopStart));
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, boolexprReg, 0));
            var loopEnd = this.newLabel();
            this.emit(new Branch(loopEnd));
            this.generateStatements(stmtNode.get(3), loopEnd);
            this.emit(new Goto(loopStart));
            this.emit(new Label(loopEnd));
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 5) {
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, boolexprReg, 0));
            var conditionFalse = this.newLabel();
            this.emit(new Branch(conditionFalse));
            this.generateStatements(stmtNode.get(3), conditionFalse);
            this.emit(new Label(conditionFalse));
        } else if (stmtType == Symbol.IF && stmtNode.childCount() == 7) {
            var boolexprReg = this.generateBoolexpr(stmtNode.get(1));
            this.emit(new Cmp(ComparisonOp.EQ, boolexprReg, 0));
            var conditionFalse = this.newLabel();
            var afterLabel = this.newLabel();
            this.emit(new Branch(conditionFalse));
            this.generateStatements(stmtNode.get(3), afterLabel);
            this.emit(new Goto(afterLabel));
            this.emit(new Label(conditionFalse));
            this.generateStatements(stmtNode.get(5), afterLabel);
            this.emit(new Label(afterLabel));
        } else {
            throw new IRGenException("Unexpected error");
        }
    }

    private void generateStatements(ASTNode stmtsNode, String breakLabel) throws IRGenException {
        while (stmtsNode.childCount() != 1) {
            var stmtNode = stmtsNode.get(0).get(0);
            stmtsNode = stmtsNode.get(1);
            this.generateStatement(stmtNode, breakLabel);
        }

        var stmtNode = stmtsNode.get(0).get(0);
        this.generateStatement(stmtNode, breakLabel);
    }

    private void generateFuncdecls(ASTNode funcdecls) throws IRGenException {
        // Generate library funcdecls
        var readiLabel = this.newLabel();
        var readfLabel = this.newLabel();
        var printiLabel = this.newLabel();
        var printfLabel = this.newLabel();
        this.emit(new Label(readiLabel));
        this.emit(new Label(readfLabel));
        this.emit(new Label(printiLabel));
        this.emit(new Label(printfLabel));
        this.funcMap.get("readi").setLabel(readiLabel);
        this.funcMap.get("readf").setLabel(readfLabel);
        this.funcMap.get("printi").setLabel(printiLabel);
        this.funcMap.get("printf").setLabel(printfLabel);

        // Generate everything else given
        while (funcdecls.childCount() > 1) {
            var funcdecl = funcdecls.getFirst();
            funcdecls = funcdecls.getLast();

            this.funcName = funcdecl.get(1).getValue();
            var newLabel = this.newLabel();
            this.funcMap.get(this.funcName).setLabel(newLabel);
            this.emit(new Label(newLabel));
            this.generateStatements(funcdecl.get(7), "");
            this.emit(new Return());
        }

        this.funcName = "";
    }

    private int allocateArrays(ArrayType current) throws IRGenException {
        var subType = current.getSubType();
        var arraySizeReg = this.newIntReg();
        var destReg = this.newIntReg();

        this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, arraySizeReg, 0,
                current.getSize()));

        if (subType.equals(new IntType())) {
            this.emit(new AllocArray(destReg, arraySizeReg, 0, false));
        } else if (subType.equals(new FloatType())) {
            this.emit(new AllocArray(destReg, arraySizeReg, 0, true));
        } else if (subType instanceof ArrayType subArrayType) {
            this.emit(new AllocArray(destReg, arraySizeReg, 0, false));
            for (var i = 0; i < current.getSize(); i++) {
                var pointerReg = this.allocateArrays(subArrayType);
                var offsetReg = this.newIntReg();
                this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, offsetReg, 0, i));
                this.emit(new Store(pointerReg, destReg, offsetReg, false));
            }
        } else {
            throw new IRGenException("Unexpected error");
        }

        return destReg;
    }

    private void generateVardecl(ASTNode vardecl) throws IRGenException {

        var optinit = vardecl.get(4);
        var idsNode = vardecl.get(1);
        while (idsNode.childCount() > 1) {
            var identifier = idsNode.getFirst().getValue();
            var entry = this.varMap.get(identifier);
            idsNode = idsNode.get(2);

            var variableIsFloat = entry.getType().equals(new FloatType());
            var destReg = (variableIsFloat) ? this.newFloatReg() : this.newIntReg();

            if (entry.getType() instanceof ArrayType current) {
                var srcReg = this.allocateArrays(current);
                this.emit(new Move(MoveOp.INT_TO_INT, destReg, srcReg));
            }

            if (optinit.childCount() > 1) {
                var literalNode = optinit.getLast().getFirst();
                if (literalNode.getSymbol() == Symbol.INTLIT) {
                    var value = Integer.parseInt(literalNode.getValue());
                    if (variableIsFloat) {
                        this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                                (float) value));
                    } else {
                        this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                                value));
                    }
                } else {
                    var value = Float.parseFloat(literalNode.getValue());
                    this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                            value));
                }
            }

            entry.setRegNum(destReg);
        }

        var identifier = idsNode.getFirst().getValue();
        var entry = this.varMap.get(identifier);
        var variableIsFloat = entry.getType().equals(new FloatType());
        var destReg = (variableIsFloat) ? this.newFloatReg() : this.newIntReg();

        if (entry.getType() instanceof ArrayType current) {
            var srcReg = this.allocateArrays(current);
            this.emit(new Move(MoveOp.INT_TO_INT, destReg, srcReg));
        }

        if (optinit.childCount() > 1) {
            var literalNode = optinit.getLast().getFirst();
            if (literalNode.getSymbol() == Symbol.INTLIT) {
                var value = Integer.parseInt(literalNode.getValue());
                if (variableIsFloat) {
                    this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                            (float) value));
                } else {
                    this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                            value));
                }
            } else {
                var value = Float.parseFloat(literalNode.getValue());
                this.emit(new ArithmeticInstructionImmediate(ArithmeticOp.ADD, destReg, 0,
                        value));
            }
        }

        entry.setRegNum(destReg);
    }

    private void generateVardecls(ASTNode vardecls) throws IRGenException {
        while (vardecls.childCount() > 1) {
            var vardecl = vardecls.getFirst();
            vardecls = vardecls.getLast();

            this.generateVardecl(vardecl);
        }
    }

    private void fixLabels() throws IRGenException {
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

    private void initFuncRegs() {
        for (var funcName : this.funcMap.keySet()) {
            var funcMapEntry = this.funcMap.get(funcName);

            // Create reg for return value
            var returnType = funcMapEntry.getReturnType();
            var returnIsFloat = returnType.equals(new FloatType());
            var returnReg =
                    (returnIsFloat) ? this.newFloatReg() : this.newIntReg();
            funcMapEntry.setReturnReg(new RegEntry(returnReg, returnIsFloat));

            // Create regs for params
            var params = funcMapEntry.getParams().entrySet();
            var paramRegs = new LinkedHashMap<String, RegEntry>();
            for (var param : params) {
                var paramIsFloat = (param.getValue().equals(new FloatType()));
                var paramReg = (paramIsFloat) ? this.newFloatReg() : this.newIntReg();
                paramRegs.put(param.getKey(), new RegEntry(paramReg, paramIsFloat));
            }

            funcMapEntry.setParamRegs(paramRegs);
        }
    }

    public List<Instruction> generateProgram() throws IRGenException {
        this.initFuncRegs();
        this.generateVardecls(this.rootNode.get(1).get(1));

        // Don't execute the functions
        var programStart = this.newLabel();
        this.emit(new Goto(programStart));

        this.generateFuncdecls(this.rootNode.get(1).get(2));

        this.emit(new Label(programStart));
        this.generateStatements(this.rootNode.get(3), "");
        this.fixLabels();
        return this.instructions;
    }

    public void printIR() {
        for (var inst : this.instructions) {
            System.out.println(inst);
        }
    }
}
