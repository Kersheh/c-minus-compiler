package absyn;

public class RepeatExp extends Exp {
  public ExpList exps;
  public Exp test;
  public RepeatExp( int pos, ExpList exps, Exp test ) {
    this.pos = pos;
    this.exps = exps;
    this.test = test;
  }
}
