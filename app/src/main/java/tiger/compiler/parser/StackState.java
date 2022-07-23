package tiger.compiler.parser;

import java.util.LinkedList;

public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;
    private ASTNode astNode;
    private ASTNode root;

    public StackState(LinkedList<Symbol> stack, int tokenNumber, ASTNode myASTNode,
            ASTNode myRoot) {
        this.stack = new LinkedList<>();
        for (int i = stack.size() - 1; i >= 0; i--) {
            this.stack.push(stack.get(i));
        }
        this.tokenNumber = tokenNumber;
        this.astNode = myASTNode;
        this.root = myRoot;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public LinkedList<Symbol> getStack() {
        return stack;
    }

    public ASTNode getAST() {
        return astNode;
    }

    public ASTNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return getStack().toString();
    }
}
