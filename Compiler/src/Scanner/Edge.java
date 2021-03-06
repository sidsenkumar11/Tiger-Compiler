import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by nick on 2/16/16.
 */
public class Edge {
    
    String character;
    State nextState;

    public Edge(String character, State nextState) {
        this.character = character;
        this.nextState = nextState;
    }

    /**
     * Created by nick on 2/16/16.
     */
    public static class DFA {

        List<State> acceptingStates, states;
        Stack<State> previousStates;
        State currentState = null;
        State initialState = null;

        public DFA(List<State> states) throws ScanException{
            acceptingStates = new ArrayList<State>();

            this.states = states;

            for(State s : states) {
                if(s.isAccepting())
                    acceptingStates.add(s);

                if(s.getLabel() == 0) { // s is initial state
                    if(initialState != null) {
                        throw new ScanException("Only one initial state is allowed");
                    }

                    initialState = s;
                }
            }

            if(initialState == null) {
                throw new ScanException("Must define one initial state");
            }
            currentState = initialState;
            
            previousStates = new Stack<State>();
        }
        
        public void rollback() {
            if(previousStates.isEmpty()) {
                currentState = initialState;
            } else {
                currentState = previousStates.pop();
            }
        }

        public boolean isAccepted() {
            return currentState.isAccepting();
        }

        public void nextChar(String s) {
            previousStates.push(currentState);
            currentState = currentState.transition(s);
        }

        public void reset() {
            currentState = initialState;
        }
    }
}
