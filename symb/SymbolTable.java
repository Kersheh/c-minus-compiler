package symb;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

public class SymbolTable {

  private Stack<HashMap<List<String>, Symbol>> tableStack = new Stack<>();
  private final static int SPACES = 4;

  public SymbolTable() {
    tableStack.push(new LinkedHashMap<>());
  }

  public void newScope(){
    this.tableStack.push(new LinkedHashMap<>());
  }

  public void leaveScope(){
    this.tableStack.pop();
  }

  public void addSymbol(Symbol symb) {
    if (symb.getType() == Symbol.FUNC_TYPE && this.tableStack.size() != 1){
      //error
      return;
    }
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    HashMap<List<String>, Symbol> top = this.tableStack.peek();
    if (top.get(key) == null){
      top.put(key, symb);
    } else {
      //error
    }
  }

  public boolean validSymbolInScope(Symbol symb){
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    for (HashMap table : this.tableStack){
      if (table.get(key) != null){
        return true;
      }
    }
    return false;
  }

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  static public void showTable() {
    System.out.println("Placeholder");
  } 
}
