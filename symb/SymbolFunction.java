package symb;
import java.util.ArrayList;
import java.util.List;

public class SymbolFunction extends Symbol {
  
  private List<SymbolInt> parameters;
  private List<SymbolInt> localDeclarations;
  private String returnType;

  public SymbolFunction(String id, String returnType) {
    super(id);
    this.returnType = returnType;
  }

  public SymbolFunction(String id, int addr, String returnType) {
    super(id, addr);
    this.returnType = returnType;
  }

  @Override
  public String getType(){
    return Symbol.FUNC_TYPE;
  }

  public String getReturnType(){
    return this.returnType;
  }

  public List<SymbolInt> getParameters(){
    return this.parameters;
  }

  public void addParameter(SymbolInt s){
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(s);
  }

  public List<SymbolInt> getLocalDeclarations(){
    return this.parameters;
  }

  public void addLocalDeclaration(SymbolInt s){
    if (this.localDeclarations == null) {
      this.localDeclarations = new ArrayList<>();
    }
    this.localDeclarations.add(s);
  }

  @Override
  public String toString(){
    String s = "Function: " + this.returnType.toLowerCase() + " " + this.getId();
    if (this.parameters == null) {
      s = s + "( void )";
    } else {
      s = s + " (";
      for(SymbolInt i : this.parameters){
        s = s + " " + i.toString();
      }
      s = s + " )";
    }
    return s;
  }

}