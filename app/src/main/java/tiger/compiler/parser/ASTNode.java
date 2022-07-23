package tiger.compiler.parser;

import java.util.LinkedList;

public class ASTNode {

    private static int nodeNum;

    private Symbol symbol;
    private LinkedList<ASTNode> derivation;
    private ASTNode parent;
    private ASTNode root;
    private int currentDeriv;
    private int thisNodeNum;

    public ASTNode(Symbol symbol, ASTNode parent, ASTNode root) {
        this.symbol = symbol;
        this.parent = parent;
        this.root = root;
        this.currentDeriv = 0;
        this.derivation = new LinkedList<>();
        this.thisNodeNum = nodeNum++;
    }

    /**
     * Deep copy constructor
     * 
     * @param symbol The symbol
     * @param derivation The derivation
     * @param currentDeriv The currentDerivation number
     * @param thisNodeNum That node's number
     */
    public ASTNode(Symbol symbol, LinkedList<ASTNode> derivation, int currentDeriv,
            int thisNodeNum) {
        this.symbol = symbol;
        this.derivation = derivation;
        this.currentDeriv = currentDeriv;
        this.thisNodeNum = thisNodeNum;
    }


    public ASTNode getCurrent() {
        return derivation.get(currentDeriv);
    }

    public ASTNode getRoot() {
        return root;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public int getNum() {
        return thisNodeNum;
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

    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public void addFirst(Symbol symbol, ASTNode parent) {
        derivation.addFirst(new ASTNode(symbol, parent, root));
    }

    public void incrementCurrentDeriv() {
        this.currentDeriv++;
    }

    public ASTNode deepCopy() {
        LinkedList<ASTNode> derivations = new LinkedList<>();

        for (int i = 0; i < derivation.size(); i++) {
            derivations.add(derivation.get(i).deepCopy());
        }
        return new ASTNode(symbol, derivations, currentDeriv, thisNodeNum);
    }

    public String toString() {
        return symbol.toString();
    }
}
