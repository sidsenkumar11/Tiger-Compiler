package tiger.compiler.intermediatecode;

public class Register {
    private int number;
    private int value;

    public Register(int number, int value) {
        this.number = number;
        this.value = value;
    }

    public int getNumber() {
        return this.number;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
