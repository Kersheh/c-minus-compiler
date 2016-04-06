package symb;

public abstract class Symbol {
  private String id;
  private int address;
  public static final String INT_TYPE = "INT";
  public static final String FUNC_TYPE = "FUNC";

  public Symbol(String id) {
    this.id = id;
    this.address = 0;
  }

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

  public void setAddress(int address){
    this.address = address;
  }

  abstract public String getType();
}