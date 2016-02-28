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
}
