package tiger.compiler.typechecker;

import java.util.LinkedHashMap;
import tiger.compiler.typechecker.types.Type;

public class FunctionTableEntry {
    // Types
    private Type returnType;
    private LinkedHashMap<String, Type> params;

    // Registers
    private RegEntry returnReg;
    private LinkedHashMap<String, RegEntry> paramRegs;

    // Label
    private String label;

    public FunctionTableEntry(Type returnType, LinkedHashMap<String, Type> params) {
        this.returnType = returnType;
        this.params = params;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public LinkedHashMap<String, Type> getParams() {
        return this.params;
    }

    public void setReturnReg(RegEntry returnReg) {
        this.returnReg = returnReg;
    }

    public void setParamRegs(LinkedHashMap<String, RegEntry> paramRegs) {
        this.paramRegs = paramRegs;
    }

    public RegEntry getReturnReg() {
        return this.returnReg;
    }

    public LinkedHashMap<String, RegEntry> getParamRegs() {
        return this.paramRegs;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
