package symb;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class SymbolTable {

  private Deque<HashMap<List<String>, Symbol>> tableStack = new ArrayDeque<>();
  private final static int SPACES = 4;
  private static SymbolTable instance = null;

  private SymbolTable() {
    tableStack.push(new LinkedHashMap<>());
  }

  public static SymbolTable getInstance(){
    if (SymbolTable.instance == null){
      SymbolTable.instance = new SymbolTable();
    }
    return SymbolTable.instance;
  }

  public void newScope(){
    this.tableStack.push(new LinkedHashMap<>());
  }

  public void leaveScope(){
    if(this.tableStack.size() <= 1){
      return;
    }
    this.tableStack.pop();
  }

  public boolean addSymbol(Symbol symb) {
    if (symb.getType() == Symbol.FUNC_TYPE && this.tableStack.size() != 1){
      return false;
    }
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    HashMap<List<String>, Symbol> top = this.tableStack.peek();
    if(top == null){
      throw new RuntimeException();
    }
    if (top.get(key) == null){
      top.put(key, symb);
    } else {
      return false;
    }
    return true;
  }

  public boolean validSymbolInScope(Symbol symb){
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    for(HashMap<List<String>, Symbol> table : this.tableStack){
      if (table.get(key) != null && sameType(table.get(key), symb)){
        return true;
      }
    }
    return false;
  }

  private boolean sameType(Symbol decl, Symbol use){
    if(decl == null || use == null || decl.getId() != use.getId()
        || decl.getType() != use.getType() || decl.getClass() != use.getClass()){
      return false;
    }

    if(decl.getClass() == SymbolFunction.class){
      SymbolFunction tempDecl = (SymbolFunction)decl;
      SymbolFunction tempUse = (SymbolFunction)use;
      if((tempDecl.getParameters() != null  && tempUse.getParameters() == null)
        || (tempDecl.getParameters() == null  && tempUse.getParameters() != null)
        || tempDecl.getParameters().size() != tempUse.getParameters().size()){
        return false;
      }
      Iterator declIter = tempDecl.getParameters().iterator();
      Iterator useIter = tempUse.getParameters().iterator();
      while(declIter.hasNext() && useIter.hasNext()){
        if (declIter.next().getClass() != useIter.next().getClass()){
          return false;
        }
      }
    }

    return true;
  }

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  static public void showTable() {
    System.out.println("Placeholder");
  } 
}
