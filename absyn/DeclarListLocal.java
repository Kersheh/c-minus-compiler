package absyn;

public class DeclarListLocal {
  public DeclarVar head;
  public DeclarListLocal tail;
  public DeclarListLocal(DeclarVar head, DeclarListLocal tail) {
    this.head = head;
    this.tail = tail;
  }
}
