import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2/16/16.
 */
public class State {
    
    int label;
    static int labelCounter = 0;
    
    boolean accepting = false;
    
//    List<Edge> neighbors;
    
    HashMap<String, State> neighbors;

    public State() {
        neighbors = new HashMap<String, State>();
        label = labelCounter;
        labelCounter++;
    }
    
    public void addNeighbor(String input, State nextState) {
        Edge newEdge = new Edge(input, nextState);
        neighbors.put(input, nextState);
    }

    public State transition(String input) {
        State nextState = neighbors.get(input);
        
        if(nextState == null) {
            nextState = new State();
            nextState.setError();
        }
        
        return nextState;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    public int getLabel() {
        return label;
    }

    public void setInitial() {
        label = 0;
    }
    
    public void setError() {
        label = -1;
    }
}
