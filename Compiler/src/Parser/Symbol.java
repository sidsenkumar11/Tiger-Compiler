/**
 * Created by Siddarth on 3/3/2016.
 */
public class Symbol {

    private boolean terminal;
    private boolean isEpsilon;
    private boolean dollarToken;
    private String value;

    public Symbol(boolean isTerminal, String value) {
        this.terminal = isTerminal;
        this.value = value.toLowerCase();
        this.dollarToken = value.equals("$");
        this.isEpsilon = value.equals("''");
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
