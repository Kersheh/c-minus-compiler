package absyn;

public class Param extends Absyn {
  public TypeSpec type;
  public String id;
  public Boolean array;
  public Param(int pos, TypeSpec type, String id, Boolean array) {
    this.type = type;
    this.id = id;
    this.array = array;
  }
}
