package absyn;

public class TypeSpec extends Absyn {
  public String type;
  public TypeSpec(int pos, String type) {
    this.pos = pos;
    this.type = type;
  }
}
