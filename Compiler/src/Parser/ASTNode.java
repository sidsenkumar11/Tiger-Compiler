import sun.awt.image.ImageWatched;

import java.util.LinkedList;

/**
 * Created by Siddarth on 3/8/2016.
 */
public class ASTNode {

    private Symbol symbol;
    private LinkedList<ASTNode> derivation;
    private ASTNode parent;
    private int currentDeriv;

    public ASTNode(Symbol symbol, ASTNode parent) {
        this.symbol = symbol;
        this.parent = parent;
        this.currentDeriv = 0;
        this.derivation = new LinkedList<>();
    }

    public ASTNode(Symbol symbol, LinkedList<ASTNode> derivation, ASTNode parent, int currentDeriv) {
        this.symbol = symbol;
        this.derivation = derivation;
        this.parent = parent;
        this.currentDeriv = currentDeriv;
    }

    public ASTNode getCurrent() {
        return derivation.get(currentDeriv);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public ASTNode getParent() {
        return parent;
    }

    public int getCurrentDeriv() {
        return currentDeriv;
    }

    public LinkedList<ASTNode> getDerivation() {
        return derivation;
    }
    public String getSymbolValue() {
        return symbol.toString();
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void addFirst(Symbol symbol, ASTNode parent) {
        derivation.addFirst(new ASTNode(symbol, parent));
    }

    public void incrementCurrentDeriv() {
        this.currentDeriv++;
    }

    public ASTNode deepCopy() {
        LinkedList<ASTNode> derivations = new LinkedList<>();

        for (int i = 0; i < derivation.size(); i++) {
            derivations.add(derivation.get(i).deepCopy());
        }
        return new ASTNode(symbol, derivations, parent, currentDeriv);
    }

    public String toString() {
        return symbol.toString();
    }
}
