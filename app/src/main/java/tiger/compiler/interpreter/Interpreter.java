package tiger.compiler.interpreter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import tiger.compiler.interpreter.instructions.Instruction;

public class Interpreter {
    public static void Run(List<Instruction> IR, int intRegCount, int floatRegCount, InputStream in,
            PrintWriter out, boolean debug) {
        var regFile = new RegisterFile(intRegCount, floatRegCount);
        var mem = new Memory();

        try (Scanner scan = new Scanner(in)) {
            while (regFile.getPC() < IR.size()) {
                var instruction = IR.get(regFile.getPC());

                if (debug) {
                    System.out.println(instruction);
                }

                instruction.execute(regFile, mem, scan, out);
            }
        }
    }
}
