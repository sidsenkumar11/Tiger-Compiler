package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Cmp extends Instruction {
    private int srcReg1;
    private int srcReg2;
    private ComparisonOp op;

    public Cmp(ComparisonOp op, int srcReg1, int srcReg2) {
        this.op = op;
        this.srcReg1 = srcReg1;
        this.srcReg2 = srcReg2;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        int src1 = regFile.getInt(srcReg1);
        int src2 = regFile.getInt(srcReg2);
        switch (this.op) {
            case EQ:
                regFile.setCondition(src1 == src2);
                break;
            case NEQ:
                regFile.setCondition(src1 != src2);
                break;
            case LT:
                regFile.setCondition(src1 < src2);
                break;
            case GT:
                regFile.setCondition(src1 > src2);
                break;
            case LEQ:
                regFile.setCondition(src1 <= src2);
                break;
            case GEQ:
                regFile.setCondition(src1 >= src2);
                break;
            default:
                regFile.setCondition(false);
        }
    }

    @Override
    public String toString() {
        return "Cmp " + this.op.toString() + " r" + this.srcReg1 + " r" + this.srcReg2;
    }
}
