/**
 * Created by Siddarth on 3/2/2016.
 */
public class Token extends Symbol {

    private String type;
    private String content;

    public Token(String type, String content) {
        super(true, type);

        if (type.equalsIgnoreCase("Keyword")) {
            setValue(content);
        }

        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return getValue();
    }
}
