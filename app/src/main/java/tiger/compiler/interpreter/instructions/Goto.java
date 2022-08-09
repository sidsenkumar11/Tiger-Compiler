package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Goto extends Instruction {
    private String label;
    private int destAddr;

    public Goto(String label) {
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
        regFile.setPC(this.destAddr);
    }

    @Override
    public String toString() {
        return "Goto " + this.label + " (" + this.destAddr + ")";
    }
}
