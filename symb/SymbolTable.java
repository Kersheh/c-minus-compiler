package symb;
import java.util.Hashtable;

public class SymbolTable {
  public Hashtable<String, Symbol> table;

  public SymbolTable(int size) {
    this.table = new Hashtable<String, Symbol>(size);
  }

  public void addSymbol(String key, Symbol symb) {
    this.table.put(key, symb);
  }

  final static int SPACES = 4;

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  static public void showTable() {
    System.out.println("Placeholder");
  } 
}
