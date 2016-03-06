package absyn;

public class VarExp extends Exp {
  public String name;
  public VarExp( int pos, String name ) {
    this.pos = pos;
    this.name = name;
  }
}
