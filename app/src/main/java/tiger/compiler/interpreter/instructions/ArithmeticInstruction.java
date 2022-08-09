package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class ArithmeticInstruction extends Instruction {
    private ArithmeticOp op;
    private int destReg;
    private int srcReg1;
    private int srcReg2;
    private boolean floatOp;

    public ArithmeticInstruction(ArithmeticOp op, int destReg, int srcReg1, int srcReg2,
            boolean floatOp) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg1 = srcReg1;
        this.srcReg2 = srcReg2;
        this.floatOp = floatOp;
    }

    private void runIntOperation(RegisterFile regFile) {
        int src1 = regFile.getInt(this.srcReg1);
        int src2 = regFile.getInt(this.srcReg2);
        int result = 0;
        switch (this.op) {
            case ADD:
                result = src1 + src2;
                break;
            case SUB:
                result = src1 - src2;
                break;
            case MUL:
                result = src1 * src2;
                break;
            case DIV:
                result = src1 / src2;
                break;
            case AND:
                result = src1 & src2;
                break;
            case OR:
                result = src1 | src2;
                break;
            default:
                result = 0;
                break;
        }

        regFile.setInt(this.destReg, result);
    }

    private void runFloatOperation(RegisterFile regFile) {
        float src1 = regFile.getFloat(this.srcReg1);
        float src2 = regFile.getFloat(this.srcReg2);
        float result = 0;
        switch (this.op) {
            case ADD:
                result = src1 + src2;
                break;
            case SUB:
                result = src1 - src2;
                break;
            case MUL:
                result = src1 * src2;
                break;
            case DIV:
                result = src1 / src2;
                break;
            // AND & OR are undefined on floats
            default:
                result = 0;
                break;
        }

        regFile.setFloat(this.destReg, result);
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        if (floatOp) {
            this.runFloatOperation(regFile);
        } else {
            this.runIntOperation(regFile);
        }
    }

    @Override
    public String toString() {
        String prefix = this.op.toString() + ((this.floatOp) ? "_F r" : " r");
        return prefix + this.destReg + " r" + this.srcReg1 + " r" + this.srcReg2;
    }
}
