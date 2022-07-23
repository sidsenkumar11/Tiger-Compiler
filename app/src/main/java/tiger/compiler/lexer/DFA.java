package tiger.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFA {

    private List<State> acceptingStates;
    private Stack<State> previousStates;
    private State currentState;
    private State initialState;

    public DFA(List<State> states) throws LexException {
        this.acceptingStates = new ArrayList<State>();

        for (State s : states) {
            if (s.isAccepting()) {
                this.acceptingStates.add(s);
            }

            if (s.isInitial()) {
                if (this.initialState != null) {
                    throw new LexException("Only one initial state is allowed");
                }

                this.initialState = s;
            }
        }

        if (initialState == null) {
            throw new LexException("Must define one initial state");
        }

        this.currentState = this.initialState;
        this.previousStates = new Stack<State>();
    }

    public void rollback() {
        if (this.previousStates.isEmpty()) {
            this.currentState = this.initialState;
        } else {
            this.currentState = this.previousStates.pop();
        }
    }

    public boolean isAccepted() {
        return this.currentState.isAccepting();
    }

    public void transition(Character s) {
        this.previousStates.push(this.currentState);
        var nextState = this.currentState.getNeighbor(s);
        if (nextState == null) {
            nextState = State.ErrorState;
        }

        this.currentState = nextState;
    }

    public void reset() {
        this.currentState = this.initialState;
        this.previousStates = new Stack<State>();
    }
}
