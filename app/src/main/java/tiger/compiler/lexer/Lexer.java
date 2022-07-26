package tiger.compiler.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private DFA keywordRecognizer;
    private DFA idRecognizer;
    private DFA intRecognizer;
    private DFA floatRecognizer;

    public Lexer() {
        this.keywordRecognizer = CreateKeywordDFA();
        this.idRecognizer = CreateIdDFA();
        this.intRecognizer = CreateIntDFA();
        this.floatRecognizer = CreateFloatDFA();
    }

    private void reset() {
        this.keywordRecognizer.reset();
        this.idRecognizer.reset();
        this.intRecognizer.reset();
        this.floatRecognizer.reset();
    }

    private void transition(Character character) {
        this.keywordRecognizer.transition(character);
        this.idRecognizer.transition(character);
        this.intRecognizer.transition(character);
        this.floatRecognizer.transition(character);
    }

    private void rollback() {
        this.keywordRecognizer.rollback();
        this.idRecognizer.rollback();
        this.intRecognizer.rollback();
        this.floatRecognizer.rollback();
    }

    private StringBuilder addToken(List<Token> tokens, String lexeme, TokenType type) {
        this.reset();
        tokens.add(new Token(lexeme, type));
        return new StringBuilder();
    }

    public List<Token> scan(String fileData) {

        List<Token> tokens = new ArrayList<Token>();
        var lexeme = new StringBuilder();
        var lineNumber = 1;
        var index = 0;
        fileData = fileData.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");

        // Reset DFA states
        this.reset();

        // Run through each DFA to find matches in the stream
        while (index < fileData.length()) {

            var curChar = fileData.charAt(index++);

            // Append to current lexeme if non-whitespace character
            if (curChar != ' ' && curChar != '\t' && curChar != '\n') {
                this.transition(curChar);
                lexeme.append(curChar);
                continue;
            }

            // Whitespace character indicates end of lexeme
            if (curChar == '\n') {
                lineNumber++;
            }

            // If the lexeme is empty, this is a whitespace character between lexemes
            if (lexeme.length() == 0) {
                continue;
            }

            if (this.keywordRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.KEYWORD);
            } else if (this.idRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.ID);
            } else if (this.intRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.INTLIT);
            } else if (this.floatRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.FLOATLIT);
            } else {
                // Reached end of word with no accepting states
                // Need to backtrack until one of the DFAs indicates an accepting state
                var oldLexeme = lexeme.toString();
                var accepted = false;
                index--; // Index was already incremented to go to the next character
                if (curChar == '\n') {
                    lineNumber--;
                }

                while (!lexeme.isEmpty()) {

                    // Remove the last character
                    lexeme = new StringBuilder(lexeme.subSequence(0, lexeme.length() - 1));
                    index--;
                    this.rollback();

                    // Check if any DFAs accepted the token
                    if (this.keywordRecognizer.isAccepted()) {
                        lexeme = this.addToken(tokens, lexeme.toString(), TokenType.KEYWORD);
                        accepted = true;
                    } else if (this.idRecognizer.isAccepted()) {
                        lexeme = this.addToken(tokens, lexeme.toString(), TokenType.ID);
                        accepted = true;
                    } else if (this.intRecognizer.isAccepted()) {
                        lexeme = this.addToken(tokens, lexeme.toString(), TokenType.INTLIT);
                        accepted = true;
                    } else if (this.floatRecognizer.isAccepted()) {
                        lexeme = this.addToken(tokens, lexeme.toString(), TokenType.FLOATLIT);
                        accepted = true;
                    }
                }

                // Check if rolling back didn't find anything
                if (!accepted) {
                    System.err.println(
                            "Error at line " + lineNumber + ". Couldn't recognize '" + oldLexeme
                                    + "'");
                    tokens.add(Token.ErrorToken);
                    return tokens;
                }
            }
        }

        // In case file doesn't end in newline, check if any DFAs accepted current token
        if (lexeme.length() > 0) {
            if (this.keywordRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.KEYWORD);
            } else if (this.idRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.ID);
            } else if (this.intRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.INTLIT);
            } else if (this.floatRecognizer.isAccepted()) {
                lexeme = this.addToken(tokens, lexeme.toString(), TokenType.FLOATLIT);
            } else {
                System.err.println(
                        "Error at line " + lineNumber + ". Couldn't recognize '" + lexeme
                                + "'");
                tokens.add(Token.ErrorToken);
                return tokens;
            }
        }

        tokens.add(Token.EOFToken);
        return tokens;
    }

    /**
     * Constructs a DFA that matches keywords in Tiger. This includes: array, begin, break, do,
     * else, end, enddo, endif, float, for, func, if, in, int, let, of, return, then, to, type, var,
     * while, ,, :, ;, (, ), [, ], {, }, ., +, -, *, /, =, <>, <, >, <=, >=, &, |, and :=.
     *
     * @return A DFA that matches keywords.
     */
    private static DFA CreateKeywordDFA() {

        List<State> states = new ArrayList<State>();
        var initial = new State(true);
        states.add(initial);

        String[] keywords = {"array", "begin", "break", "do", "else", "end", "enddo", "endif",
                "float", "for", "func", "if", "in", "int", "let", "of", "return", "then", "to",
                "type", "var", "while", ",", ":", ";", "(", ")", "[", "]", "{", "}", ".", "+", "-",
                "*", "/", "=", "<>", "<", ">", "<=", ">=", "&", "|", ":="};

        for (var keyword : keywords) {

            // Match as much of the keyword as possible using existing states
            var current = initial;
            while (keyword.length() > 0) {
                var firstChar = keyword.charAt(0);
                if (!current.hasNeighbor(firstChar)) {
                    break;
                }

                current = current.getNeighbor(firstChar);
                keyword = keyword.substring(1);
            }

            // Create new states for the rest of the keyword
            while (keyword.length() > 0) {
                var nextState = new State();
                states.add(nextState);

                current.addNeighbor(keyword.charAt(0), nextState);
                current = nextState;
                keyword = keyword.substring(1);
            }

            current.setAccepting();
        }

        try {
            return new DFA(states);
        } catch (LexException e) {
            return null;
        }
    }

    /**
     * Constructs a DFA that matches program identifiers in Tiger. A program identifier is a
     * sequence of letters, numbers, and the underscore character. An identifier must begin with
     * either a letter or underscore, and must contain at least one letter or number.
     *
     * @return A DFA that matches program identifiers.
     */
    private static DFA CreateIdDFA() {

        // Regex: (_|[A-Z]|[a-z])([A-Z]|[a-z]|[0-9]|_)*

        // Create states
        var initial = new State(true);
        var leadingUnderscore = new State();
        var accepting = new State();
        accepting.setAccepting();

        // An identifier may contain an underscore,
        // but there must be other alphanumeric characters in the id.
        initial.addNeighbor('_', leadingUnderscore);
        accepting.addNeighbor('_', accepting);

        // Add capital letter transitions
        for (int i = 65; i < 91; i++) {
            char s = (char) i;
            initial.addNeighbor(s, accepting);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }

        // Add lowercase letter transitions
        for (int i = 97; i < 123; i++) {
            char s = (char) i;
            initial.addNeighbor(s, accepting);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }

        // Add integer transitions
        // An identifier cannot begin with an integer
        for (int i = 0; i < 10; i++) {
            char s = (char) ('0' + i);
            leadingUnderscore.addNeighbor(s, accepting);
            accepting.addNeighbor(s, accepting);
        }

        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(leadingUnderscore);
        states.add(accepting);
        try {
            return new DFA(states);
        } catch (LexException e) {
            return null;
        }
    }

    /**
     * Constructs a DFA that matches integer literals in Tiger. An integer literal is a non-empty
     * sequence of digits.
     *
     * @return A DFA that matches integer literals.
     */
    private static DFA CreateIntDFA() {

        // Regex: [0-9]+

        // Create states
        var initial = new State(true);
        var accepting = new State();
        accepting.setAccepting();

        // Integer is any non-empty sequence of digits
        for (int i = 0; i < 10; i++) {
            initial.addNeighbor((char) ('0' + i), accepting);
            accepting.addNeighbor((char) ('0' + i), accepting);
        }

        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(accepting);
        try {
            return new DFA(states);
        } catch (LexException e) {
            return null;
        }
    }

    /**
     * Constructs a DFA that matches floating-point literals in Tiger. A floating-point literal must
     * consist of a non-empty sequence of digits, a radix ('.'), and a (possibly empty) sequence of
     * digits. The literal cannot contain any leading zeroes not required to ensure that the
     * sequence of digits before the radix is non-empty. e.g., 0.123 is a float literal, but 00.123
     * is not.
     *
     * @return A DFA that matches floating-point literals.
     */
    private static DFA CreateFloatDFA() {

        // Regex: ((0\.)|([1-9][0-9]*\.))([0-9])*

        // Create states
        var initial = new State(true);
        var leadingZero = new State();
        var leadingNum = new State();
        var decimal = new State();
        var trailingNum = new State();

        decimal.setAccepting();
        trailingNum.setAccepting();

        // Initial can go to leadingZero or leadingNum
        initial.addNeighbor('0', leadingZero);
        for (int i = 1; i < 10; i++) {
            initial.addNeighbor((char) ('0' + i), leadingNum);
        }

        // leadingZero must be followed by decimal
        leadingZero.addNeighbor('.', decimal);

        // leadingNum could be followd by decimal or more leadingNums
        leadingNum.addNeighbor('.', decimal);
        for (int i = 0; i < 10; i++) {
            leadingNum.addNeighbor((char) ('0' + i), leadingNum);
        }

        // decimal could be followed by trailingNums
        for (int i = 0; i < 10; i++) {
            decimal.addNeighbor((char) ('0' + i), trailingNum);
            trailingNum.addNeighbor((char) ('0' + i), trailingNum);
        }

        List<State> states = new ArrayList<State>();
        states.add(initial);
        states.add(leadingZero);
        states.add(leadingNum);
        states.add(decimal);
        states.add(trailingNum);
        try {
            return new DFA(states);
        } catch (LexException e) {
            return null;
        }
    }
}
