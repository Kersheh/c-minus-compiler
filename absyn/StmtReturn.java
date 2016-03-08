package absyn;

public class StmtReturn extends Stmt {
  public Exp item;
  public StmtReturn(int pos, Exp item) {
    this.pos = pos;
    this.item = item;
  }
}
