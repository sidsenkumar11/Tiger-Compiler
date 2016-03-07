import java.util.LinkedList;

/**
 * Created by Siddarth on 3/7/2016.
 */
public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;

    public StackState(LinkedList<Symbol> stack, int tokenNumber) {
        this.stack = new LinkedList<>();
        for (int i = stack.size() - 1; i >=0; i--) {
            this.stack.push(stack.get(i));
        }
        this.tokenNumber = tokenNumber;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public LinkedList<Symbol> getStack() {
        return stack;
    }

    @Override
    public String toString() {
        return getStack().toString();
    }
}
