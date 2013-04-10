package parsetree;

import visitors.Visitor;

/**
 * Clasa pentru variabile.
 * @author Marius Avram
 *
 */
public class Variabila extends Node {
	
	
	public Variabila(){
		super();
	}
	
	public Variabila(char valoare) { 
		this();
		this.setValoare(valoare);
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}
	
	
}
