package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Store extends Instruction {
    private int srcReg;
    private int baseReg;
    private int offsetReg;
    private boolean floatOp;

    public Store(int srcReg, int baseReg, int offsetReg, boolean floatOp) {
        this.srcReg = srcReg;
        this.baseReg = baseReg;
        this.offsetReg = offsetReg;
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
            mem.setFloat(regFile.getInt(this.baseReg), regFile.getInt(this.offsetReg),
                    regFile.getFloat(srcReg));
        } else {
            mem.setInt(regFile.getInt(this.baseReg), regFile.getInt(this.offsetReg),
                    regFile.getInt(srcReg));
        }
    }

    @Override
    public String toString() {
        var prefix = "STORE" + ((this.floatOp) ? "_F r" : " r");
        return prefix + this.srcReg + " r" + this.baseReg + " r" + this.offsetReg;
    }
}
