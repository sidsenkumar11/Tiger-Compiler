package tiger.compiler.parser;

public class Symbol {
    private SymbolType type;

    public Symbol(SymbolType type) {
        this.type = type;
    }

    public SymbolType getType() {
        return this.type;
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Symbol)) {
            return false;
        }

        return this.type == ((Symbol) o).type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
