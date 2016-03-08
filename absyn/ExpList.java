package absyn;

public class ExpList {
  public ExpList head;
  public Exp tail;
  public ExpList(ExpList head, Exp tail) {
    this.head = head;
    this.tail = tail;
  }
}
