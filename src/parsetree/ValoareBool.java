package parsetree;

import visitors.Visitor;

/**
 * Clasa pentru operanzi Booleeni.
 * @author Marius Avram
 *
 */
public class ValoareBool extends Valoare {
	
	private boolean valoareAdevar;
	
	public ValoareBool( boolean valoare) { 
		super();
		this.setValoare(valoare);
	}
	
	public String getValoare() { 
		return Boolean.toString(valoareAdevar);
	}
	
	/**
	 * Setter ce poate primi direct o valoarea booleana.
	 * @param valoare noua valoare
	 */
	public void setValoare( boolean valoare ) { 
		this.valoareAdevar = valoare;
	}
	
	/**
	 * Dimensiunea nodului este diferita fata de celelalte cazuri. Se suprascrie 
	 * metoda din clasa parinte Node.
	 */
	public int getDimNode(){ 
		return Boolean.toString(this.valoareAdevar).length() + 1;
	}
	
	public void accept(Visitor visitor){
		visitor.visit(this);
	}

}
