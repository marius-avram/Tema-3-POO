package visitors;
import parsetree.*;

/**
 * Interfata Visitor(Vizitator) contine definitiile metodelor visit ce primesc
 * ca parametru un Node sau o clasa extinsa din aceasta (OperatorEgal,
 * OperatorInmultit, OperatorPlus, etc).
 * @author Marius Avram
 *
 */
public interface Visitor {
	
	void visit(Node nod);
	
	void visit(OperatorEgal nod);
	
	void visit(OperatorInmultit nod);
	
	public void visit(OperatorPlus nod);
	
	public void visit (ValoareBool nod);

	public void visit (ValoareInt nod);
	
	public void visit(Variabila nod);
	
}
