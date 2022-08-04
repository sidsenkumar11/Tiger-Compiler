package tiger.compiler.typechecker.types;

public abstract class Type {
    public abstract boolean canBeAssignedTo(Type other);
}
