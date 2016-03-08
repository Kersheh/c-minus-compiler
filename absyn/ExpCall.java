package absyn;

public class ExpCall extends Exp {
  public String id;
  public ExpList args;
  public ExpCall(int pos, String id, ExpList args) {
    this.pos = pos;
    this.id = id;
    this.args = args;
  }
}
