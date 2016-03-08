package absyn;

public class DeclarLocal extends Declar {
  public DeclarLocal declar_local;
  public DeclarVar declar_var;
  public DeclarLocal(int pos, DeclarLocal declar_local, DeclarVar declar_var) {
    this.pos = pos;
    this.declar_local = declar_local;
    this.declar_var = declar_var;
  }
}
