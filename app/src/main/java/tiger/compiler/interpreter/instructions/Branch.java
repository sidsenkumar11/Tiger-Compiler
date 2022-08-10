package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Branch extends Instruction {
    private String label;
    private int destAddr;

    public Branch(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setDestAddr(int destAddr) {
        this.destAddr = destAddr;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        if (regFile.getCondition()) {
            regFile.setPC(this.destAddr);
        } else {
            regFile.incPC();
        }
    }

    @Override
    public String toString() {
        return "BRANCH " + this.label;
    }
}
