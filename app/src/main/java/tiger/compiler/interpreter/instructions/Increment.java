package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Increment extends Instruction {
    private int incReg;

    public Increment(int incReg) {
        this.incReg = incReg;
    }

    @Override
    public void execute(RegisterFile regFile, Memory mem, Scanner scan, PrintWriter out) {
        regFile.incPC();
        regFile.setInt(this.incReg, regFile.getInt(this.incReg) + 1);
    }

    @Override
    public String toString() {
        return "INC r" + this.incReg;
    }
}
