package symb;

public abstract class Symbol {
  private String id;
  private int address;
  public static final String INT_TYPE = "INT";
  public static final String FUNC_TYPE = "FUNC";

  public Symbol(String id, int address) {
    this.id = id;
    this.address = address;
  }

  public String getId(){
  	return this.id;
  }

  public int getAddress(){
  	return this.address;
  }

  abstract public String getType();
}