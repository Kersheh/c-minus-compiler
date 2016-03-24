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
      System.out.println("Illegal declaration");
    }
  }

  static private void showTree(DeclarVar tree, int spaces) {
    if(tree == null) {
      return;
    }
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
    else if(tree instanceof StmtWhile)
      showTree((StmtWhile)tree, spaces);
    else if(tree instanceof StmtReturn)
      showTree((StmtReturn)tree, spaces);
    else {
      indent(spaces);
      System.out.println("Illegal statement");
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
    if (tree.else_stmt != null)
      showTree(tree.else_stmt, spaces);
  }

  static private void showTree(StmtWhile tree, int spaces) {
    indent(spaces);
    System.out.println("StmtWhile:");
    spaces += SPACES;
    showTree(tree.test, spaces);
    showTree(tree.stmt, spaces);
  }

  static private void showTree(StmtReturn tree, int spaces) {
    indent(spaces);
    System.out.println("StmtReturn:");
    spaces += SPACES;
    if (tree.item != null)
      showTree(tree.item, spaces);
    else{
      indent(spaces);
      System.out.println("No Return Value");
    }
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
      System.out.println("Illegal expression at line " + (tree.pos + 1));
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
    if(tree.args != null)
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
      System.out.println("ExpVar: " + tree.name + "[]");
      spaces += SPACES;
      showTree(tree.exp, spaces);
    }
  }

  static private void showTree(ExpOp tree, int spaces) {
    indent(spaces);
    System.out.println("ExpOp: ");
    spaces += SPACES;
    showTree(tree.left, spaces);
    indent(spaces);
    System.out.print("Operator: ");
    switch(tree.op) {
      case ExpOp.PLUS:
        System.out.println(" + ");
        break;
      case ExpOp.MINUS:
        System.out.println(" - ");
        break;
      case ExpOp.TIMES:
        System.out.println(" * ");
        break;
      case ExpOp.OVER:
        System.out.println(" / ");
        break;
      case ExpOp.LT:
        System.out.println(" < ");
        break;
      case ExpOp.LTEQ:
        System.out.println(" <= ");
        break;
      case ExpOp.GT:
        System.out.println(" > ");
        break;
      case ExpOp.GTEQ:
        System.out.println(" >= ");
        break;
      case ExpOp.EQ:
        System.out.println(" == ");
        break;
      case ExpOp.NOTEQ:
        System.out.println(" != ");
        break;
      default:
        System.out.println("Unrecognized operator at line " + (tree.pos + 1));
    }
    showTree(tree.right, spaces);
  }
}
