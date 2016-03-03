/**
 * Created by Siddarth on 3/3/2016.
 */
public class ParseTable {

    static Symbol[] [][] parseTable;

    public ParseTable() {
        parseTable = new Symbol[0] [0][0];
    }

    /**
     * Tells whether there is a definition for this stack value on this input token.
     * @param stackSymbol The current stack symbol.
     * @param nextTokenSymbol The next input token.
     * @return If there is a defintion for this stack value on this input token.
     */
    public boolean isEmpty(Symbol stackSymbol, Symbol nextTokenSymbol) {
        return parseTable[stackSymbol.hashCode()][nextTokenSymbol.hashCode()].length == 0;
    }

    /**
     * Gets the expanded symbol list for this stack value on this input token.
     * @param stackSymbol The current stack symbol.
     * @param nextTokenSymbol The next input token.
     * @return The expanded symbol list for this input token on this stack value.
     */
    public Symbol[] get(Symbol stackSymbol, Symbol nextTokenSymbol) {
        return parseTable[stackSymbol.hashCode()][nextTokenSymbol.hashCode()];
    }
}
