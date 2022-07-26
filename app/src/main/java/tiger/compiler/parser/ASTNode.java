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

    public void setValue(String value) {
        this.value = value;
    }

    public ASTNode addChild(Symbol symbol) {
        var childNode = new ASTNode(symbol);
        this.derivation.add(childNode);
        return childNode;
    }

    public String toString() {
        return (this.value.equals("")) ? this.symbol.toString() : this.value;
    }
}
