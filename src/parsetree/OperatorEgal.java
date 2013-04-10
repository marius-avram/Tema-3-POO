package parsetree;
import visitors.*;

/**
 * Clasa pentru operatorul egal.
 * @author Marius Avram
 *
 */
public class OperatorEgal extends Operator {
	
	public OperatorEgal(){ 
		super('=');
		this.setPrioritate(0);
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}
	
	
}
