package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Push extends Instruction {
    private int regNum;
    private boolean isFloat;

    public Push(int regNum, boolean isFloat) {
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

        if (this.isFloat) {
            mem.push(regFile.getSP(), regFile.getFloat(this.regNum));
        } else {
            mem.push(regFile.getSP(), regFile.getInt(this.regNum));
        }

        regFile.incSP();
    }

    @Override
    public String toString() {
        var prefix = "PUSH" + (this.isFloat ? "_F r" : " r");
        return prefix + this.regNum;
    }
}
