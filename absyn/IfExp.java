package absyn;

public class IfExp extends Exp {
  public Exp test;
  public ExpList thenpart;
  public ExpList elsepart;
  public IfExp( int pos, Exp test, ExpList thenpart, ExpList elsepart ) {
    this.pos = pos;
    this.test = test;
    this.thenpart = thenpart;
    this.elsepart = elsepart;
  }
}
