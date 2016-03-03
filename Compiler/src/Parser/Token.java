/**
 * Created by Siddarth on 3/2/2016.
 */
public class Token {

    private String type;
    private String content;

    public Token() {

    }

    public Token(String type, String content) {
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
        return content + " is a " + type;
    }
}
