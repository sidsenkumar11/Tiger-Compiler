package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Move extends Instruction {
    private MoveOp moveOp;
    private int destReg;
    private int srcReg;

    public Move(MoveOp moveOp, int destReg, int srcReg) {
        this.moveOp = moveOp;
        this.destReg = destReg;
        this.srcReg = srcReg;
    }

    @Override
    public void execute(RegisterFile regFile, Memory mem, Scanner scan, PrintWriter out) {
        regFile.incPC();

        switch (this.moveOp) {
            case INT_TO_INT:
                regFile.setInt(this.destReg, regFile.getInt(this.srcReg));
                break;
            case INT_TO_FLOAT:
                regFile.setFloat(this.destReg, regFile.getInt(this.srcReg));
                break;
            case FLOAT_TO_FLOAT:
                regFile.setFloat(this.destReg, regFile.getFloat(this.srcReg));
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        var prefix = "Move." + this.moveOp.toString();
        return prefix + " r" + this.destReg + " r" + this.srcReg;
    }
}
