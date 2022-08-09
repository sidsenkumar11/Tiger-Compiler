package tiger.compiler.typechecker.types;

public class ArrayType extends Type {
    private Type subType;
    private int size;

    public ArrayType(Type subType, int size) {
        this.subType = subType;
        this.size = size;
    }

    public Type getSubType() {
        return this.subType;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public boolean canBeAssignedTo(Type other) {
        if (!(other instanceof ArrayType)) {
            return false;
        }

        var otherArray = (ArrayType) (other);
        return otherArray.getSubType().equals(this.subType);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ArrayType)) {
            return false;
        }

        var otherArray = (ArrayType) (o);
        return otherArray.getSubType().equals(this.subType);
    }

    @Override
    public String toString() {
        return "Array[" + this.subType + "]";
    }
}
