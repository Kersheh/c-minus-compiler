package symb;

public class SymbolArray extends SymbolInt {
  
  private int size;

  public SymbolArray(String id) {
    super(id);
    this.size = 0;
  }

  public SymbolArray(String id, int size) {
    super(id);
    this.size = size;
  }

  public SymbolArray(String id, int addr, int size) {
    super(id, addr);
    this.size = size;
  }

  public int getSize(){
    return this.size;
  }

  @Override
  public String toString() {
    String index = "[" + this.size + "]";
    if (this.size == 0) {
      index = "[]";
    }
    String s = "Var: " + this.getType().toLowerCase() + " " + this.getId() + 
    index;
    return s;
  }
}