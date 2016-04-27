/**
 * Created by Siddarth on 3/28/2016.
 */
public class SymbolTableEntry {

    private String scope, name, type, attr;
    private int index;
    private int reg_no;

    public SymbolTableEntry(String scope, String name, String type, String attr, int reg_no) {
        this.scope = scope;
        this.name = name;
        this.type = type;
        this.attr = attr;
        this.reg_no = reg_no;
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

    public int reg_no() {
        return reg_no;
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

    public void setReg_no(int reg_no) {
        this.reg_no = reg_no;
    }
}
