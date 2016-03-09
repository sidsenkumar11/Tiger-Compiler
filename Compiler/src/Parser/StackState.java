import java.util.LinkedList;

/**
 * Created by Siddarth on 3/7/2016.
 */
public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;
    private ASTNode astNode;
    private int astStackTop;

    public StackState(LinkedList<Symbol> stack, int tokenNumber, ASTNode myASTNode, int stackTop) {
        this.stack = new LinkedList<>();
        for (int i = stack.size() - 1; i >=0; i--) {
            this.stack.push(stack.get(i));
        }
        this.tokenNumber = tokenNumber;
        this.astNode = myASTNode.deepCopy();
        this.astStackTop = stackTop;
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

    public int getAstStackTop() {
        return astStackTop;
    }

    @Override
    public String toString() {
        return getStack().toString();
    }
}
