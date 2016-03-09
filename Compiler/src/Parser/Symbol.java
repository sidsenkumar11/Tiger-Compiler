/**
 * Created by Siddarth on 3/3/2016.
 */
public class Symbol {

    private boolean terminal;
    private boolean isEpsilon;
    private boolean dollarToken;
    private boolean isSpecial;
    private String value;

    public Symbol(boolean isTerminal, String value) {
        this.terminal = isTerminal;
        this.value = value.toLowerCase();
        this.dollarToken = value.equals("$");
        this.isEpsilon = value.equals("''");

        this.isSpecial = (value.equals("ids'") || value.equals("neparams'") || value.equals("neexprs'") || value.equals("boolexpr'") || value.equals("clause'") || value.equals("numexpr'") || value.equals("term'") || value.equals("factor'"));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public boolean isNonterminal() {
        return !terminal;
    }

    public boolean isDollarToken() {
        return dollarToken;
    }

    public boolean isEpsilon() { return isEpsilon; }

    public boolean isPotentialSpecialParentOfEpsilon() {
        return this.isSpecial;
    }
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return value.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        if (value.equals("?")) {
            return ",";
        } else {
            return value;
        }
    }
}
