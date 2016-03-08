package absyn;

public class StmtSelect extends Stmt {
  public Exp exp;
  public Stmt then_stmt;
  public Stmt else_stmt;
  public StmtSelect(int pos, Exp exp, Stmt then_stmt, Stmt else_stmt) {
    this.pos = pos;
    this.exp = exp;
    this.then_stmt = then_stmt;
    this.else_stmt = else_stmt;
  }
}
