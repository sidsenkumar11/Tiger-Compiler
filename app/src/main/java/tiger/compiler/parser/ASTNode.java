package tiger.compiler.parser;

import java.util.LinkedList;
import java.util.List;
import tiger.compiler.lexer.TokenType;

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

    public ASTNode getFirst() {
        return this.derivation.get(0);
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

    public String getAST() {
        return this.inOrder(this).toString();
    }

    private StringBuilder inOrder(ASTNode current) {
        var currSymbol = current.getSymbol();
        if (currSymbol.isTerminal()) {
            if (currSymbol == Symbol.EPSILON) {
                return new StringBuilder();
            } else if (currSymbol.getTerminalType() == TokenType.KEYWORD) {
                return new StringBuilder(current.getSymbol().toString());
            } else {
                return new StringBuilder(current.getValue());
            }
        }

        var children = current.getDerivation();
        StringBuilder x = new StringBuilder();

        var onlyChildIsEpsilon =
                children.size() == 1 && children.get(0).getSymbol() == Symbol.EPSILON;

        if (onlyChildIsEpsilon) {
            x.append(current.toString());
        } else {
            x.append("(");
            x.append(current.toString());
            x.append(" ");
        }

        for (var i = 0; i < children.size() - 1; i++) {
            if (children.get(i).getSymbol() != Symbol.EPSILON) {
                x.append(this.inOrder(children.get(i)));
                x.append(" ");
            }
        }

        var lastChild = children.get(children.size() - 1);
        if (lastChild.getSymbol() != Symbol.EPSILON) {
            x.append(this.inOrder(children.get(children.size() - 1)));
        }

        if (!onlyChildIsEpsilon) {
            x.append(")");
        }
        return x;
    }
}
