
public class TigerProgramListener extends TigerBaseListener {

    @Override
    public void enterProgram(TigerParser.ProgramContext ctx) {
        //System.out.println("Entered Tiger Program");
    }

    @Override
    public void exitProgram(TigerParser.ProgramContext ctx) {
        //System.out.println("Exiteded Tiger Program");
    }

    @Override
    public void enterNumexpr(TigerParser.NumexprContext ctx) {
        //System.out.println("Expr text: " + ctx.getText());
    }
}
