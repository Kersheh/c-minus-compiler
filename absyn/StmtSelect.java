package absyn;

public class StmtSelect extends Stmt {
  public Exp test;
  public Stmt then_stmt;
  public Stmt else_stmt;
  public StmtSelect(int pos, Exp test, Stmt then_stmt, Stmt else_stmt) {
    this.pos = pos;
    this.test = test;
    this.then_stmt = then_stmt;
    this.else_stmt = else_stmt;
  }
}
