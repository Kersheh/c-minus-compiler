package absyn;

public class Param extends Absyn {
  public TypeSpec type;
  public Boolean array;
  public Param(int pos, TypeSpec type, Boolean array) {
    this.type = type;
    this.array = array;
  }
}
