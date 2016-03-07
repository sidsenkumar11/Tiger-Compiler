import java.util.LinkedList;

/**
 * Created by Siddarth on 3/7/2016.
 */
public class StackState {

    private LinkedList<Symbol> stack;
    private int tokenNumber;

    public StackState(LinkedList<Symbol> stack, int tokenNumber) {
        this.stack = stack;
        this.tokenNumber = tokenNumber;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public LinkedList<Symbol> getStack() {
        return stack;
    }
}
