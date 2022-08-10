package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;
import tiger.compiler.typechecker.RegEntry;

public class Call extends Instruction {
    private String label;
    private int destAddr;
    private int destReg;
    private List<RegEntry> paramRegs;

    public Call(String label, int destReg, List<RegEntry> paramRegs) {
        this.label = label;
        this.destReg = destReg;
        this.paramRegs = paramRegs;
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
        regFile.incPC();

        if (this.label.equals("printi")) {
            out.print("printi: ");
            out.println(regFile.getInt(this.paramRegs.get(0).regNum()));
        } else if (this.label.equals("printf")) {
            out.print("printf: ");
            out.println(regFile.getFloat(this.paramRegs.get(0).regNum()));
        } else if (this.label.equals("readi")) {
            out.print("readi: ");
            regFile.setInt(this.destReg, scan.nextInt());
        } else if (this.label.equals("readf")) {
            out.print("readf: ");
            regFile.setFloat(this.destReg, scan.nextFloat());
        } else {
            mem.push(regFile.getSP(), regFile.getPC());
            regFile.incSP();
            regFile.setPC(this.destAddr);
        }
    }

    @Override
    public String toString() {
        return "CALL " + this.label;
    }
}
