package symb;

public class SymbolArray extends SymbolInt {
  
  private int size;

  public SymbolArray(String id, int addr) {
    super(id, addr);
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
    String s = "int " + this.id + "[" + this.size + "]";
    return s;
  }
}