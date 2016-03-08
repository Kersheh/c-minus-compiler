package absyn;

public class StmtComp extends Stmt {
  public DeclarLocal declar_local;
  public StmtList stmt_list;
  public StmtComp(int pos, DeclarLocal declar_local, StmtList stmt_list) {
    this.pos = pos;
    this.declar_local = declar_local;
    this.stmt_list = stmt_list;
  }
}
