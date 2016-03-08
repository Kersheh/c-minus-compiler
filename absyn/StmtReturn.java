package absyn;

public class StmtReturn extends Stmt {
  public ExpVar item;
  public StmtReturn(int pos, ExpVar item) {
    this.pos = pos;
    this.item = item;
  }
}
