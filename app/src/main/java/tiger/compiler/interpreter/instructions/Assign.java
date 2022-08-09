package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Assign extends Instruction {
    private int destReg;
    private int srcReg;
    private boolean floatOp;

    public Assign(int destReg, int srcReg, boolean floatOp) {
        this.destReg = destReg;
        this.srcReg = srcReg;
        this.floatOp = floatOp;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        if (this.floatOp) {
            regFile.setFloat(this.destReg, regFile.getFloat(this.srcReg));
        } else {
            regFile.setInt(this.destReg, regFile.getInt(this.srcReg));
        }
    }

    @Override
    public String toString() {
        var prefix = "Assign" + ((this.floatOp) ? "_F r" : " r");
        return prefix + this.destReg + " r" + this.srcReg;
    }
}
