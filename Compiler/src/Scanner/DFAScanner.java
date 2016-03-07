import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 3/6/16.
 */
public class DFAScanner {
    
    Edge.DFA keywordRecognizer;
    Edge.DFA idRecognizer;
    Edge.DFA intRecognizer;
    Edge.DFA floatRecognizer;
    
    String fileData = "";
    
    public DFAScanner(String filename) {
        
        File f = new File(filename);
        
        try {
            String line = "";
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
//                fileData += line + "\n";
                fileData += line + " "; // split each line with a space instead of newline so it's one long character string, much like this comment
            }
            bufferedReader.close();
            fileReader.close();
        } catch(FileNotFoundException fnfe) {
            System.err.println("No file found");
        } catch(IOException ioe) {
            System.err.println("IO exception in Scanner");
        }
        
        fileData = fileData.trim();
        fileData = fileData.replace("\t", " ");
        fileData += "$";
        System.out.println(fileData);
        
        keywordRecognizer = initializeKeywordDFA();
        idRecognizer = initializeIdDFA();
        intRecognizer = initializeIntDFA();
        floatRecognizer = initializeFloatDFA();
    }
    
    public List<Token> scan() {
        List<Token> tokens = new ArrayList<Token>();
        
        if(!fileData.equals("")) {
            
            String lexeme = "";
            
            //Search the file data character-by-character
            //running through each DFA to find a match
            int index = 0;
            boolean done = false;
            while(!done) {
                String character = fileData.charAt(index) + "";
                if(character.equals(" ") || character.equals("$") || character.equals("\t")) {
                    if(keywordRecognizer.isAccepted()) {
                        //add keyword token
                        Token keyword = new Token("Keyword", lexeme);
//                        System.out.println(keyword);
                        tokens.add(keyword);
                        lexeme = reset();
                        if(character.equals("$")) {
                            done = true;
                        } else {
                            index += 1;
                        }
                    } else if(idRecognizer.isAccepted()) {
                        //add ID token
                        Token id = new Token("Id", lexeme);
//                        System.out.println(id);
                        tokens.add(id);
                        lexeme = reset();
                        if(character.equals("$")) {
                            done = true;
                        } else {
                            index += 1;
                        }
                    } else if(intRecognizer.isAccepted()){
                        //add int token
                        Token intlit = new Token("Intlit", lexeme);
//                        System.out.println(intlit);
                        tokens.add(intlit);
                        lexeme = reset();
                        if(character.equals("$")) {
                            done = true;
                        } else {
                            index += 1;
                        }
                    } else if(floatRecognizer.isAccepted()) {
                        //add float token
                        Token token = new Token("Floatlit", lexeme);
//                        System.out.println(token);
                        tokens.add(token);
                        lexeme = reset();
                        
                        if(character.equals("$")) {
                            done = true;
                        } else {
                            index += 1;
                        }
                    } else {
                        //reached end of word with no accepting
                        
//                        lexeme = lexeme.substring(0, lexeme.length()-1);
//
//                        idRecognizer.rollback();
//                        intRecognizer.rollback();
//                        floatRecognizer.rollback();
                        
                        //Remove after here to get back to working:
                        if(lexeme.equals("") || character.equals("\t")) {
                            index += 1;
                        } else {
                            int numRemoved = 0;
                            while (!lexeme.equals("")) {
                                lexeme = lexeme.substring(0, lexeme.length() - 1);
                                numRemoved += 1;

                                keywordRecognizer.rollback();
                                idRecognizer.rollback();
                                intRecognizer.rollback();
                                floatRecognizer.rollback();

                                if (keywordRecognizer.isAccepted()) {
                                    //add keyword token
                                    tokens.add(new Token("Keyword", lexeme));
                                    lexeme = reset();
                                } else if (idRecognizer.isAccepted()) {
                                    //add ID token
                                    tokens.add(new Token("Id", lexeme));
                                    lexeme = reset();
                                } else if (intRecognizer.isAccepted()) {
                                    //add int token
                                    tokens.add(new Token("Intlit", lexeme));
                                    lexeme = reset();
                                } else if (floatRecognizer.isAccepted()) {
                                    //add float token
                                    tokens.add(new Token("Floatlit", lexeme));
                                    lexeme = reset();
                                }
                            }

                            index -= numRemoved;
                        }
                    }
                } else {
                    transition(character);
                    
                    lexeme += character;
                    System.out.println("Current lexeme: " + lexeme);
                    
                    index += 1;
                }
            }
        }
        
        return tokens;
    }
    
    private String reset() {
        floatRecognizer.reset();
        intRecognizer.reset();
        idRecognizer.reset();
        keywordRecognizer.reset();
        return "";
    }

    private void transition(String character) {
        keywordRecognizer.nextChar(character);
        idRecognizer.nextChar(character);
        intRecognizer.nextChar(character);
        floatRecognizer.nextChar(character);
    }

    /**
     * Creates the DFA the scanner uses as a recognizer. The DFA should
     * probable be created before hand and serialized so it can be loaded
     * instead of having to be created.
     * 
     * @return The DFA that recognizes keywords in a Tiger program
     */
    private Edge.DFA initializeKeywordDFA() {
        State initial = new State();
        initial.setInitial();
        
        //a states
        State a0 = new State();
        State a1 = new State();
        State a2 = new State();
        State a3 = new State();
        State array = new State();
        array.setAccepting();
        //a transitions
        initial.addNeighbor("a", a0);
        a0.addNeighbor("r", a1);
        a1.addNeighbor("r", a2);
        a2.addNeighbor("a", a3);
        a3.addNeighbor("y", array);
        
        //b states
        State b0 = new State();
        State b1 = new State();
        State b2 = new State();
        State b3 = new State();
        State begin = new State();
        begin.setAccepting();
        State b4 = new State();
        State b5 = new State();
        State b6 = new State();
        State breakA = new State();
        breakA.setAccepting();
        //b transitions
        initial.addNeighbor("b", b0);
        b0.addNeighbor("e", b1);
        b1.addNeighbor("g", b2);
        b2.addNeighbor("i", b3);
        b3.addNeighbor("n", begin);
        b0.addNeighbor("r", b4);
        b4.addNeighbor("e", b5);
        b5.addNeighbor("a", b6);
        b6.addNeighbor("k", breakA);
        
        //d states
        State d0 = new State();
        State doA = new State();
        doA.setAccepting();
        //d transitions
        initial.addNeighbor("d", d0);
        d0.addNeighbor("o", doA);
        
        //e states
        State e0 = new State();
        State e1 = new State();
        State e2 = new State();
        State elseA = new State();
        elseA.setAccepting();
        State e3 = new State();
        State end = new State();
        end.setAccepting();
        State e4 = new State();
        State enddo = new State();
        enddo.setAccepting();
        State e5 = new State();
        State endif = new State();
        endif.setAccepting();
        //e transitions
        initial.addNeighbor("e", e0);
        e0.addNeighbor("l", e1);
        e1.addNeighbor("s", e2);
        e2.addNeighbor("e", elseA);
        e0.addNeighbor("n", e3);
        e3.addNeighbor("d", end);
        end.addNeighbor("d", e4);
        e4.addNeighbor("o", enddo);
        end.addNeighbor("i", e5);
        e5.addNeighbor("f", endif);
        
        //f states
        State f0 = new State();
        State f1 = new State();
        State forA = new State();
        forA.setAccepting();
        State f2 = new State();
        State f3 = new State();
        State f4 = new State();
        State floatA = new State();
        floatA.setAccepting();
        State f5 = new State();
        State f6 = new State();
        State func = new State();
        func.setAccepting();
        //f transitions
        initial.addNeighbor("f", f0);
        f0.addNeighbor("o", f1);
        f1.addNeighbor("r", forA);
        f0.addNeighbor("u", f5);
        f5.addNeighbor("n", f6);
        f6.addNeighbor("c", func);
        f0.addNeighbor("l", f2);
        f2.addNeighbor("o", f3);
        f3.addNeighbor("a", f4);
        f4.addNeighbor("t", floatA);
        
        //i states
        State i0 = new State();
        State ifA = new State();
        ifA.setAccepting();
        State i1 = new State();
        State intA = new State();
        intA.setAccepting();
        //i transitions
        initial.addNeighbor("i", i0);
        i0.addNeighbor("f", ifA);
        i0.addNeighbor("n", i1);
        i1.addNeighbor("t", intA);
        
        //l states
        State l0 = new State();
        State l1 = new State();
        State let = new State();
        let.setAccepting();
        //l transitions
        initial.addNeighbor("l", l0);
        l0.addNeighbor("e", l1);
        l1.addNeighbor("t", let);
        
        //o states
        State o0 = new State();
        State of = new State();
        of.setAccepting();
        //o transitions
        initial.addNeighbor("o", o0);
        o0.addNeighbor("f", of);
        
        //r states
        State r0 = new State();
        State r1 = new State();
        State r2 = new State();
        State r3 = new State();
        State r4 = new State();
        State returnA = new State();
        returnA.setAccepting();
        //r transitions
        initial.addNeighbor("r", r0);
        r0.addNeighbor("e", r1);
        r1.addNeighbor("t", r2);
        r2.addNeighbor("u", r3);
        r3.addNeighbor("r", r4);
        r4.addNeighbor("n", returnA);
        
        //t states
        State t0 = new State();
        State to = new State();
        to.setAccepting();
        State t1 = new State();
        State t2 = new State();
        State then = new State();
        then.setAccepting();
        State t3 = new State();
        State t4 = new State();
        State type = new State();
        type.setAccepting();
        //t transitions
        initial.addNeighbor("t", t0);
        t0.addNeighbor("o", to);
        t0.addNeighbor("h", t1);
        t1.addNeighbor("e", t2);
        t2.addNeighbor("n", then);
        t0.addNeighbor("y", t3);
        t3.addNeighbor("p", t4);
        t4.addNeighbor("e", type);
        
        //v states
        State v0 = new State();
        State v1 = new State();
        State var = new State();
        var.setAccepting();
        //v transitions
        initial.addNeighbor("v", v0);
        v0.addNeighbor("a", v1);
        v1.addNeighbor("r", var);
        
        //w states
        State w0 = new State();
        State w1 = new State();
        State w2 = new State();
        State w3 = new State();
        State whileA = new State();
        whileA.setAccepting();
        //w transitions
        initial.addNeighbor("w", w0);
        w0.addNeighbor("h", w1);
        w1.addNeighbor("i", w2);
        w2.addNeighbor("l", w3);
        w3.addNeighbor("e", whileA);
        
        //: transitions
        State colon = new State();
        colon.setAccepting();
        State colonequals = new State();
        colonequals.setAccepting();
        //: transitions
        initial.addNeighbor(":", colon);
        colon.addNeighbor("=", colonequals);
        
        //< states
        State lt = new State();
        lt.setAccepting();
        State ltgt = new State();
        ltgt.setAccepting();
        State lteq = new State();
        lteq.setAccepting();
        //< transitions
        initial.addNeighbor("<", lt);
        lt.addNeighbor(">", ltgt);
        lt.addNeighbor("=", lteq);
        
        //> states
        State gt = new State();
        gt.setAccepting();
        State gteq = new State();
        gteq.setAccepting();
        //> transitions
        initial.addNeighbor(">", gt);
        gt.addNeighbor("=", gteq);
        
        //Symbol state
        State symbol = new State();
        symbol.setAccepting();
        //Sybmol transitions
        initial.addNeighbor("=", symbol);
        initial.addNeighbor("/", symbol);
        initial.addNeighbor("*", symbol);
        initial.addNeighbor(".", symbol);
        initial.addNeighbor(",", symbol);
        initial.addNeighbor(";", symbol);
        initial.addNeighbor("(", symbol);
        initial.addNeighbor(")", symbol);
        initial.addNeighbor("[", symbol);
        initial.addNeighbor("]", symbol);
        initial.addNeighbor("{", symbol);
        initial.addNeighbor("}", symbol);
        initial.addNeighbor("+", symbol);
        initial.addNeighbor("-", symbol);
        initial.addNeighbor("&", symbol);
        initial.addNeighbor("|", symbol);

        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(a0);
        states.add(a1);
        states.add(a2);
        states.add(a3);
        states.add(array);
        states.add(b0);
        states.add(b1);
        states.add(b2);
        states.add(b3);
        states.add(begin);
        states.add(b4);
        states.add(b5);
        states.add(b6);
        states.add(breakA);
        states.add(d0);
        states.add(doA);
        states.add(e0);
        states.add(e1);
        states.add(e2);
        states.add(elseA);
        states.add(e3);
        states.add(end);
        states.add(e4);
        states.add(enddo);
        states.add(e5);
        states.add(endif);
        states.add(f0);
        states.add(f1);
        states.add(forA);
        states.add(f2);
        states.add(f3);
        states.add(f4);
        states.add(floatA);
        states.add(f5);
        states.add(f6);
        states.add(func);
        states.add(i0);
        states.add(ifA);
        states.add(i1);
        states.add(intA);
        states.add(l0);
        states.add(l1);
        states.add(let);
        states.add(o0);
        states.add(of);
        states.add(r0);
        states.add(r1);
        states.add(r2);
        states.add(r3);
        states.add(r4);
        states.add(returnA);
        states.add(t0);
        states.add(to);
        states.add(t1);
        states.add(t2);
        states.add(then);
        states.add(t3);
        states.add(t4);
        states.add(type);
        states.add(v0);
        states.add(v1);
        states.add(var);
        states.add(w0);
        states.add(w1);
        states.add(w2);
        states.add(w3);
        states.add(whileA);
        states.add(colon);
        states.add(colonequals);
        states.add(lt);
        states.add(ltgt);
        states.add(lteq);
        states.add(gt);
        states.add(gteq);
        states.add(symbol);
        Edge.DFA recognizer = null;

        try {
            recognizer = new Edge.DFA(states);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return recognizer;
    }
    
    private Edge.DFA initializeIdDFA() {
        State initial = new State();
        initial.setInitial();
        
        State leadingUnderscore = new State();
        
        State accepting = new State();
        accepting.setAccepting();
        
        initial.addNeighbor("_", leadingUnderscore);
        accepting.addNeighbor("_", accepting);
        
        //loop through capital letters, adding transitions
        for(int i=65; i < 91; i++) {
            String s = String.valueOf((char) i);
//            System.out.println("Adding ID transition on " + s);
            initial.addNeighbor(s, accepting);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }
        
        //loop through lowercase letters, adding transitions
        for(int i=97; i < 123; i++) {
            String s = String.valueOf((char) i);
//            System.out.println("Adding ID transition on " + s);
            initial.addNeighbor(s, accepting);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }
        
        //loop through numbers, adding transitions
        for(int i = 0; i < 10; i++) {
            String s = Integer.toString(i);
//            System.out.println("Adding ID transition on " + s);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }
        
        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(leadingUnderscore);
        states.add(accepting);

        Edge.DFA recognizer = null;

        try {
            recognizer = new Edge.DFA(states);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return recognizer;
    }
    
    private Edge.DFA initializeIntDFA() {
        State initial = new State();
        initial.setInitial();
        
        State onlyOtherState = new State();
        onlyOtherState.setAccepting();
        
        for(int i=0; i < 10; i++) {
            initial.addNeighbor(Integer.toString(i), onlyOtherState);
            onlyOtherState.addNeighbor(Integer.toString(i), onlyOtherState);
        }
        
        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(onlyOtherState);

        Edge.DFA recognizer = null;

        try {
            recognizer = new Edge.DFA(states);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return recognizer;
    }
    
    private Edge.DFA initializeFloatDFA() {
        State initial = new State();
        initial.setInitial();
        
        State leadingZero = new State();
        
        State leadingNum = new State();
        
        State decimal = new State();
        decimal.setAccepting();
        
        State trailingNum = new State();
        trailingNum.setAccepting();
        
        initial.addNeighbor("0", leadingZero);
        leadingZero.addNeighbor(".", decimal);
        
        //Add transitions 1-9 from initial to leadingNum
        for(int i=1;i<10;i++) {
            initial.addNeighbor(Integer.toString(i), leadingNum);
        }
        
        //Add transitions 0-9 from leadingNum back into leading num
        //Also add transitions 0-9 from decimal to trailinNum
        for(int i=0; i < 10; i++) {
            leadingNum.addNeighbor(Integer.toString(i), leadingNum);
            decimal.addNeighbor(Integer.toString(i), trailingNum);
            trailingNum.addNeighbor(Integer.toString(i), trailingNum);
        }
        
        leadingNum.addNeighbor(".", decimal);

        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(leadingZero);
        states.add(leadingNum);
        states.add(decimal);
        states.add(trailingNum);

        Edge.DFA recognizer = null;

        try {
            recognizer = new Edge.DFA(states);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return recognizer;
    }
}
