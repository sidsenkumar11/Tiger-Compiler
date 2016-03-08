import java.util.LinkedList;

/**
 * Created by Siddarth on 3/7/2016.
 */
public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;
    private LinkedList<ASTSymbol> astStack;
    private int astStackTop;

    public StackState(LinkedList<Symbol> stack, int tokenNumber, LinkedList<ASTSymbol> astStack, int stackTop) {
        this.stack = new LinkedList<>();
        for (int i = stack.size() - 1; i >=0; i--) {
            this.stack.push(stack.get(i));
        }
        this.tokenNumber = tokenNumber;
        this.astStack = astStack;
        this.astStackTop = stackTop;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public LinkedList<Symbol> getStack() {
        return stack;
    }

    public LinkedList<ASTSymbol> getAST() {
        return astStack;
    }

    public int getAstStackTop() {
        return astStackTop;
    }

    @Override
    public String toString() {
        return getStack().toString();
    }
}
