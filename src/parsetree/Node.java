package parsetree;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import visitors.Visitor;

/**
 * Clasa reprezinta un token general din limbajul nostru. Ea va fi folosita pentru 
 * instantierea radacinii generice si din ea va porni intreg arborele care contine 
 * Noduri particulare spefice fiecarui tip de token. Clasa implementeaza Visitable
 * (accepta visitarea unui Visitor).
 * @author Marius Avram
 *
 */
public class Node implements Visitable {
	
	private char valoare; 
	private List<Node> copii; 
	
	/**
	 * Constructor de baza. Se initializeaza valoare cu 'r' (radacina generica).
	 */
	public Node() { 
		
		this('r');
	}
	
	/**
	 * Constructor
	 * @param valoare valoarea tokenului
	 */
	public Node(char valoare){ 
		this.setValoare(valoare); 
		this.setCopii(new ArrayList<Node>());
	}

	
	/**
	 * returneaza valoarea. Am ales sa returneze un String pentru ca anumite clase 
	 * care deriva din Node vor avea valoarea compusa din mai multe caractere. In acest 
	 * mod nu va fi nevoie sa fie folosit cheia instanceof.
	 * @return valoare 
	 */
	public String getValoare() {
		
		return Character.toString(this.valoare);
	}

	/**
	 * Setter valoare.
	 * @param valoare noua valoare a tokenului
	 */
	public void setValoare(char valoare) {
		
		this.valoare = valoare;
	}

	
	/**
	 * Getter lista copii
	 * @return lista copii
	 */
	public List<Node> getCopii() {
		return copii;
	}

	/**
	 * Setter lista copii
	 * @param copii noua lista copii
	 */
	public void setCopii(List<Node> copii) {
		this.copii = copii;
	}
	
	/**
	 * Adauga un nou copil in lista de copii.
	 * @param copil
	 */
	public void addCopil(Node copil) { 
		this.copii.add(copil);
	}
	
	/**
	 * Dimensiunea nodului (include un spatiu). Metoda va fi suprascrisa in functie 
	 * de felul in care variaza anumiti tokeni. De exemplu Integer poate varia in functie
	 * de numarul de cifre iar boolean poate fi 4 sau 5. Se foloseste pentru determinarea 
	 * coloanei cu ajutorul arborelui de parsare.
	 * @return dimensiunea nodului
	 */
	public int getDimNode() { 
		return 2;
	}
	
	/**
	 * Dimensiunea arborelui care porneste din nodul curent. Foloseste 
	 * metoda getDimNode() pentru fiecare nod in parte. Se foloseste pentru determinarea 
	 * coloanei cu ajutorul arborelui de parsare.
	 * @return dimensiunea arborelui
	 */
	public int getDimArbore(){ 
		int dim = this.getDimNode();
		for (int i=0; i<this.getCopii().size(); i++){ 
			dim += this.getCopii().get(i).getDimArbore();
		}
		return dim;
	}

	public void accept(Visitor visitor) { 
		visitor.visit(this);
	}
	
	/**
	 * Creeaza o instanta de Node(Variabila, Valoare, Operator, etc)
	 * in functie de tokenul dat. De exemplu daca tokenul va fi un caracter
	 * de la a la z atunci metoda va returna o instanta de Variabila.
	 * @param token token parsat de clasa Scanner
	 * @return instanta  creeata
	 */
	public Node creeazaNodSpecific(String token){ 
		char ctoken = token.charAt(0);
		int inttoken; 
		if (ctoken == '=') { 
			//System.out.println("egal");
			return new OperatorEgal();
		}
		else if (ctoken == '+') { 
			//System.out.println("plus");
			//System.out.println(":::" + new OperatorPlus().getValoare() );
			return new OperatorPlus();
		}
		else if ( ctoken == '*') { 
			//System.out.println("inmultit");
			return new OperatorInmultit();
		}
		else if ( token.compareTo("true") == 0 ) { 
			//System.out.println("bool");
			return new ValoareBool(true);
		}
		else if ( token.compareTo("false") == 0) {
			//System.out.println("booL");
			return new ValoareBool(false);
		}
		else if ( ctoken >= 'a' &&  ctoken <= 'z') { 
			//System.out.println("var");
			return new Variabila(ctoken);
		}

		try { 
			//System.out.println("int");
			inttoken = Integer.parseInt(token);
			return new ValoareInt(inttoken);
		} catch ( NumberFormatException e ) { 
			// Nu se poate parsa un integer din tokenul dat
		}
		return null;
	}
	
	/**
	 * Construieste arborele de parsare.
	 * @param fisier fisierul de intrare in care se afla codul
	 * @return Nodul radacina al arborelui format in urma parsarii codului
	 * @throws FileNotFoundException
	 */
	public Node constructieArbore(File fisier) throws FileNotFoundException { 
		Scanner citire = new Scanner(fisier);
		Scanner citireLinie;
		Stack<Node> operanzi = new Stack<Node>();
		Stack<Node> operanziInv = new Stack<Node>();
		Stack<Operator> operatori = new Stack<Operator>();
		Node nod, rez1, rez2;
		Operator op, op2;
		String token,linie;
		while (citire.hasNextLine()){ 
			
			linie = citire.nextLine();
			citireLinie = new Scanner(linie);
	
			while (citireLinie.hasNext()) { 
				
				token = citireLinie.next();
		
				nod = creeazaNodSpecific(token);
				// Aici incepe algorimul Constructie Parse Tree
				if ( nod instanceof Valoare || nod instanceof Variabila) { 
					operanzi.push(nod);
				}
				else if ( nod instanceof Operator  ) { 
			
					op = (Operator) nod;
					while ( op != null ) {
						if (operatori.isEmpty()) { 
							operatori.push(op);
							op = null;
						}
						else { 
							
							
								if (op.getPrioritate() >= operatori.peek().getPrioritate()) { 
								//daca operatorul curent are prioritate mai mare dacat elmentul din 
								//varful stivei
									operatori.push(op);
									op = null;
								}
								else if (op.getPrioritate() < operatori.peek().getPrioritate()) { 
								//daca operatorul curent are prioritate mai mica decat elementul din 
								//varful stivei
									rez1 = operanzi.pop();
									rez2 = operanzi.pop();
									op2 = operatori.pop();
									op2.addCopil(rez2);
									op2.addCopil(rez1);
									operanzi.add(op2);
								}
						}	
					}
				}
				
			}
			
			// Golim stiva cu operatori in cazul in care mai contine ceva 
			// pentru a efectua toate operatiile si a construi arbore de 
			// parsare pana la capat
			while (!operatori.isEmpty()){ 
				rez1 = operanzi.pop();
				rez2 = operanzi.pop();
				op = operatori.pop();
				op.addCopil(rez2);
				op.addCopil(rez1);
				operanzi.add(op);
			}
		
		}
		
		// In cazul in care exista mai multe linii toate componentele trebuie sa se 
		// afle in acelasi arbore de aceea le vom lega cu un nod comun 'r' - radacina generica
		Node radacina = new Node();
		// Mai intai intoarcem in ordine inversa operanzii
		while (!operanzi.isEmpty()){ 
			operanziInv.push(operanzi.pop());
		}
		while (!operanziInv.isEmpty()) { 
			radacina.addCopil(operanziInv.pop());
		}

		return radacina;
	}
	
	
	/**
	 * Metoda recursiva de afisare. Este o metoda auxiliara folosita de display.
	 * @param radacina nodul din care porneste afisarea
	 * @param tab numarul de taburi fata de margine
	 * @return un String ce contine arborele afisat cu taburi, fiecare nod pe o linie
	 */
	public String Afisare(Node radacina, int tab) { 
		
		String rezultat = radacina.getValoare() + System.lineSeparator();
		for (int i=0; i<radacina.getCopii().size(); i++){ 
			for (int j=0; j<tab; j++){
				rezultat += "\t";
			}
			rezultat += Afisare(radacina.getCopii().get(i),tab+1);
		}
		return rezultat;
	}
	
	/**
	 * Afiseaza arborele din nodul curent.
	 */
	public void display(){ 
		System.out.println(Afisare(this,1).trim());
	}
	
	public String toString(){
		return Afisare(this,1).trim();

	}
	
	
}
