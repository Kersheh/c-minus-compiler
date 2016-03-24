package absyn;

public class TypeSpec extends Absyn {
  public String type;
  public static final String INT = "INT";
  public static final String VOID = "VOID";

  public TypeSpec(int pos, String type) {
    this.pos = pos;
    this.type = type;
  }
}
