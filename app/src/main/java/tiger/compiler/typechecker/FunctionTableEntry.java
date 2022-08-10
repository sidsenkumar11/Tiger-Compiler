package tiger.compiler.typechecker;

import java.util.LinkedHashMap;
import tiger.compiler.typechecker.types.FloatType;
import tiger.compiler.typechecker.types.Type;

public class FunctionTableEntry {
    private Type returnType;
    private LinkedHashMap<String, ParamEntry> params;
    private int returnReg;
    private String label;

    public FunctionTableEntry(Type returnType, LinkedHashMap<String, ParamEntry> params) {
        this.returnType = returnType;
        this.params = params;
    }

    public boolean isFloat() {
        return this.returnType.equals(new FloatType());
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public LinkedHashMap<String, ParamEntry> getParams() {
        return this.params;
    }

    public int getReturnReg() {
        return this.returnReg;
    }

    public void setReturnReg(int regNum) {
        this.returnReg = regNum;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
