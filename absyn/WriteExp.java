package absyn;

public class WriteExp extends Exp {
  public Exp output;
  public WriteExp( int pos, Exp output ) {
    this.pos = pos;
    this.output = output;
  }
}
