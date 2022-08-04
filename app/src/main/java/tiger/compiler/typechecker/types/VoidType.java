package tiger.compiler.typechecker.types;

public class VoidType extends Type {

    @Override
    public boolean canBeAssignedTo(Type other) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof VoidType;
    }

    @Override
    public String toString() {
        return "Void";
    }
}
