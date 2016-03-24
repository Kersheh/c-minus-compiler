package symb;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import absyn.*;
import symb.exceptions.*;

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
    if(symb.getType() == Symbol.FUNC_TYPE && this.tableStack.size() != 1){
      return false;
    }
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    HashMap<List<String>, Symbol> top = this.tableStack.peek();
    if(top == null){
      throw new RuntimeException();
    }
    if(top.get(key) == null){
      top.put(key, symb);
    } else {
      return false;
    }
    return true;
  }

  public Symbol getMatchingSymbol(Symbol symb) throws InvalidTypeException,
      UndeclaredException {
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    for(HashMap<List<String>, Symbol> table : this.tableStack){
      if (table.get(key) != null) {
        try {
          sameType(table.get(key), symb);
        } catch(InvalidTypeException e) {
          throw e;
        } catch(UndeclaredException e) {
          throw e;
        }
        return table.get(key);
      }
    }
    return null;
  }

  private void sameType(Symbol decl, Symbol use) throws InvalidTypeException, 
      UndeclaredException {
    if(decl == null || use == null || !decl.getId().equals(use.getId()) 
       || !decl.getType().equals(use.getType())) {
      throw new UndeclaredException("Use of undeclared variable");
    }

    if(Symbol.INT_TYPE.equals(decl.getType()) && decl.getClass() != decl.getClass()) {
      if(decl.getClass() == SymbolArray.class){
        throw new InvalidTypeException(decl.getId() + " defined as array; used as int");
      } else {
        throw new InvalidTypeException(use.getId() + "  defined as array; used as int");
      }
    }

    if(decl.getClass() == decl.getClass() && Symbol.FUNC_TYPE.equals(decl.getType())){
      SymbolFunction tempDecl = (SymbolFunction)decl;
      SymbolFunction tempUse = (SymbolFunction)use;
      if(tempDecl.getParameters() != null  && tempUse.getParameters() == null){
        throw new InvalidTypeException(tempDecl.getId() + " takes zero parameters, "
                                       + tempUse.getParameters().size() 
                                       + " parameters were provided.");
      }
      if(tempDecl.getParameters() == null  && tempUse.getParameters() != null){
        throw new InvalidTypeException(tempDecl.getId() + " takes " + tempDecl.getParameters().size() 
                                       + " parameters, zero parameters were provided.");
      }
      if(tempDecl.getParameters() != null && tempUse.getParameters() != null){
        if(tempDecl.getParameters().size() != tempUse.getParameters().size()){
          throw new InvalidTypeException(tempDecl.getId() + " takes " + tempDecl.getParameters().size() 
                                         + " parameters, " + tempUse.getParameters().size() 
                                         + " parameters were provided.");
        }
        Iterator declIter = tempDecl.getParameters().iterator();
        Iterator useIter = tempUse.getParameters().iterator();
        while(declIter.hasNext() && useIter.hasNext()){
          SymbolFunction declNext = (SymbolFunction)declIter.next();
          SymbolFunction useNext = (SymbolFunction)useIter.next();
          if(declNext.getClass() != useNext.getClass()){
            throw new InvalidTypeException("Incorrect matching parameters: " + 
                                           declNext.getClass() + " and " + 
                                           useNext.getClass());
          }
        }
      }
    }
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
        System.out.println("Variable redefinition error on line " + tree.pos);
      }
    }
    else{
      Symbol s = new SymbolInt(tree.name, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Variable redefinition error on line " + tree.pos);
      }
    }
  }

  //
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

  //
  private void showTable(ExpAssign tree, int spaces) {
    showTable(tree.lhs, spaces);
    if(tree.rhs instanceof ExpCall){
      ExpCall call = (ExpCall) tree.rhs;
      Symbol s = new SymbolFunction(call.id, 0, null, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in an assignment requiring type INT on line "
                  + tree.pos);
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.rhs, spaces);
  }

  private void showTable(ExpCall tree, int spaces) {
    Symbol s = new SymbolFunction(tree.id, 0, null, null);
    try {
      this.getMatchingSymbol(s);
    }
    catch(Exception e) {
      indent(spaces);
      System.out.println(e.getMessage() + ": on line " + tree.pos);
    }
    if(tree.args != null)
      showTable(tree.args, spaces);
  }

  private void showTable(ExpVar tree, int spaces) {
    if(tree.exp == null) { //normal variable
      Symbol s = new SymbolInt(tree.name, 0);
      try {
        this.getMatchingSymbol(s);
      }
      catch(Exception e) {
        indent(spaces);
        System.out.println(e.getMessage() + ": on line " + tree.pos);
      }
    } else { //array variable
      Symbol s = new SymbolArray(tree.name, 0);
      try {
        Symbol match = this.getMatchingSymbol(s);
      }
      catch(Exception e) {
        indent(spaces);
        System.out.println(e.getMessage() + ": on line " + tree.pos);
      }

      if(tree.exp instanceof ExpCall){
        ExpCall call = (ExpCall) tree.exp;
        Symbol symb = new SymbolFunction(call.id, 0, null, TypeSpec.INT);
        try {
          SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(symb);
          if (TypeSpec.VOID.equals(match.getReturnType())){
            indent(spaces);
            System.out.println("Error: " + match.getId() + " of type VOID used in array indexing requiring type INT on line "
                    + tree.pos);
          }
        } catch (Exception e) {
          //Do nothing
        }
      }
      showTable(tree.exp, spaces);
    }
  }

  //
  private void showTable(ExpOp tree, int spaces) {
    if (tree.left instanceof ExpCall){
      ExpCall call = (ExpCall) tree.left;
      Symbol s = new SymbolFunction(call.id, 0, null, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in expression requiring type INT on line "
            + tree.pos);
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.left, spaces);
    if (tree.right instanceof ExpCall){
      ExpCall call = (ExpCall) tree.right;
      Symbol s = new SymbolFunction(call.id, 0, null, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in expression requiring type INT on line "
                  + tree.pos);
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.right, spaces);
  }
}
