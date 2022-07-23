package tiger.compiler.lexer;

import java.util.HashMap;

public class State {

    public static State ErrorState = new State();
    private HashMap<Character, State> neighbors;
    private boolean isInitial;
    private boolean isAccepting;

    public State() {
        this(false);
    }

    public State(boolean isInitial) {
        this.neighbors = new HashMap<Character, State>();
        this.isAccepting = false;
        this.isInitial = isInitial;
    }

    public boolean isInitial() {
        return this.isInitial;
    }

    public boolean isAccepting() {
        return this.isAccepting;
    }

    public void setAccepting() {
        this.isAccepting = true;
    }

    public void addNeighbor(Character input, State nextState) {
        this.neighbors.put(input, nextState);
    }

    public boolean hasNeighbor(Character input) {
        return this.neighbors.containsKey(input);
    }

    public State getNeighbor(Character input) {
        return this.neighbors.get(input);
    }
}
