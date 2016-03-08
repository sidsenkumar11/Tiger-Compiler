/**
 * Created by Siddarth on 3/8/2016.
 */
public class ASTSymbol {

    private Symbol symbol;
    private boolean leftParen;
    private boolean rightParen;

    public ASTSymbol(Symbol symbol, boolean leftParen, boolean rightParen) {
        this.symbol = symbol;
        this.leftParen = leftParen;
        this.rightParen = rightParen;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public boolean isLeftParen() {
        return leftParen;
    }

    public boolean isRightParen() {
        return rightParen;
    }

    public void setLeftParen(boolean isLeftParen) {
        this.leftParen = isLeftParen;
    }
    public String toString() {
        String retString = "";
        if (leftParen) {
            retString = "(" + symbol.toString();
        } else if (rightParen) {
            retString = symbol.toString() + ")";
        } else {
            retString = symbol.toString();
        }
        return retString;
    }
}
