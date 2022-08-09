package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public class Label extends Instruction {
    private String name;

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out) {
        regFile.incPC();
    }

    @Override
    public String toString() {
        return this.name + ":";
    }
}
