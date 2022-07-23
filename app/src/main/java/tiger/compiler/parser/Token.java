package tiger.compiler.parser;

public class Token extends Symbol {

    public static Token EOFToken = new Token("$", "$");
    private String type;
    private String content;

    // TODO: Make Type into enum
    public Token(String type, String content) {
        super(true, type);

        if (type.equalsIgnoreCase("Keyword")) {
            super.setValue(content);
        }

        this.type = type;
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public String toString() {
        return this.content;
    }
}
