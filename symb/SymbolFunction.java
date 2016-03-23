package symb;
import java.util.List;

public class SymbolFunction extends Symbol {
  
  private List<SymbolInt> parameters;

  public SymbolFunction(String id, int addr, List<SymbolInt> parameters) {
    super(id, addr);
    this.parameters = parameters;
  }

  @Override
  public String getType(){
    return Symbol.FUNC_TYPE;
  }

  public List<SymbolInt> getParameters(){
    return this.parameters;
  }
}