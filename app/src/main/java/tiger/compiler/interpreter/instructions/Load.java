package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Load extends Instruction {
    private int destReg;
    private int baseReg;
    private int offsetReg;
    private boolean floatOp;

    public Load(int destReg, int baseReg, int offsetReg, boolean floatOp) {
        this.destReg = destReg;
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
            regFile.setFloat(this.destReg,
                    mem.getFloat(regFile.getInt(this.baseReg), regFile.getInt(this.offsetReg)));
        } else {
            regFile.setInt(this.destReg,
                    mem.getInt(regFile.getInt(this.baseReg), regFile.getInt(this.offsetReg)));
        }
    }

    @Override
    public String toString() {
        var prefix = "Load" + ((this.floatOp) ? "_F r" : " r");
        return prefix + this.destReg + " r" + this.baseReg + " r" + this.offsetReg;
    }
}
