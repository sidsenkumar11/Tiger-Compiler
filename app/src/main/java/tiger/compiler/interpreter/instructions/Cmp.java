package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;
import tiger.compiler.typechecker.RegEntry;

public class Cmp extends Instruction {
    private int destReg;
    private RegEntry srcReg1;
    private RegEntry srcReg2;
    private ComparisonOp op;

    public Cmp(ComparisonOp op, int destReg, RegEntry srcReg1, RegEntry srcReg2) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg1 = srcReg1;
        this.srcReg2 = srcReg2;
    }

    public Cmp(ComparisonOp op, int destReg, int srcReg1, int srcReg2) {
        this.op = op;
        this.destReg = destReg;
        this.srcReg1 = new RegEntry(srcReg1, false);
        this.srcReg2 = new RegEntry(srcReg2, false);;
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

        if (this.destReg != 0) {
            regFile.setInt(this.destReg, (regFile.getCondition()) ? 1 : 0);
        }
    }

    @Override
    public String toString() {
        var src1Pre = (this.srcReg1.isFloat()) ? " rf" : " r";
        var src2Pre = (this.srcReg2.isFloat()) ? " rf" : " r";

        return "CMP." + this.op.toString() + " r" + this.destReg + src1Pre + this.srcReg1.regNum()
                + src2Pre
                + this.srcReg2.regNum();
    }
}
