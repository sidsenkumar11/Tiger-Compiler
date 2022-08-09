package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class ArithmeticInstructionImmediate extends Instruction {
    private ArithmeticOp op;
    private int destReg;
    private int srcReg;
    private int intImm;
    private float floatImm;
    private boolean floatOp;

    public ArithmeticInstructionImmediate(ArithmeticOp op, int destReg, int srcReg, int intImm) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg = srcReg;
        this.intImm = intImm;
        this.floatOp = false;
    }

    public ArithmeticInstructionImmediate(ArithmeticOp op, int destReg, int srcReg,
            float floatImm) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg = srcReg;
        this.floatImm = floatImm;
        this.floatOp = true;
    }

    private void runIntOperation(RegisterFile regFile) {
        int src1 = regFile.getInt(this.srcReg);
        int result = 0;
        switch (this.op) {
            case ADD:
                result = src1 + this.intImm;
                break;
            case SUB:
                result = src1 - this.intImm;
                break;
            case MUL:
                result = src1 * this.intImm;
                break;
            case DIV:
                result = src1 / this.intImm;
                break;
            default:
                result = 0;
                break;
        }

        regFile.setInt(this.destReg, result);
    }

    private void runFloatOperation(RegisterFile regFile) {
        float src1 = regFile.getFloat(this.srcReg);
        float result = 0;
        switch (this.op) {
            case ADD:
                result = src1 + this.floatImm;
                break;
            case SUB:
                result = src1 - this.floatImm;
                break;
            case MUL:
                result = src1 * this.floatImm;
                break;
            case DIV:
                result = src1 / this.floatImm;
                break;
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

        if (this.floatOp) {
            return prefix + this.destReg + " r" + this.srcReg + " " + this.floatImm;
        } else {
            return prefix + this.destReg + " r" + this.srcReg + " " + this.intImm;
        }
    }
}
