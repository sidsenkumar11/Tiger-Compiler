package tiger.compiler.typechecker;

import tiger.compiler.typechecker.types.Type;

public class VariableTableEntry {
    private Type type;
    private int regNum;

    public VariableTableEntry(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public int getRegNum() {
        return this.regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }
}
