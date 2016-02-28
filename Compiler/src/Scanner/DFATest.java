import java.util.*;

/**
 * Created by nick on 2/16/16.
 */
public class DFATest {
    
    public static void main(String[] args) {
        State s0 = new State();
        s0.setInitial();
        
        State s1 = new State();
        
        State s2 = new State();
        
        State s3 = new State();
        s3.setAccepting(true);
        
        s0.addNeighbor("n", s1);
        s1.addNeighbor("e", s2);
        s2.addNeighbor("w", s3);

        List<State> states = new ArrayList<State>();
        states.add(s0);
        states.add(s1);
        states.add(s2);
        states.add(s3);
        
        DFA recognizer = null;
        
        try {
            recognizer = new DFA(states);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        if(recognizer != null) {
            String input = "new";
            
            if(matchString(recognizer, input)) {
                System.out.println("Handled the good string properly");
            } else {
                System.out.println("Handled the good string improperly");
            }
            
            recognizer.reset();
            
            String badInput = "naw";
            
            if(!matchString(recognizer, badInput)) {
                System.out.println("Handled the bad string properly!");
            } else {
                System.out.println("Handled the bad string improperly");
            }
            
            recognizer.reset();
            
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            System.out.print("Enter a string: ");
            String userInput = scanner.nextLine();
            while(!userInput.equals("EXIT")) {
                if(matchString(recognizer, userInput)) {
                    System.out.println("Matched the string");
                } else {
                    System.out.println("Didn't match the string");
                }
                recognizer.reset();

                System.out.print("Enter a string: ");
                userInput = scanner.nextLine();
            }
        }
    }
    
    public static boolean matchString(DFA recognizer, String input) {
        for(int i = 0; i < input.length(); i++) {
            recognizer.nextChar(input.charAt(i) + "");
        }
        return recognizer.isAccepted();
    }
}
