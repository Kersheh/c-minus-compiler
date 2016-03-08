package absyn;

public class ExpWhile extends Exp {
  public Exp test;
  public ExpList exps;
  public ExpWhile(int pos, ExpList exps, Exp test) {
    this.pos = pos;
    this.test = test;
    this.exps = exps;
  }
}
