package tiger.compiler.typechecker.types;

public class FloatType extends Type {

    @Override
    public boolean canBeAssignedTo(Type other) {
        return other.equals(new FloatType());
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof FloatType;
    }

    @Override
    public String toString() {
        return "Float";
    }
}
