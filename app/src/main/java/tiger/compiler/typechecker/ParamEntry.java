package tiger.compiler.typechecker;

import tiger.compiler.typechecker.types.FloatType;
import tiger.compiler.typechecker.types.Type;

public class ParamEntry {
    private Type type;
    private int regNum;

    public ParamEntry(Type type) {
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

    public boolean isFloat() {
        return this.type.equals(new FloatType());
    }
}
