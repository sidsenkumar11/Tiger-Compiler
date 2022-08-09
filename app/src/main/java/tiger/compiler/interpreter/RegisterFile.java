package tiger.compiler.interpreter;

import java.util.ArrayList;
import java.util.List;

public class RegisterFile {
    private List<Integer> intRegs;
    private List<Float> floatRegs;
    private int PC;
    private int SP;
    private boolean conditionFlag;

    public RegisterFile(int numIntRegs, int numFloatRegs) {
        this.intRegs = new ArrayList<>(numIntRegs);
        this.floatRegs = new ArrayList<>(numFloatRegs);
        this.PC = 0;
        this.SP = 0;
        this.conditionFlag = false;

        for (var i = 0; i < numIntRegs; i++) {
            this.intRegs.add(0);
        }

        for (var i = 0; i < numFloatRegs; i++) {
            this.floatRegs.add((float) 0);
        }
    }

    public int getInt(int regno) {
        return this.intRegs.get(regno);
    }

    public void setInt(int regno, int val) {
        this.intRegs.set(regno, val);
    }

    public float getFloat(int regno) {
        return this.floatRegs.get(regno);
    }

    public void setFloat(int regno, float val) {
        this.floatRegs.set(regno, val);
    }

    public int getPC() {
        return this.PC;
    }

    public void setPC(int newPC) {
        this.PC = newPC;
    }

    public void incPC() {
        this.PC++;
    }

    public int getSP() {
        return this.SP;
    }

    public void incSP() {
        this.SP++;
    }

    public void decSP() {
        this.SP--;
    }

    public boolean getCondition() {
        return this.conditionFlag;
    }

    public void setCondition(boolean newCondition) {
        this.conditionFlag = newCondition;
    }
}
