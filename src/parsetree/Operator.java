package parsetree;

/**
 * Clasa comuna tutoror operatorilor.
 * Contine in plus un camp de prioritate care este 0 pentru egal, 1 pentru + si 
 * 2 pentru *. Campul este folosit in constructia arborelui de parsare.
 * @author Marius Avram
 *
 */
public class Operator extends Node {
	
	private int prioritate; 
	
	public Operator(char valoare){ 
		super();
		this.setValoare(valoare);
	}
	
	/**
	 * Seteaza prioritatea Operatorului
	 * @param prioritate noua prioritate
	 */
	public void setPrioritate(int prioritate){ 
		this.prioritate = prioritate;
	}
	
	/**
	 * Getter cu valoarea prioritatii
	 * @return prioritate
	 */
	public int getPrioritate(){
		return this.prioritate;
	}
	
}
