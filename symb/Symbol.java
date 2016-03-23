package symb;

public class Symbol {
  Boolean constant;
  SymbolType type;
  String id;
  int value;

  static public enum SymbolType {
    INT, VOID
  }

  public Symbol(SymbolType type, String id) {
    this.constant = false;
    this.type = type;
    this.id = id;
  }

  public Symbol(Boolean constant, SymbolType type, String id, int val) {
    this.constant = constant;
    this.type = type;
    this.id = id;
    this.value = val;
  }

  public void setValue(int val, int pos) {
    if(this.constant)
      System.out.println("Assignment to constant variable at line " + pos);
    else this.value = val;
  }
}