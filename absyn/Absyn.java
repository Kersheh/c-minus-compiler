package absyn;

abstract public class Absyn {
  public int pos;

  final static int SPACES = 4;

  static private void indent(int spaces) {
    for(int i = 0; i < spaces; i++) System.out.print(" ");
  }

  static public void showTree(DeclarList tree, int spaces) {
    while(tree != null) {
      showTree(tree.head, spaces);
      tree = tree.tail;
    }
  }

  static public void showTree(DeclarListLocal tree, int spaces) {
    while(tree != null) {
      showTree(tree.head, spaces);
      tree = tree.tail;
    }
  }

  static public void showTree(ExpList tree, int spaces) {
    while(tree != null) {
      showTree(tree.head, spaces);
      tree = tree.tail;
    }
  }

  static public void showTree(StmtList tree, int spaces) {
    while(tree != null) {
      showTree(tree.head, spaces);
      tree = tree.tail;
    }
  }

  static public void showTree(ParamList tree, int spaces) {
    while(tree != null) {
      showTree(tree.head, spaces);
      tree = tree.tail;
    }
  }

  static public void showTree(Declar tree, int spaces) {
    if(tree instanceof DeclarVar)
      showTree((DeclarVar)tree, spaces);
    else if(tree instanceof DeclarFun)
      showTree((DeclarFun)tree, spaces);
    else {
      indent(spaces);
      System.out.println("Illegal declaration at line " + tree.pos);
    }
  }

  static private void showTree(DeclarVar tree, int spaces) {
    indent(spaces);
    if (tree.array)
      System.out.println("DeclarVar: " + tree.type.type + " " + tree.name + "[]");
    else
      System.out.println("DeclarVar: " + tree.type.type + " " + tree.name );
  }

  static private void showTree(DeclarFun tree, int spaces) {
    indent(spaces);
    System.out.println("DeclarFun: " + tree.type.type + " " + tree.name);
    spaces += SPACES;
    showTree(tree.params, spaces);
    showTree(tree.stmt, spaces);
  }

  static private void showTree(Params tree, int spaces) {
    indent(spaces);
    System.out.println("Params:");
    spaces += SPACES;
    if(tree.isVoidParams){
      indent(spaces);
      System.out.println("VOID");
    }
    else{
      showTree(tree.param_list, spaces);
    }
  }

  static private void showTree(Param tree, int spaces) {
    indent(spaces);
    if (tree.array)
      System.out.println("Param: " + tree.type.type + " " + tree.id + "[]");
    else
      System.out.println("Param: " + tree.type.type + " " + tree.id );
  }

  static private void showTree(Stmt tree, int spaces) {
    if(tree instanceof StmtComp)
      showTree((StmtComp)tree, spaces);
    else if(tree instanceof StmtExp)
      showTree((StmtExp)tree, spaces);
    else if(tree instanceof StmtSelect)
      showTree((StmtSelect)tree, spaces);
    //else if(tree instanceof StmtWhile)
    //  showTree((StmtWhile)tree, spaces);
    else if(tree instanceof StmtReturn)
      showTree((StmtWhile)tree, spaces);
    else {
      indent(spaces);
      System.out.println("Illegal statement at line " + tree.pos);
    }
  }

  static private void showTree(StmtComp tree, int spaces) {
    indent(spaces);
    System.out.println("StmtComp:");
    spaces += SPACES;
    showTree(tree.declar_local, spaces);
    showTree(tree.stmt_list, spaces);
  }

  static private void showTree(StmtExp tree, int spaces) {
    indent(spaces);
    System.out.println("StmtExp:");
    spaces += SPACES;
    showTree(tree.exp, spaces);
  }

  static private void showTree(StmtSelect tree, int spaces) {
    indent(spaces);
    System.out.println("StmtSelect:");
    spaces += SPACES;
    showTree(tree.test, spaces);
    showTree(tree.then_stmt, spaces);
    showTree(tree.else_stmt, spaces);
  }

  static private void showTree(StmtReturn tree, int spaces) {
    indent(spaces);
    System.out.println("StmtReturn:");
    spaces += SPACES;
    showTree(tree.item, spaces);
  }

  static public void showTree(Exp tree, int spaces) {
    if(tree instanceof ExpAssign)
      showTree((ExpAssign)tree, spaces);
    else if(tree instanceof ExpCall)
      showTree((ExpCall)tree, spaces);
    else if(tree instanceof ExpConst)
      showTree((ExpConst)tree, spaces);
    else if(tree instanceof ExpOp)
      showTree((ExpOp)tree, spaces);
    else if(tree instanceof ExpVar)
      showTree((ExpVar)tree, spaces);
    else {
      indent(spaces);
      System.out.println("Illegal expression at line " + tree.pos);
    }
  }

  static private void showTree(ExpAssign tree, int spaces) {
    indent(spaces);
    System.out.println("ExpAssign:");
    spaces += SPACES;
    showTree(tree.lhs, spaces);
    showTree(tree.rhs, spaces);
  }

  static private void showTree(ExpCall tree, int spaces) {
    indent(spaces);
    System.out.println("ExpCall: " + tree.id);
    spaces += SPACES;
    showTree(tree.args, spaces);
  }

  static private void showTree(ExpConst tree, int spaces) {
    indent(spaces);
    System.out.println("ExpConst: " + tree.val);
  }

  static private void showTree(ExpVar tree, int spaces) {
    indent(spaces);
    if(tree.exp == null)
      System.out.println("ExpVar: " + tree.name);
    else{
      System.out.println("ExpVar: " + tree.name);
      spaces += SPACES;
      showTree(tree.exp, spaces);
    }
  }

  static private void showTree(ExpOp tree, int spaces) {
    indent(spaces);
    System.out.println("ExpOp: ");
  }


//   static private void showTree(IfExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("IfExp:");
//     spaces += SPACES;
//     showTree(tree.test, spaces);
//     showTree(tree.thenpart, spaces);
//     showTree(tree.elsepart, spaces);
//   }

//   static private void showTree(IntExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("IntExp: " + tree.value); 
//   }

//   static private void showTree(OpExp tree, int spaces) {
//     indent(spaces);
//     System.out.print("OpExp:"); 
//     switch(tree.op) {
//       case OpExp.PLUS:
//         System.out.println(" + ");
//         break;
//       case OpExp.MINUS:
//         System.out.println(" - ");
//         break;
//       case OpExp.TIMES:
//         System.out.println(" * ");
//         break;
//       case OpExp.OVER:
//         System.out.println(" / ");
//         break;
//       case OpExp.LT:
//         System.out.println(" < ");
//         break;
//       case OpExp.LTEQ:
//         System.out.println(" <= ");
//         break;
//       case OpExp.GT:
//         System.out.println(" > ");
//         break;
//       case OpExp.GTEQ:
//         System.out.println(" >= ");
//         break;
//       case OpExp.EQ:
//         System.out.println(" == ");
//         break;
//       case OpExp.NOTEQ:
//         System.out.println(" != ");
//         break;
//       default:
//         System.out.println("Unrecognized operator at line " + tree.pos);
//     }
//     spaces += SPACES;
//     showTree(tree.left, spaces);
//     showTree(tree.right, spaces); 
//   }

//   static private void showTree(VarExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("VarExp: " + tree.name);
//   }

//   static private void showTree(WhileExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("WhileExp:");
//     spaces += SPACES;
//     showTree(tree.test, spaces);
//     showTree(tree.exps, spaces);
//   }

//   static private void showTree(InputExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("InputExp:");
//     showTree(tree.input, spaces + SPACES);  
//   }

//   static private void showTree(OutputExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("OutputExp:");
//     showTree(tree.output, spaces + SPACES); 
//   }
// }
}
