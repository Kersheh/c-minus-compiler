package absyn;

public class StmtComp extends Stmt {
  public DeclarListLocal declar_local;
  public StmtList stmt_list;
  public StmtComp(int pos, DeclarListLocal declar_local, StmtList stmt_list) {
    this.pos = pos;
    this.declar_local = declar_local;
    this.stmt_list = stmt_list;
  }
}
