package absyn;

public class StmtWhile extends Stmt {
  public Exp test;
  public Stmt stmt;
  public StmtWhile(int pos, Exp test, Stmt stmt) {
    this.pos = pos;
    this.test = test;
    this.stmt = stmt;
  }
}
