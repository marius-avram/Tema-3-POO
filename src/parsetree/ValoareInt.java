package parsetree;

import visitors.Visitor;

/**
 * Clasa pentru operanzi intregi.
 * @author Marius Avram
 *
 */
public class ValoareInt extends Valoare {
	
	private int valoareIntreaga;
	
	public ValoareInt(int valoare) { 
		super();
		this.setValoare(valoare);
	}
	
	/**
	 * Setter ce poate primi direct un Intreg
	 * @param valoare noua valoare
	 */
	public void setValoare( int valoare){ 
		this.valoareIntreaga = valoare;
	}
	
	public String getValoare(){ 
		return Integer.toString(valoareIntreaga);
	}
	
	/**
	 * Se suprascrie metoda din clasa parinte Node pentru ca dimensiunea variaza in functie 
	 * de numarul de cifre al intregului.
	 */
	public int getDimNode(){
		String valToString = "" + this.valoareIntreaga;
		return valToString.length() + 1 ;
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}

}
