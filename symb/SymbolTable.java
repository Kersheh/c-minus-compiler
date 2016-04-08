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
  private SymbolFunction currentFunction;
  private int temp = 0;

  public boolean error = false;

  private int currentOffset = 0;

  private int globalOffset = 0;

  public SymbolTable() {
    this.newScope();
    SymbolFunction input = new SymbolFunction("input", 4, TypeSpec.INT);
    SymbolFunction output = new SymbolFunction("output", 7, TypeSpec.VOID);
    output.addParameter(new SymbolInt("x", 0));
    this.addSymbol(input);
    this.addSymbol(output);
  }

  public void newScope(){
    this.tableStack.push(new LinkedHashMap<>());
  }

  public void leaveScope(){
    if(this.tableStack.size() <= 1){
      return;
    }
    this.currentOffset += this.tableStack.peek().size();
    this.tableStack.pop();
  }

  public SymbolInt newTemp(){
    SymbolInt symb = new SymbolInt( "_t" + ++temp);
    this.addSymbol(symb);
    return symb;
  }

  public SymbolArray newTempArray(int size){
    SymbolArray symb = new SymbolArray( "_t" + ++temp, size);
    this.addSymbol(symb);
    return symb;
  }

  public boolean addSymbol(Symbol symb) {
    if(Symbol.FUNC_TYPE.equals(symb.getType()) && this.tableStack.size() != 1){
      return false;
    }
    if(Symbol.FUNC_TYPE.equals(symb.getType())) {
      this.currentFunction = (SymbolFunction)symb;
      this.currentOffset = 0;
    }
    List<String> key = Arrays.asList(symb.getId(), symb.getType());
    HashMap<List<String>, Symbol> top = this.tableStack.peek();
    if(top == null){
      throw new RuntimeException();
    }
    if(top.get(key) == null){
      symb.setScope(this.tableStack.size());
      if (symb.isGlobalVar()){
        symb.setAddress(this.globalOffset);
        this.globalOffset--;
      } else if(symb.getClass() == SymbolInt.class) {
        symb.setAddress(this.currentOffset);
      } else if (symb.getClass() == SymbolArray.class) {
        this.currentOffset -= (((SymbolArray)symb).getSize()-1);
        symb.setAddress(this.currentOffset);
      }
      top.put(key, symb);
      if (!Symbol.FUNC_TYPE.equals(symb.getType()))
        this.currentOffset--;
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
        } catch(InvalidTypeException | UndeclaredException e) {
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

    if(Symbol.INT_TYPE.equals(decl.getType()) && decl.getClass() != use.getClass()) {
      if(decl.getClass() == SymbolArray.class){
        throw new InvalidTypeException(decl.getId() + " defined as array; used as int");
      } else {
        throw new InvalidTypeException(use.getId() + "  defined as array; used as int");
      }
    }
  }

  public boolean haveMatchingParameters(SymbolFunction decl, SymbolFunction use){
    if(decl.getParameters() == null  && use.getParameters() == null){
      return true;
    }
    if((decl.getParameters() != null  && use.getParameters() == null)
      || (decl.getParameters() == null  && use.getParameters() != null)){
        return false;
    }
    if(decl.getParameters() != null && use.getParameters() != null){
      if(decl.getParameters().size() != use.getParameters().size()){
        return false;
      }
      Iterator<SymbolInt> declIter = decl.getParameters().iterator();
      Iterator<SymbolInt> useIter = use.getParameters().iterator();
      while(declIter.hasNext() && useIter.hasNext()){
        if(declIter.next().getClass() != useIter.next().getClass()){
          return false;
        }
      }
    }
    return true;
  }

  public void checkType(ExpCall call){
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          this.error(match.getId() + " of type VOID used in expression requiring type INT on line " + (call.pos + 1));
        }
      } catch (Exception e) {
        //Do nothing
      }
  }

  public boolean inGlobalScope(){
    return this.tableStack.size() == 1;
  }

  public boolean inFunctionOuterScope(){
    return this.tableStack.size() == 2;
  }

  public int getCurrentOffset(){
    return this.currentOffset;
  }

  public int getGlobalOffset(){
    return this.globalOffset;
  }


  public void error(String message){
    System.err.println("Error: " + message);
    this.error = true;
  }

  public SymbolFunction getCurrentFunction(){
    return this.currentFunction;
  }

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  public void printScope(int spaces){
    if(this.tableStack.peek().isEmpty()) {
      indent(spaces);
      System.out.println("No variables defined");
    } else {
      for(Symbol s : this.tableStack.peek().values()){
        indent(spaces);
        System.out.println(s);
      }
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
  public void showTable(ExpList tree, int spaces, SymbolFunction func) {
    while(tree != null) {
      if(tree.head instanceof ExpVar){
        ExpVar var = (ExpVar) tree.head;
        if (var.exp != null){
          func.addParameter(new SymbolInt("arg", 0));
        } else {
          Symbol match = null;
          SymbolInt s = new SymbolArray(var.name, 0);
          try {
            match = this.getMatchingSymbol(s);
          } catch (InvalidTypeException e) {
            s = new SymbolInt(var.name, 0);
          } catch (Exception e){
            //Do nothing
          }
          func.addParameter(s);
        }
      } else {
        func.addParameter(new SymbolInt("arg", 0));
      }
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
    if (tree.array){
      Symbol s = new SymbolArray(tree.name, tree.size);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Variable redefinition error on line " + (tree.pos + 1));
      }
    }
    else{
      Symbol s = new SymbolInt(tree.name);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Variable redefinition error on line " + (tree.pos + 1));
      }
    }
  }

  private void showTable(DeclarFun tree, int spaces) {
    spaces += SPACES;
    indent(spaces);
    System.out.println("Local scope at " + tree.name +  ":");
    Symbol s = new SymbolFunction(tree.name, 0, tree.type.type);
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
      SymbolArray s = new SymbolArray(tree.id, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Parameter redefinition error");
      } else {
        this.currentFunction.addParameter(s);
      }
    }
    else {
      SymbolInt s = new SymbolInt(tree.id, 0);
      if(!this.addSymbol(s)){
        indent(spaces);
        System.out.println("Parameter redefinition error");
      } else {
        this.currentFunction.addParameter(s);
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
    if (tree.test instanceof ExpCall){
      ExpCall call = (ExpCall) tree.test;
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in condition requiring type INT on line "
                  + tree.pos);
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.test, spaces);
    showTable(tree.then_stmt, spaces);
    if (tree.else_stmt != null)
      showTable(tree.else_stmt, spaces);
  }

  private void showTable(StmtWhile tree, int spaces) {
    if (tree.test instanceof ExpCall){
      ExpCall call = (ExpCall) tree.test;
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in condition requiring type INT on line "
                  + tree.pos);
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.test, spaces);
    showTable(tree.stmt, spaces);
  }

  private void showTable(StmtReturn tree, int spaces) {
    if (tree.item != null) {
      if(!currentFunction.getReturnType().equals(TypeSpec.INT)) {
        indent(spaces);
        System.out.println("Incorrect return type on line " + (tree.pos + 1));
      }
      showTable(tree.item, spaces);
    } else {
      if(!currentFunction.getReturnType().equals(TypeSpec.VOID)) {
        indent(spaces);
        System.out.println("Incorrect return type on line " + (tree.pos + 1));
      }
    }
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

  private void showTable(ExpAssign tree, int spaces) {
    showTable(tree.lhs, spaces);
    if(tree.rhs instanceof ExpCall){
      ExpCall call = (ExpCall) tree.rhs;
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in an assignment requiring type INT on line "
                  + (tree.pos + 1));
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.rhs, spaces);
  }

  private void showTable(ExpCall tree, int spaces) {
    SymbolFunction s = new SymbolFunction(tree.id, 0, null);
    SymbolFunction match = null;
    try {
       match = (SymbolFunction) this.getMatchingSymbol(s);
    }
    catch(Exception e) {
      indent(spaces);
      System.out.println(e.getMessage() + ": on line " + (tree.pos + 1));
    }
    if (tree.args != null) {
      showTable(tree.args, spaces, s);
    }
    if (!haveMatchingParameters(match, s)) {
      indent(spaces);
      System.out.println("Error: arguments in function call to " + match.getId() + " on line "
              + tree.pos + " does not match definition");
    }
  }

  private void showTable(ExpVar tree, int spaces) {
    if(tree.exp == null) { //normal variable
      Symbol s = new SymbolInt(tree.name, 0);
      try {
        this.getMatchingSymbol(s);
      }
      catch(InvalidTypeException e) {
        //Do nothing. Arrays can be used without brackets in some cases
        //i.e. int foo(int arr[]) ...  int a[10]; foo(a);
      } catch (Exception e){
        indent(spaces);
        System.out.println(e.getMessage() + ": on line " + (tree.pos + 1));
      }
    } else { //array variable
      Symbol s = new SymbolArray(tree.name, 0);
      try {
        Symbol match = this.getMatchingSymbol(s);
      }
      catch(Exception e) {
        indent(spaces);
        System.out.println(e.getMessage() + ": on line " + (tree.pos + 1));
      }

      if(tree.exp instanceof ExpCall){
        ExpCall call = (ExpCall) tree.exp;
        Symbol symb = new SymbolFunction(call.id, 0, TypeSpec.INT);
        try {
          SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(symb);
          if (TypeSpec.VOID.equals(match.getReturnType())){
            indent(spaces);
            System.out.println("Error: " + match.getId() + " of type VOID used in array indexing requiring type INT on line "
                    + (tree.pos + 1));
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
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in expression requiring type INT on line "
                  + (tree.pos + 1));
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.left, spaces);
    if (tree.right instanceof ExpCall){
      ExpCall call = (ExpCall) tree.right;
      Symbol s = new SymbolFunction(call.id, 0, TypeSpec.INT);
      try {
        SymbolFunction match = (SymbolFunction) this.getMatchingSymbol(s);
        if (TypeSpec.VOID.equals(match.getReturnType())){
          indent(spaces);
          System.out.println("Error: " + match.getId() + " of type VOID used in expression requiring type INT on line "
                  + (tree.pos + 1));
        }
      } catch (Exception e) {
        //Do nothing
      }
    }
    showTable(tree.right, spaces);
  }
}
