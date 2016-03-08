package absyn;

public class Params extends Absyn {
  public ParamList param_list;
  public boolean isVoidParams;
  public Params(int pos, ParamList param_list) {
    this.param_list = param_list;
    this.isVoidParams = false;
    if(this.param_list == null){
    	this.isVoidParams = true;
    }
  }
}
