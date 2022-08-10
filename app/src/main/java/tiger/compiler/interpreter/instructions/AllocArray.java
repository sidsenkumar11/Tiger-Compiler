package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class AllocArray extends Instruction {
    private int destReg;
    private int arraySizeReg;
    private int valueReg;
    private boolean floatOp;

    public AllocArray(int destReg, int arraySizeReg, int valueReg, boolean floatOp) {
        this.destReg = destReg;
        this.arraySizeReg = arraySizeReg;
        this.valueReg = valueReg;
        this.floatOp = floatOp;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        int pointer = 0;
        if (this.floatOp) {
            pointer = mem.initFloatArray(regFile.getInt(this.arraySizeReg),
                    regFile.getFloat(this.valueReg));
        } else {
            pointer = mem.initIntArray(regFile.getInt(this.arraySizeReg),
                    regFile.getInt(this.valueReg));
        }
        regFile.setInt(this.destReg, pointer);
    }

    @Override
    public String toString() {
        var prefix = "ALLOC" + ((this.floatOp) ? "_F r" : " r");
        return prefix + this.destReg + " r" + this.arraySizeReg + " r" + this.valueReg;
    }
}
