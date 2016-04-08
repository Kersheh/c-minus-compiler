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
    Operations.DIV
  );

  /* add assembly line to output StringBuilder */
  private void emitCode(int address, Operations oper, int r, int s, int t, String comment) {
    String addr = String.format("%1$3s", Integer.toString(address));
    String op = String.format("%1$6s", oper.name());
    if(registerOnly.contains(oper)) {
      asm.append(addr + ": " + op + "  " + r + "," + s + "," + t + " \t" + comment + "\n");
    }
    else {
      asm.append(addr + ": " + op + "  " + r + "," + s + "(" + t + ")" + " \t"  +  comment + "\n");
    }
  }

  private void emitCode(int address, Operations oper, int r, int s, int t) {
    this.emitCode(address, oper, r, s, t, "");
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
    asm.append("  0:     LD  6,0(0) \t\n");
    asm.append("  1:    LDA  5,0(6) \t\n");
    asm.append("  2:     ST  0,0(0) \t\n");
    asm.append("* Jump around i/o routines here\n");
    asm.append("* code for input routine\n");
    asm.append("  4:     ST  0,-1(5) \t\n");
    asm.append("  5:     IN  0,0,0 \t\n");
    asm.append("  6:     LD  7,-1(5) \t\n");
    asm.append("* code for output routine\n");
    asm.append("  7:     ST  0,-1(5) \t\n");
    asm.append("  8:     LD  0,-2(5) \t\n");
    asm.append("  9:    OUT  0,0,0 \t\n");
    asm.append(" 10:     LD  7,-1(5) \t\n");
    asm.append("  3:    LDA  7,7(7) \t\n");
    asm.append("* End of standard prelude.\n");
    address += 10;
  }

  /* default assembly code tail */
  private void end() {
    try {
      SymbolFunction match = (SymbolFunction) this.symbolTable.getMatchingSymbol(new SymbolFunction("main", null));
      this.emitCode(++this.address, Operations.ST, FP, symbolTable.getGlobalOffset(), FP, "push ofp");
      this.emitCode(++this.address, Operations.LDA, FP, symbolTable.getGlobalOffset(), FP, "push frame");
      this.emitCode(++this.address, Operations.LDA, AC, 1, PC, "load ac with ret ptr");
      this.emitCode(++this.address, Operations.LDA, PC, match.getAddress() - this.address - 1, PC, "jump to " + match.getId() + " loc");
      this.emitCode(++this.address, Operations.LD, FP, 0, FP, "pop frame");
    }
    catch(UndeclaredException e) {
      this.symbolTable.error("Missing declaration of main function");
    } catch (Exception e) {

    }
    asm.append("* End of execution:\n");
    asm.append(String.format("%1$3s", Integer.toString(++address)));
    asm.append(":   HALT  0,0,0 \t\n");
  }

  /* generate assembly code and output to file */
  public void generateAssembly(String filename, DeclarList tree) {
    header(filename);
    prelude();
    this.genCode(tree);
    end();

    if(this.symbolTable.error) {
      return;
    }
    /* output file name with path and .tm file type */
    String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
    /* export assembly to external file */
    try {
      File file = new File(write);
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
      if (tree.head instanceof DeclarVar){
        this.emitComment("allocating global var: " + ((DeclarVar)tree.head).name);
      }
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(DeclarListLocal tree) {
    while(tree != null) {
      this.emitComment("allocating local var: " + tree.head.name);
      genCode(tree.head);
      tree = tree.tail;
    }
  }

  public void genCode(ExpList tree, SymbolFunction func) {
    List<Integer> argAddresses = new LinkedList<>();
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
          } catch (Exception e) {
            this.symbolTable.error("Invaild type for argument on line: " + tree.head.pos);
          }
          func.addParameter(s);
        }
        genCode(var);
        SymbolInt temp = symbolTable.newTemp();
        this.emitCode(++this.address, Operations.ST, AC, temp.getAddress(), FP, "store arg val");
        argAddresses.add(temp.getAddress());
      } else {
        func.addParameter(new SymbolInt("arg", 0));
        genCode(tree.head);
        SymbolInt temp = symbolTable.newTemp();
        this.emitCode(++this.address, Operations.ST, AC, temp.getAddress(), FP, "store arg val");
        argAddresses.add(temp.getAddress());
      }
      tree = tree.tail;
    }
    int i = 0;
    for(Integer ad : argAddresses) {
      this.emitCode(++this.address, Operations.LD, AC, ad, FP, "load arg val");
      this.emitCode(++this.address, Operations.ST, AC, symbolTable.getCurrentOffset() - (2 + i), FP, "store arg val in next frame");
      i++;
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
    this.address++;
    int jmpAround = this.address;
    Symbol s = new SymbolFunction(tree.name, ++this.address, tree.type.type);
    if(!this.symbolTable.addSymbol(s)){
      this.symbolTable.error("Function redefinition error of function " + tree.name + " on line: " + tree.pos);
    }
    this.symbolTable.newScope();
    this.symbolTable.addSymbol(new SymbolInt("_ofp"));
    this.symbolTable.addSymbol(new SymbolInt("_ret"));

    this.emitComment("processing function: " + tree.name);
    this.emitCode(this.address, Operations.ST, 0, -1, 5);
    genCode(tree.params);
    genCode(tree.stmt);
    this.emitCode(++this.address, Operations.LD, PC, -1, FP);
    this.emitCode(jmpAround, Operations.LDA, PC, this.address - jmpAround, PC, "jump around " + tree.name + " body");
    this.symbolTable.leaveScope();
  }

  private void genCode(Params tree) {
    if(!tree.isVoidParams){
      genCode(tree.param_list);
    }
  }

  private void genCode(Param tree) {
    this.emitComment("allocating parameter: " + tree.id);
    if (tree.array) {
      SymbolArray s = new SymbolArray(tree.id);
      if(!this.symbolTable.addSymbol(s)){
        this.symbolTable.error("Parameter redefinition error");
      } else {
        this.symbolTable.getCurrentFunction().addParameter(s);
      }
    }
    else {
      SymbolInt s = new SymbolInt(tree.id);
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
    this.emitComment("-> if");
    genCode(tree.test);
    this.address++;
    int jmpAround = this.address;
    genCode(tree.then_stmt);
    this.emitCode(jmpAround, Operations.JEQ, AC, this.address + 1 - jmpAround, PC, "if: jmp to else");
    if (tree.else_stmt != null) {
      jmpAround = ++this.address;
      genCode(tree.else_stmt);
      this.emitCode(jmpAround, Operations.LDA, PC, this.address - jmpAround, PC, "if: jmp to end of else");
    }
    this.emitComment("<- if");
  }

  private void genCode(StmtWhile tree) {
    if (tree.test instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.test);
    }
    this.emitComment("-> while");
    int test = this.address;
    genCode(tree.test);
    this.address++;
    int jmpAround = this.address;
    genCode(tree.stmt);
    this.emitCode(++this.address, Operations.LDA, PC, test - this.address, PC, "while: unconditional jmp to start");
    this.emitCode(jmpAround, Operations.JEQ, AC, this.address - jmpAround, PC, "while: jmp around on false");
    this.emitComment("<- while");
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
    this.emitCode(++this.address, Operations.LD, PC, -1, FP, "return to caller");
  }

  public void genCode(Exp tree) {
    if(tree instanceof ExpAssign)
      genCode((ExpAssign)tree);
    else if(tree instanceof ExpCall)
      genCode((ExpCall)tree);
    else if(tree instanceof ExpConst)
      genCode((ExpConst)tree);
    else if(tree instanceof ExpOp)
      genCode((ExpOp)tree);
    else if(tree instanceof ExpVar)
      genCode((ExpVar)tree, true);
  }

  private void genCode(ExpAssign tree) {
    SymbolInt temp = this.symbolTable.newTemp();
    genCode(tree.lhs, false);
    this.emitCode(++this.address, Operations.ST, AC, temp.getAddress(), FP, "push left");
    if(tree.rhs instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.rhs);
    }
    genCode(tree.rhs);
    this.emitCode(++this.address, Operations.LD, AC1, temp.getAddress(), FP);
    this.emitCode(++this.address, Operations.ST, AC, 0, AC1, "assign: store value");
  }

  private void genCode(ExpCall tree) {
    SymbolFunction s = new SymbolFunction(tree.id, 0, null);
    SymbolFunction match = null;
    try {
      match = (SymbolFunction) this.symbolTable.getMatchingSymbol(s);
      if (tree.args != null) {
        genCode(tree.args, s);
      }
      if (!this.symbolTable.haveMatchingParameters(match, s)) {
        this.symbolTable.error("arguments in function call to " + match.getId() + " on line "
                + tree.pos + " does not match definition");
      }
      this.emitComment("call to function: " + tree.id);
      this.emitCode(++this.address, Operations.ST, FP, symbolTable.getCurrentOffset(), FP, "push ofp");
      this.emitCode(++this.address, Operations.LDA, FP, symbolTable.getCurrentOffset(), FP, "push frame");
      this.emitCode(++this.address, Operations.LDA, AC, 1, PC, "load ac with ret ptr");
      this.emitCode(++this.address, Operations.LDA, PC, match.getAddress() - this.address - 1, PC, "jump to " + match.getId() + " loc");
      this.emitCode(++this.address, Operations.LD, FP, 0, FP, "pop frame");
    }
    catch(Exception e) {
      this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
    }
  }

  private void genCode(ExpVar tree, boolean value) {
    Operations load = value ? Operations.LD : Operations.LDA;
    if(tree.exp == null) { //normal variable
      Symbol s = new SymbolInt(tree.name);
      try {
        Symbol match = this.symbolTable.getMatchingSymbol(s);
        this.emitComment("Looking up id: " + tree.name);
        if (match.isGlobalVar()){
          this.emitCode(++this.address, load, AC, match.getAddress(), GP, "load id");
        } else {
          this.emitCode(++this.address, load, AC, match.getAddress(), FP, "load id");
        }
      }
      catch(InvalidTypeException e) {
        //Arrays can be used without brackets in some cases
        //i.e. int foo(int arr[]) ...  int a[10]; foo(a);
      } catch (Exception e){
        this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
      }
    } else { //array variable
      Symbol s = new SymbolArray(tree.name);
      try {
        SymbolArray match = (SymbolArray)this.symbolTable.getMatchingSymbol(s);
        if(tree.exp instanceof ExpCall){
          this.symbolTable.checkType((ExpCall)tree.exp);
        }
        this.emitComment("Looking up id: " + tree.name);
        this.emitComment("generating index");
        genCode(tree.exp);
        this.emitCode(++this.address, Operations.LD, AC1, match.getAddress(), FP, "top of array");
        this.emitCode(++this.address, Operations.ADD, AC, AC1, AC);
        this.emitCode(++this.address, load, AC, 0, AC, "load id");
      }
      catch(Exception e) {
        this.symbolTable.error(e.getMessage() + ": on line " + (tree.pos + 1));
      }
    }
  }

  private void genCode(ExpOp tree) {
    SymbolInt temp;
    if (tree.left instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.left);
    }
    genCode(tree.left);
    temp = this.symbolTable.newTemp();
    this.emitCode(++this.address, Operations.ST, AC, temp.getAddress(), FP, "push left");
    if (tree.right instanceof ExpCall){
      this.symbolTable.checkType((ExpCall)tree.right);
    }
    genCode(tree.right);
    this.emitCode(++this.address, Operations.LD, AC1, temp.getAddress(), FP, "load left");
    switch(tree.op) {
      case ExpOp.PLUS:
        this.emitCode(++this.address, Operations.ADD, AC, AC1, AC);
        break;
      case ExpOp.MINUS:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        break;
      case ExpOp.TIMES:
        this.emitCode(++this.address, Operations.MUL, AC, AC1, AC);
        break;
      case ExpOp.OVER:
        this.emitCode(++this.address, Operations.DIV, AC, AC1, AC);
        break;
      case ExpOp.LT:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JLT, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      case ExpOp.LTEQ:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JLE, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      case ExpOp.GT:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JGT, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      case ExpOp.GTEQ:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JGE, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      case ExpOp.EQ:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JEQ, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      case ExpOp.NOTEQ:
        this.emitCode(++this.address, Operations.SUB, AC, AC1, AC);
        this.emitCode(++this.address, Operations.JNE, AC, 2, PC, "br if true");
        this.emitCode(++this.address, Operations.LDC, AC, 0, 0, "false case");
        this.emitCode(++this.address, Operations.LDA, PC, 1, PC, "unconditional jump");
        this.emitCode(++this.address, Operations.LDC, AC, 1, 0, "true case");
        break;
      default:
        System.out.println("Unrecognized operator at line " + (tree.pos + 1));
    }
  }

  private void genCode(ExpConst tree) {
    this.emitCode(++this.address, Operations.LDC, AC, tree.val, 0, "load constant");
  }
}