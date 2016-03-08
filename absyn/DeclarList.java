package absyn;

public class DeclarList {
  public Declar head;
  public DeclarList tail;
  public DeclarList(Declar head, DeclarList tail) {
    this.head = head;
    this.tail = tail;
  }
}
