package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;
import tiger.compiler.typechecker.RegEntry;

public class Pop extends Instruction {
    private RegEntry regEntry;

    public Pop(RegEntry regEntry) {
        this.regEntry = regEntry;
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
        if (this.regEntry.isFloat()) {
            regFile.setFloat(this.regEntry.regNum(), (float) data);
        } else {
            regFile.setInt(this.regEntry.regNum(), (int) data);
        }
    }

    @Override
    public String toString() {
        var prefix = "Pop" + (this.regEntry.isFloat() ? "_F r" : " r");
        return prefix + regEntry.regNum();
    }
}
