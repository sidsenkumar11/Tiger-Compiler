package tiger.compiler.typechecker.types;

public class IntType extends Type {

    @Override
    public boolean canBeAssignedTo(Type other) {
        return other.equals(new IntType()) || other.equals(new FloatType());
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof IntType;
    }

    @Override
    public String toString() {
        return "Int";
    }
}
