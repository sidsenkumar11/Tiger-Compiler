package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class MoveConst extends Instruction {
    private int destReg;
    private int intImm;
    private float floatImm;
    private boolean floatOp;

    public MoveConst(int destReg, int intImm) {
        this.destReg = destReg;
        this.intImm = intImm;
        this.floatOp = false;
    }

    public MoveConst(int destReg, float floatImm) {
        this.destReg = destReg;
        this.floatImm = floatImm;
        this.floatOp = true;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        if (this.floatOp) {
            regFile.setFloat(this.destReg, this.floatImm);
        } else {
            regFile.setInt(this.destReg, this.intImm);
        }
    }

    @Override
    public String toString() {
        if (this.floatOp) {
            return "MOVE_CONST rf" + this.destReg + " " + this.floatImm;
        } else {
            return "MOVE_CONST r" + this.destReg + " " + this.intImm;
        }
    }
}
