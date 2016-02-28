import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 2/16/16.
 */
public class DFA {
    
    List<State> acceptingStates, states;
    State currentState = null;
    State initialState = null;
    
    public DFA(List<State> states) throws Exception{
        acceptingStates = new ArrayList<State>();
        
        this.states = states;
        
        for(State s : states) {
            if(s.isAccepting())
                acceptingStates.add(s);
            
            if(s.getLabel() == 0) { // s is initial state
                if(initialState != null) {
                    throw new Exception("Only one initial state is allowed");
                }
                
                initialState = s;
            }
        }
        
        if(initialState == null) {
            throw new Exception("Must define one initial state");
        }
        currentState = initialState;
    }
    
    public boolean isAccepted() {
        return currentState.isAccepting();
    }
    
    public void nextChar(String s) {
        currentState = currentState.transition(s);
    }
    
    public void reset() {
        currentState = initialState;
    }
}
