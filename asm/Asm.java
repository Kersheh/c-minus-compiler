package asm;

import absyn.*;
import symb.*;
import symb.exceptions.*;
import java.io.*;
import java.util.*;

public class Asm {
  private String input = "";
  private StringBuilder asm = new StringBuilder(); //string output to .tm file
  private SymbolTable symbolTable = new SymbolTable();  
  private int address = 0;
  private int pc = 0;
  static private final int PC = 7;
  static private final int GP = 6;
  static private final int FP = 5;
  static private final int AC = 0;
  static private final int AC1 = 1;

  private enum Operations {
    HALT, IN, OUT, ADD, SUB, MUL, DIV, LD, ST,
    LDA, LDC, JLT, JLE, JGT, JGE, JEQ, JNE;
  }

  private List<Operations> registerOnly = Arrays.asList(
    Operations.HALT, Operations.IN, Operations.OUT, 
    Operations.ADD, Operations.SUB, Operations.MUL, 
    Operations.DIV, Operations.LDC
  );

  /* add assembly line to output StringBuilder */
  private void addLine(int address,  Operations oper, int r, int s, int t) {
    if(registerOnly.contains(oper)) {
      asm.append(address + ":  " + oper.name() + " " + r + "," + s + "," + t + "\n");
    }
    else {
      asm.append(address + ":  " + oper.name() + " " + r + "," + s + "(" + t + ")" + "\n");
    }
  }

  private void emitComment(String s) {
    asm.append("* " + s + "\n");
  }

  /* default assembly code header */
  private void header(String filename) {
    asm.append("* C-Minus Compilation to TM Code\n");
    String out = filename.substring(0, filename.lastIndexOf(".")) + ".tm";
    asm.append("* File: " + out + "\n");
  }

  /* default assembly code prelude - input and ouput */
  private void prelude() {
    asm.append("* Standard prelude:\n");
    asm.append("0:     LD  6,0(0)\n");
    asm.append("1:    LDA  5,0(6)\n");
    asm.append("2:     ST  0,0(0)\n");
    asm.append("* Jump around i/o routines here\n");
    asm.append("* code for input routine\n");
    asm.append("4:     ST  0,-1(5)\n");
    asm.append("5:     IN  0,0,0\n");
    asm.append("6:     LD  7,-1(5)\n");
    asm.append("* code for input routine\n");
    asm.append("7:     ST  0,-1(5)\n");
    asm.append("8:     LD  0,-2(5)\n");
    asm.append("9:    OUT  0,0,0\n");
    asm.append("10:    LD  7,-1(5)\n");
    asm.append("3:    LDA  7,7(7)\n");
    asm.append("* End of standard prelude.\n");
    address += 10;
  }

  /* default assembly code tail */
  private void end() {
    asm.append("* End of execution:\n");
    asm.append(++address + ":     HALT  0,0,0\n");
  }

  /* generate assembly code and output to file */
  public void generateAssembly(String filename, DeclarList tree) {
    header(filename);
    prelude();
    this.genCode(tree);
    end();

    /* output file name with path and .tm file type */
    String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
    /* export assembly to external file */
    try {
      File file = new File(write);
      if(!file.exists()) 
        file.createNewFile();

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(asm.toString());
      bw.close();
      System.out.println("Compile complete, saved to " + write);
    } catch(IOException e) {
      System.out.println("Error: failed to write to " + write);
    }
  }

  public void genCode(DeclarList tree) {
    while(tree != null) {
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(DeclarListLocal tree) {
    while(tree != null) {
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(ExpList tree, SymbolFunction func) {
    while(tree != null) {
      if(tree.head instanceof ExpVar){
        ExpVar var = (ExpVar) tree.head;
        if (var.exp != null){
          func.addParameter(new SymbolInt("arg", 0));
        } else {
          Symbol match = null;
          SymbolInt s = new SymbolArray(var.name, 0);
          try {
            match = this.symbolTable.getMatchingSymbol(s);
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
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(StmtList tree) {
    while(tree != null) {
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(ParamList tree) {
    while(tree != null) {
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(Declar tree) {
    if(tree instanceof DeclarVar)
      genCode((DeclarVar)tree);
    else if(tree instanceof DeclarFun)
      genCode((DeclarFun)tree);
    else {
      //do nothing ¯\_(ツ)_/¯
    }
  }

  private void genCode(DeclarVar tree) {
    if(tree == null) {
      return;
    }
    if (tree.array){
      Symbol s = new SymbolArray(tree.name, tree.size);
      if(!this.symbolTable.addSymbol(s)){
        this.symbolTable.error("Variable redefinition error on line " + (tree.pos + 1));
      }
    }
    else{
      Symbol s = new SymbolInt(tree.name);
      if(!this.symbolTable.addSymbol(s)){
        this.symbolTable.error("Variable redefinition error on line " + (tree.pos + 1));
      }
    }
  }

  private void genCode(DeclarFun tree) {
    Symbol s = new SymbolFunction(tree.name, tree.type.type);
    if(!this.symbolTable.addSymbol(s)){
      this.symbolTable.error("Function redefinition error");
    }
    this.symbolTable.newScope();
    this.symbolTable.addSymbol(new SymbolInt("_ofp"));
    this.symbolTable.addSymbol(new SymbolInt("_ret"));
    this.address++;
    int jmpAround = this.address;
    this.address++;
    this.emitComment("processing function: " + tree.name);
    this.addLine(this.address, Operations.ST, 0, -1, 5);
    genCode(tree.params);
    genCode(tree.stmt);
    this.addLine(jmpAround, Operations.LDA, 7, this.address - jmpAround, 7);
    this.symbolTable.leaveScope();
  }

  private void genCode(Params tree) {
    if(!tree.isVoidParams){
      genCode(tree.param_list);
    }
  }

  private void genCode(Param tree) {
    if (tree.array) {
      SymbolArray s = new SymbolArray(tree.id, 0);
      if(!this.symbolTable.addSymbol(s)){
        this.symbolTable.error("Parameter redefinition error");
      } else {
        this.symbolTable.getCurrentFunction().addParameter(s);
      }
    }
    else {
      SymbolInt s = new SymbolInt(tree.id, 0);
      if(!this.symbolTable.addSymbol(s)){
        this.symbolTable.error("Parameter redefinition error");
      } else {
        this.symbolTable.getCurrentFunction().addParameter(s);
      }
    }
  }

  private void genCode(Stmt tree) {
    if(tree instanceof StmtComp){
      this.symbolTable.newScope();
      genCode((StmtComp)tree);
      this.symbolTable.leaveScope();
    }
    else if(tree instanceof StmtExp){
      genCode((StmtExp)tree);
    }
    else if(tree instanceof StmtSelect){
      genCode((StmtSelect)tree);
    }
    else if(tree instanceof StmtWhile){
      genCode((StmtWhile)tree);
    }
    else if(tree instanceof StmtReturn){
      genCode((StmtReturn)tree);
    }
    else {
      this.symbolTable.error("Illegal statement");
    }
  }

  private void genCode(StmtComp tree) {
    genCode(tree.declar_local);
    genCode(tree.stmt_list);
  }

  private void genCode(StmtExp tree) {
    genCode(tree.exp);
  }

  private void genCode(StmtSelect tree) {
    if (tree.test instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.test);
    }
    genCode(tree.test);
    genCode(tree.then_stmt);
    if (tree.else_stmt != null)
      genCode(tree.else_stmt);
  }

  private void genCode(StmtWhile tree) {
    if (tree.test instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.test);
    }
    genCode(tree.test);
    genCode(tree.stmt);
  }

  private void genCode(StmtReturn tree) {
    if (tree.item != null) {
      if(!this.symbolTable.getCurrentFunction().getReturnType().equals(TypeSpec.INT)) {
        this.symbolTable.error("Incorrect return type on line " + (tree.pos + 1));
      }
      genCode(tree.item);
    } else {
      if(!this.symbolTable.getCurrentFunction().getReturnType().equals(TypeSpec.VOID)) {
        this.symbolTable.error("Incorrect return type on line " + (tree.pos + 1));
      }
    }
  }

  public void genCode(Exp tree) {
    if(tree instanceof ExpAssign)
      genCode((ExpAssign)tree);
    else if(tree instanceof ExpCall)
      genCode((ExpCall)tree);
    else if(tree instanceof ExpOp)
      genCode((ExpOp)tree);
    else if(tree instanceof ExpVar)
      genCode((ExpVar)tree);
  }

  private void genCode(ExpAssign tree) {
    genCode(tree.lhs);
    if(tree.rhs instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.rhs);
    }
    genCode(tree.rhs);
  }

  private void genCode(ExpCall tree) {
    SymbolFunction s = new SymbolFunction(tree.id, 0, null);
    SymbolFunction match = null;
    try {
       match = (SymbolFunction) this.symbolTable.getMatchingSymbol(s);
    }
    catch(Exception e) {
      this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
    }
    if (tree.args != null) {
      genCode(tree.args, s);
    }
    if (!this.symbolTable.haveMatchingParameters(match, s)) {
      this.symbolTable.error("arguments in function call to " + match.getId() + " on line "
              + tree.pos + " does not match definition");
    }
  }

  private void genCode(ExpVar tree) {
    if(tree.exp == null) { //normal variable
      Symbol s = new SymbolInt(tree.name, 0);
      try {
        this.symbolTable.getMatchingSymbol(s);
      }
      catch(InvalidTypeException e) {
        //Do nothing. Arrays can be used without brackets in some cases
        //i.e. int foo(int arr[]) ...  int a[10]; foo(a);
      } catch (Exception e){
        this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
      }
    } else { //array variable
      Symbol s = new SymbolArray(tree.name, 0);
      try {
        Symbol match = this.symbolTable.getMatchingSymbol(s);
      }
      catch(Exception e) {
        this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
      }

      if(tree.exp instanceof ExpCall){
        this.symbolTable.checkType((ExpCall)tree.exp);
      }
      genCode(tree.exp);
    }
  }

  private void genCode(ExpOp tree) {
    if (tree.left instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.left);
    }
    genCode(tree.left);
    if (tree.right instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.right);
    }
    genCode(tree.right);
  }
}