package symb;

abstract public class SymbolTable {
  public int pos;

  final static int SPACES = 4;

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  static public void showTable() {
    System.out.println("Placeholder");
  } 
}
