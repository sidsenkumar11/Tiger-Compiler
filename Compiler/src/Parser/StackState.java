import java.util.LinkedList;

/**
 * Created by Siddarth on 3/7/2016.
 */
public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;
    private LinkedList<String> astString;
    private int astLocation;

    public StackState(LinkedList<Symbol> stack, int tokenNumber, LinkedList<String> astString, int astLocation) {
        this.stack = new LinkedList<>();
        for (int i = stack.size() - 1; i >=0; i--) {
            this.stack.push(stack.get(i));
        }
        this.tokenNumber = tokenNumber;
        this.astString = astString;
        this.astLocation = astLocation;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public LinkedList<Symbol> getStack() {
        return stack;
    }

    public LinkedList<String> getAST() {
        return astString;
    }

    public int getAstLocation() {
        return astLocation;
    }

    @Override
    public String toString() {
        return getStack().toString();
    }
}
