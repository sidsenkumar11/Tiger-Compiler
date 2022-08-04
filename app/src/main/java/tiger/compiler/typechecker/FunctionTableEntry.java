package tiger.compiler.typechecker;

import java.util.LinkedHashMap;
import tiger.compiler.typechecker.types.Type;

public class FunctionTableEntry {

    private Type returnType;
    private LinkedHashMap<String, Type> args;

    public FunctionTableEntry(Type returnType, LinkedHashMap<String, Type> args) {
        this.returnType = returnType;
        this.args = args;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public LinkedHashMap<String, Type> getArgs() {
        return this.args;
    }
}
