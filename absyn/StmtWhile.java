package absyn;

public class StmtWhile extends Stmt {
  public Exp test;
  public StmtList exps;
  public StmtWhile(int pos, Exp test, StmtList exps) {
    this.pos = pos;
    this.test = test;
    this.exps = exps;
  }
}
