package tiger.compiler.interpreter.instructions;

import java.io.PrintWriter;
import java.util.Scanner;
import tiger.compiler.interpreter.Memory;
import tiger.compiler.interpreter.RegisterFile;

public abstract class Instruction {
    public abstract void execute(
            RegisterFile regFile,
            Memory mem,
            Scanner scan,
            PrintWriter out);
}
