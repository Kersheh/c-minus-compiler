package symb;

public class SymbolArray extends SymbolInt {
  
  private int size;

  public SymbolArray(String id, int addr, int size) {
    super(id, addr);
    this.size = size;
  }

  public int getSize(){
    return this.size;
  }
}