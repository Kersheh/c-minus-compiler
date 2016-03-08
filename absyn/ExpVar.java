package absyn;

public class ExpVar extends Exp {
  public String name;
  public Exp exp;
  public ExpVar(int pos, String name, Exp exp) {
    this.pos = pos;
    this.name = name;
    this.exp = exp;
  }
}
