package absyn;

public class OpExp extends Exp {
  public final static int PLUS  = 0;
  public final static int MINUS = 1;
  public final static int TIMES = 2;
  public final static int OVER  = 3;
  public final static int EQ    = 4;
  public final static int LT    = 5;
  public final static int GT    = 6;

  public Exp left;
  public int op;
  public Exp right;
  public OpExp( int pos, Exp left, int op, Exp right ) {
    this.pos = pos;
    this.left = left;
    this.op = op;
    this.right = right;
  }
}
