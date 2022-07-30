package tiger.compiler.typechecker;

public class TypeTableEntry {

    private String name, type, attr;
    private int index;

    public TypeTableEntry(String name, String type, String attr, int index) {
        this.scope = scope;
        this.name = name;
        this.type = type;
        this.attr = attr;
        this.index = index;
    }

    public String scope() {
        return scope;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public String attr() {
        return attr;
    }



    public int index() {
        return index;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
