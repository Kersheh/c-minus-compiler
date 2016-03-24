package symb;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import absyn.*;

public class SymbolTable {

  private Deque<HashMap<List<String>, Symbol>> tableStack = new ArrayDeque<>();
  private final static int SPACES = 4;
  private static SymbolTable instance = null;

  private SymbolTable() {
    tableStack.push(new LinkedHashMap<>());
    Symbol input = new SymbolFunction("input", 0, null, TypeSpec.INT);
    Symbol output = new SymbolFunction("output", 0, null, TypeSpec.VOID);
    this.addSymbol(input);
    this.addSymbol(output);
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

  public Symbol getMatchingSymbol(Symbol symb){
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    for(HashMap<List<String>, Symbol> table : this.tableStack){
      if (table.get(key) != null && sameType(table.get(key), symb)){
        return table.get(key);
      }
    }
    return null;
  }

  private boolean sameType(Symbol decl, Symbol use){
    if(decl == null || use == null || !decl.getId().equals(use.getId())
        || !decl.getType().equals(use.getType()) || decl.getClass() != use.getClass()){
      return false;
    }

    if(decl.getClass() == SymbolFunction.class){
      SymbolFunction tempDecl = (SymbolFunction)decl;
      SymbolFunction tempUse = (SymbolFunction)use;
      if((tempDecl.getParameters() != null  && tempUse.getParameters() == null)
        || (tempDecl.getParameters() == null  && tempUse.getParameters() != null)){
        return false;
      }
      if(tempDecl.getParameters() != null && tempUse.getParameters() != null){
        if (tempDecl.getParameters().size() != tempUse.getParameters().size()){
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
    }

    return true;
  }

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  public void printScope(int spaces){
    for(Symbol s : this.tableStack.peek().values()){
      indent(spaces);
      System.out.println(s);
    }
  }

  public void showTable(DeclarList tree, int spaces) {
    while(tree != null) {
      showTable(tree.head, spaces);
      tree = tree.tail;
    }
    System.out.println("Global scope at end of parse:");
    spaces += SPACES;
    this.printScope(spaces);
  }

  public void showTable(DeclarListLocal tree, int spaces) {
    while(tree != null) {
      showTable(tree.head, spaces);
      tree = tree.tail;
    }
  }

  //
  public void showTable(ExpList tree, int spaces) {
    while(tree != null) {
      showTable(tree.head, spaces);
      tree = tree.tail;
    }
  }

  public void showTable(StmtList tree, int spaces) {
    while(tree != null) {
      showTable(tree.head, spaces);
      tree = tree.tail;
    }
  }

  public void showTable(ParamList tree, int spaces) {
    while(tree != null) {
      showTable(tree.head, spaces);
      tree = tree.tail;
    }
  }

  public void showTable(Declar tree, int spaces) {
    if(tree instanceof DeclarVar)
      showTable((DeclarVar)tree, spaces);
    else if(tree instanceof DeclarFun)
      showTable((DeclarFun)tree, spaces);
    else {
      //do nothing ¯\_(ツ)_/¯
    }
  }

  private void showTable(DeclarVar tree, int spaces) {
    if(tree == null) {
      return;
    }
    if (tree.array){
      Symbol s = new SymbolArray(tree.name, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Variable redefinition error");
      }
    }
    else{
      Symbol s = new SymbolInt(tree.name, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Variable redefinition error");
      }
    }
  }

  private void showTable(DeclarFun tree, int spaces) {
    spaces += SPACES;
    indent(spaces);
    System.out.println("Local scope at " + tree.name +  ":");
    Symbol s = new SymbolFunction(tree.name, 0, null, tree.type.type);
    if(!this.addSymbol(s)){
      indent(spaces);
      System.out.println("Function redefinition error");
    }
    this.newScope();
    spaces += SPACES;
    showTable(tree.params, spaces);
    showTable(tree.stmt, spaces);
    this.printScope(spaces);
    this.leaveScope();
  }

  private void showTable(Params tree, int spaces) {
    if(!tree.isVoidParams){
      showTable(tree.param_list, spaces);
    }
  }

  private void showTable(Param tree, int spaces) {
    if (tree.array) {
      Symbol s = new SymbolArray(tree.id, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Parameter redefinition error");
      }
    }
    else {
      Symbol s = new SymbolInt(tree.id, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Parameter redefinition error");
      }
    }
  }

  //
  private void showTable(Stmt tree, int spaces) {
    if(tree instanceof StmtComp){
      indent(spaces);
      System.out.println("Local block:");
      this.newScope();
      spaces += SPACES;
      showTable((StmtComp)tree, spaces);
      this.printScope(spaces);
      this.leaveScope();
    }
    else if(tree instanceof StmtExp){
      showTable((StmtExp)tree, spaces);
    }
    else if(tree instanceof StmtSelect){
      showTable((StmtSelect)tree, spaces);
    }
    else if(tree instanceof StmtWhile){
      showTable((StmtWhile)tree, spaces);
    }
    else if(tree instanceof StmtReturn){
      showTable((StmtReturn)tree, spaces);
    }
    else {
      indent(spaces);
      System.out.println("Illegal statement");
    }
  }

  private void showTable(StmtComp tree, int spaces) {
    showTable(tree.declar_local, spaces);
    showTable(tree.stmt_list, spaces);
  }

  private void showTable(StmtExp tree, int spaces) {
    showTable(tree.exp, spaces);
  }

  private void showTable(StmtSelect tree, int spaces) {
    showTable(tree.test, spaces);
    showTable(tree.then_stmt, spaces);
    if (tree.else_stmt != null)
      showTable(tree.else_stmt, spaces);
  }

  private void showTable(StmtWhile tree, int spaces) {
    showTable(tree.test, spaces);
    showTable(tree.stmt, spaces);
  }

  //
  private void showTable(StmtReturn tree, int spaces) {
    if (tree.item != null)
      showTable(tree.item, spaces);
  }

  //
  public void showTable(Exp tree, int spaces) {
    if(tree instanceof ExpAssign)
      showTable((ExpAssign)tree, spaces);
    else if(tree instanceof ExpCall)
      showTable((ExpCall)tree, spaces);
    else if(tree instanceof ExpOp)
      showTable((ExpOp)tree, spaces);
    else if(tree instanceof ExpVar)
      showTable((ExpVar)tree, spaces);
  }

  private void showTable(ExpAssign tree, int spaces) {
    showTable(tree.lhs, spaces);
    showTable(tree.rhs, spaces);
  }

  //
  private void showTable(ExpCall tree, int spaces) {
    Symbol s = new SymbolFunction(tree.id, 0, null, null);
    if(!this.validSymbolInScope(s)){
      indent(spaces);
      System.out.println("Error: Use of undeclared function on line " + tree.pos);
    }
    if(tree.args != null)
      showTable(tree.args, spaces);
  }

  //
  private void showTable(ExpVar tree, int spaces) {
    if(tree.exp == null) { //normal variable
      Symbol s = new SymbolInt(tree.name, 0);
      if(!this.validSymbolInScope(s)){
        indent(spaces);
        System.out.println("Error: Use of undeclared variable on line " + tree.pos);
      }
    } else { //array variable
      Symbol s = new SymbolArray(tree.name, 0);
      if(!this.validSymbolInScope(s)){
        indent(spaces);
        System.out.println("Error: Use of undeclared variable on line " + tree.pos);
      }
      else if (tree.exp instanceof ExpCall) {
        SymbolFunction call = new SymbolFunction(tree.name, 0, null, null);
        SymbolFunction def = (SymbolFunction)this.getMatchingSymbol(call);
        if(def == null){
          System.out.println("Error: Use of undeclared function on line " + tree.pos);
        }
        else if(TypeSpec.VOID.equals(def.getReturnType())){
          System.out.println("Error: Function with void return type cannot be used for indexing. Line " + tree.pos);
        }
      }
    }
    showTable(tree.exp, spaces);
  }

  //
  private void showTable(ExpOp tree, int spaces) {
    showTable(tree.left, spaces);
    showTable(tree.right, spaces);
  }
}
