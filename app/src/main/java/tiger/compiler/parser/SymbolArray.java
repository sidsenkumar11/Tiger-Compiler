package tiger.compiler.parser;

public class SymbolArray {

    private Symbol[] symbols;

    public SymbolArray(Symbol... symbols) {
        this.symbols = symbols;
    }

    public Symbol[] getSymbols() {
        return symbols;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        for (int i = 0; i < symbols.length; i++) {
            hash = 31 * hash + symbols[i].hashCode();
        }
        return hash;
    }

}
