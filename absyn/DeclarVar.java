package absyn;

public class DeclarVar extends Declar {
  public TypeSpec type;
  public String name;
  public Boolean array;
  public int size;

  public DeclarVar(int pos, TypeSpec type, String name, Boolean array) {
    this.pos = pos;
    this.type = type;
    this.name = name;
    this.array = array;
    this.size = 0;
  }

  public DeclarVar(int pos, TypeSpec type, String name, Boolean array, int size) {
    this.pos = pos;
    this.type = type;
    this.name = name;
    this.array = array;
    this.size = size;
  }
}
