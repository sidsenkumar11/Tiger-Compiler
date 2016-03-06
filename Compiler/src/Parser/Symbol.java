/**
 * Created by Siddarth on 3/3/2016.
 */
public class Symbol {

    private boolean terminal;
    private boolean dollarToken;
    private String value;

    public Symbol(boolean isTerminal, String value) {
        this.terminal = isTerminal;
        this.value = value;
        this.dollarToken = value.equals("$");
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
        return value;
    }
}
