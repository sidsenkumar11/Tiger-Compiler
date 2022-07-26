package tiger.compiler.parser;

import java.util.LinkedList;
import java.util.List;

public class ASTNode {
    private List<ASTNode> derivation;
    private Symbol symbol;
    private String value;

    public ASTNode(Symbol symbol) {
        this.derivation = new LinkedList<ASTNode>();
        this.symbol = symbol;
        this.value = "";
    }

    public List<ASTNode> getDerivation() {
        return this.derivation;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public String getValue() {
        return this.value;
    }

    public void mergeLastNodeChildren() {
        var lastNode = this.removeLast();
        for (var childNode : lastNode.getDerivation()) {
            if (childNode.getSymbol() != Symbol.EPSILON) {
                this.derivation.add(childNode);
            }
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ASTNode addChild(Symbol symbol) {
        var childNode = new ASTNode(symbol);
        this.derivation.add(childNode);
        return childNode;
    }

    public void addChild(ASTNode childNode) {
        this.derivation.add(childNode);
    }

    public ASTNode getLast() {
        return this.derivation.get(this.derivation.size() - 1);
    }

    public ASTNode removeLast() {
        return this.derivation.remove(this.derivation.size() - 1);
    }

    public int childCount() {
        return this.derivation.size();
    }

    public String toString() {
        return (this.value.equals("")) ? this.symbol.toString() : this.value;
    }
}
