package parsetree;

import visitors.Visitor;

/**
 * Clasa pentru operatorul Inmultit.
 * @author Marius Avram
 *
 */
public class OperatorInmultit extends Operator {
	
	public OperatorInmultit(){ 
		super('*');
		this.setPrioritate(2);
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}
	
}
