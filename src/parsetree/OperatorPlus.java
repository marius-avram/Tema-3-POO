package parsetree;

import visitors.Visitor;

/**
 * Clasa pentru operatorul Plus.
 * @author Marius Avram
 *
 */
public class OperatorPlus extends Operator {
	
	public OperatorPlus(){ 
		super('+');
		this.setPrioritate(1);
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}
	
}
