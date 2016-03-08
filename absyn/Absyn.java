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
}

//   static public void showTree(ExpList tree, int spaces) {
//     while(tree != null) {
//       showTree(tree.head, spaces);
//       tree = tree.tail;
//     } 
//   }

//   static private void showTree(Exp tree, int spaces) {
//     if(tree instanceof AssignExp)
//       showTree((AssignExp)tree, spaces);
//     else if(tree instanceof IfExp)
//       showTree((IfExp)tree, spaces);
//     else if(tree instanceof IntExp)
//       showTree((IntExp)tree, spaces);
//     else if(tree instanceof OpExp)
//       showTree((OpExp)tree, spaces);
//     else if(tree instanceof VarExp)
//       showTree((VarExp)tree, spaces);
//     else if(tree instanceof ReturnExp)
//       showTree((ReturnExp)tree, spaces);
//     else {
//       indent(spaces);
//       System.out.println("Illegal expression at line " + tree.pos);
//     }
//   }

//   static private void showTree(AssignExp tree, int spaces) {
//     indent(spaces);
//     System.out.println("AssignExp:");
//     spaces += SPACES;
//     showTree(tree.lhs, spaces);
//     showTree(tree.rhs, spaces);
//   }

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
