package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;
import tiger.compiler.typechecker.RegEntry;

public class Push extends Instruction {
    private RegEntry regEntry;

    public Push(RegEntry regEntry) {
        this.regEntry = regEntry;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();

        if (this.regEntry.isFloat()) {
            mem.push(regFile.getSP(), regFile.getFloat(this.regEntry.regNum()));
        } else {
            mem.push(regFile.getSP(), regFile.getInt(this.regEntry.regNum()));
        }

        regFile.incSP();
    }

    @Override
    public String toString() {
        var prefix = "Push" + (this.regEntry.isFloat() ? "_F r" : " r");
        return prefix + regEntry.regNum();
    }
}
