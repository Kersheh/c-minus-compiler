package absyn;

public class ExpConst extends Exp {
  public int val;
  public ExpConst(int pos, int val) {
    this.val = val;
  }
}
