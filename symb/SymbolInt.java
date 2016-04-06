package symb;

public class SymbolInt extends Symbol {

  public SymbolInt(String id) {
    super(id);
  }
  
  public SymbolInt(String id, int addr) {
    super(id, addr);
  }

  @Override
  public String getType(){
    return Symbol.INT_TYPE;
  }

  @Override
  public String toString() {
    String s = "Var: " + this.getType().toLowerCase() + " " + this.getId();
    return s;
  }
}