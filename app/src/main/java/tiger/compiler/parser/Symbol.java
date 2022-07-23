package tiger.compiler.parser;

public class Symbol {

    private boolean isTerminal;
    private boolean isEpsilon;
    private boolean isDollarToken;
    private boolean isSpecial;
    private String value;

    public Symbol(boolean isTerminal, String value) {
        this.value = value;
        this.isTerminal = isTerminal;
        this.isEpsilon = value.equals("''");
        this.isDollarToken = value.equals("$");

        this.isSpecial = (value.equals("ids'") || value.equals("neparams'")
                || value.equals("neexprs'") || value.equals("boolexpr'") || value.equals("clause'")
                || value.equals("numexpr'") || value.equals("term'") || value.equals("factor'"));
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }

    public boolean isNonterminal() {
        return !this.isTerminal;
    }

    public boolean isEpsilon() {
        return this.isEpsilon;
    }

    public boolean isDollarToken() {
        return this.isDollarToken;
    }

    public boolean isSpecial() {
        return this.isSpecial;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        return this.value.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return this.value;
    }
}
