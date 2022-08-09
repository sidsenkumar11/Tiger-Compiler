package tiger.compiler.typechecker;

public class RegEntry {
    private int regNum;
    private boolean isFloat;

    public RegEntry(int regNum, boolean isFloat) {
        this.regNum = regNum;
        this.isFloat = isFloat;
    }

    public int regNum() {
        return this.regNum;
    }

    public boolean isFloat() {
        return this.isFloat;
    }
}
