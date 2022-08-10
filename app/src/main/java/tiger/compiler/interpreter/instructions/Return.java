package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Return extends Instruction {

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.decSP();
        int callerPC = (int) mem.pop(regFile.getSP());
        regFile.setPC(callerPC);
    }

    @Override
    public String toString() {
        return "RET";
    }
}
