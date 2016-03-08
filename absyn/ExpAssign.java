package absyn;

public class ExpAssign extends Exp {
  public ExpVar lhs;
  public Exp rhs;
  public ExpAssign(int pos, ExpVar lhs, Exp rhs) {
    this.pos = pos;
    this.lhs = lhs;
    this.rhs = rhs;
  }
}
