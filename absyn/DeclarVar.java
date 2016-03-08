package absyn;

public class DeclarVar extends Declar {
  public TypeSpec type;
  public String name;
  public Boolean array;
  public DeclarVar(int pos, TypeSpec type, String name, Boolean array) {
    this.pos = pos;
    this.type = type;
    this.name = name;
    this.array = array;
  }
}
