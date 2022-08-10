package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Pop extends Instruction {
    private int regNum;
    private boolean isFloat;

    public Pop(int regNum, boolean isFloat) {
        this.regNum = regNum;
        this.isFloat = isFloat;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
        regFile.decSP();

        var data = mem.pop(regFile.getSP());
        if (this.isFloat) {
            regFile.setFloat(this.regNum, (float) data);
        } else {
            regFile.setInt(this.regNum, (int) data);
        }
    }

    @Override
    public String toString() {
        var prefix = "POP" + (this.isFloat ? "_F r" : " r");
        return prefix + this.regNum;
    }
}
