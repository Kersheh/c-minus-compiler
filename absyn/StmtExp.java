package absyn;

public class StmtExp extends Stmt {
  public Exp exp;
  public StmtExp(int pos, Exp exp) {
    this.pos = pos;
    this.exp = exp;
  }
}
