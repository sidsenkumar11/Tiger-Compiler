package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;
import tiger.compiler.typechecker.RegEntry;

public class ArithmeticInstruction extends Instruction {
    private ArithmeticOp op;
    private int destReg;
    private RegEntry srcReg1;
    private RegEntry srcReg2;
    private boolean floatOp;

    public ArithmeticInstruction(ArithmeticOp op, int destReg, RegEntry srcReg1, RegEntry srcReg2) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg1 = srcReg1;
        this.srcReg2 = srcReg2;
        this.floatOp = srcReg1.isFloat() || srcReg2.isFloat();
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        double src1 = (this.srcReg1.isFloat()) ? regFile.getFloat(this.srcReg1.regNum())
                : regFile.getInt(this.srcReg1.regNum());
        double src2 = (this.srcReg2.isFloat()) ? regFile.getFloat(this.srcReg2.regNum())
                : regFile.getInt(this.srcReg2.regNum());

        double result = 0;
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
                result = (int) src1 & (int) src2;
                break;
            case OR:
                result = (int) src1 | (int) src2;
                break;
            default:
                result = 0;
                break;
        }

        if (this.floatOp) {
            regFile.setFloat(this.destReg, (float) result);
        } else {
            regFile.setInt(this.destReg, (int) result);
        }
    }

    @Override
    public String toString() {
        String prefix = this.op.toString() + ((this.floatOp) ? " rf" : " r");
        var src1Pre = (this.srcReg1.isFloat()) ? " rf" : " r";
        var src2Pre = (this.srcReg1.isFloat()) ? " rf" : " r";
        return prefix + this.destReg + src1Pre + this.srcReg1.regNum() + src2Pre
                + this.srcReg2.regNum();
    }
}
