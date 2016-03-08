package absyn;

public class DeclarFun extends Declar {
  public TypeSpec type;
  public String name;
  public Params params;
  public StmtComp stmt;
  public DeclarFun(int pos, TypeSpec type, String name, Params params, StmtComp stmt) {
    this.pos = pos;
    this.type = type;
    this.name = name;
    this.params = params;
    this.stmt = stmt;
  }
}
