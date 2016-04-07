package symb;

public abstract class Symbol {
  private String id;
  private int address;
  private int scope = -1;
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

  public void setScope(int scope){
    this.scope = scope;
  }

  public int getScope(){
    return this.scope;
  }

  public boolean isGlobalVar(){
    return this.scope == 1 && INT_TYPE.equals(this.getType());
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