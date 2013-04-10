package visitors;
import java.util.*;
import java.io.*;
import parsetree.*;

enum evalexpresie { 
	Integer, Boolean, Nedefinit, NedefinitSemnalat, Incompatibil
}

/**
 * O implementare a interfetei Visitor(Vizitator). Evalueaza corectitudinea semantica a 
 * expresiilor.
 * @author Marius Avram
 *
 */
public class SemanticVisitor implements Visitor {
	
	private Map<Character , evalexpresie> variabile ;
	private PrintWriter out;
	private evalexpresie tip;
	private int linie, coloana;
	private int eroare;
	
	
	/**
	 * Constructorul de baza. In acest caz nu primeste ca parametru un PrintWriter,
	 * de aceea va face afisarile in consola. Foloseste PrintWriter ca decorator 
	 * pentru System.out. 
	 * 
	 */
	public SemanticVisitor(){ 
		
		variabile = new HashMap<Character, evalexpresie>();
		out = new PrintWriter(System.out,true);
	}
	
	/**
	 * Constructor.
	 * @param out PrintWriter-ul catre care va directiona afisarile (va contine
	 * fisierul de iesire).
	 */
	public SemanticVisitor(PrintWriter out){ 
		variabile = new HashMap<Character, evalexpresie>();
		this.out = out;
	}
	
	/**
	 * Determina coloana pe care se afla nodul, pentru un nod care este 
	 * descendentul stang al unui nod parinte. Coloana si linia pe care se afla 
	 * un Node este afla din constructia arborelui de parsare.
	 * @param nod nodul stang; nu primeste nodul parinte 
	 * @return coloana pe care se afla nodul primit ca parametru
	 */
	private int indiceColoanaStang(Node nod) { 
		 
		if ( !nod.getCopii().isEmpty()) { 
			// Daca are succesori
			coloana -= nod.getCopii().get(1).getDimArbore() + 2 ;
		} else {
			// Daca nu are succesori
			coloana-=2;
		}
		return coloana;
	}
	
	/**
	 * Determina coloana pe care se afla nodul, pentru un nod care este 
	 * descendentul drept al unui nod parinte. Coloana si linia pe care se afla 
	 * un Node este afla din constructia arborelui de parsare.
	 * @param nod nodul drept; nu primeste nodul parinte 
	 * @return coloana pe care se afla nodul primit ca parametru
	 */
	private int indiceColoanaDrept(Node nod) { 
		
		if ( !nod.getCopii().isEmpty()) { 
			// Daca are succesori
			coloana += nod.getCopii().get(0).getDimArbore() + 2 ;
		} else { 
			// Daca nu are succesori
			coloana+=2;
		}
		return coloana;
	}
	
	/**
	 * Va apela metoda accept pentru toti fiii radacinii generice 'r'.
	 */
	
	public void visit(Node nod){
		Node fiu;
		if (nod.getValoare().compareTo("r") == 0) { 
			for (int i=0; i<nod.getCopii().size(); i++) { 
				// Linia fiului corespunde cu indicele nodului din lista de noduri al 
				// radacinii generice
				linie = i+1;
				fiu = nod.getCopii().get(i);
				// Coloana corespunde dimensiunii arborelui stanga (din stanga egalului) 
				// +1 pentru ca numaratoare incepe de la 0.
				coloana = fiu.getCopii().get(0).getDimArbore()+1;
				// Viziteaza fiul
				fiu.accept(this);
			}
		}
	}
	
	/**
	 * Verifica daca in stanga operatorului egal este o variabila. In cazul in care nu este 
	 * afiseaza un mesaj de eroare. Face verificari si in partea dreapta a egalului. Ele 
	 * nu sunt tratate la acest nivel ci se intra in recursivitate.
	 */
	public void visit(OperatorEgal nod) { 
		
		if (nod.getCopii().get(0) instanceof Valoare){ 
			coloana-=nod.getCopii().get(0).getDimNode();
			eroare++;
			out.println("membrul stang nu este o variabila la linia " + linie + " coloana " + coloana);
			coloana+=nod.getCopii().get(0).getDimNode();
		} 
		
		// cand se intalneste un egal trebuie ca tipul expresiei sa fie resetat
		tip = evalexpresie.Nedefinit;
		// se viziteaza termenul stang 
		indiceColoanaDrept(nod.getCopii().get(1));
		nod.getCopii().get(1).accept(this);
		// in cazul in care tipul termenului stang este definit 
		// vom introduce variabila in hashmap
		if (tip !=evalexpresie.NedefinitSemnalat && tip != evalexpresie.Incompatibil) { 
			variabile.put(nod.getCopii().get(0).getValoare().charAt(0), tip);
		}
		
	}
	
	/**
	 * Metoda auxiliara folosita atat in cazul inmultirii cat si adunarii. 
	 * Verifica daca tipurile fiilor sunt de acelasi tip. In cazul in care nu sunt 
	 * se afiseaza mesaj de eroare.
	 * @param nod Node ce poate fi de tip OperatorInmultit sau OperatorPlus
	 * @param operatie tipul operatiei. De obicei '+' sau '*'. Caracterul este folosit la 
	 * afisarea mesajului de eroare.
	 */
	public void visitInmultitsiPlus(Node nod, char operatie)  { 
		
		evalexpresie varStanga, varDreapta;
		int indiceColoanaOld = coloana;
		
		indiceColoanaStang(nod.getCopii().get(0));
		nod.getCopii().get(0).accept(this);
		// Memoram tipul expresiei din stanga
		varStanga = tip;
		
		coloana = indiceColoanaOld;
		indiceColoanaDrept(nod.getCopii().get(1));
		nod.getCopii().get(1).accept(this);
		// Memoram tipul expresiei din dreapta
		varDreapta = tip;
		
		coloana = indiceColoanaOld;
		if (varStanga == evalexpresie.Nedefinit || varDreapta == evalexpresie.Nedefinit) { 
			// Le comparam sa vedem daca nu cumva exista o variabila nedefinita
			tip = evalexpresie.Nedefinit;
		}
		else if ((varStanga == evalexpresie.Integer && varDreapta == evalexpresie.Boolean) || 
				 (varStanga == evalexpresie.Boolean && varDreapta == evalexpresie.Integer)) { 
			// Le comparam se vedem daca nu cumva exista tipuri incompatibile.
			// Exemplu: Cazul in care avem Boolean si Integer in aceasi expresie.
			// Nu se va mai afisa mesajul daca deja sa afisat unul asemanator
			tip = evalexpresie.Incompatibil;
			eroare++;
			out.println(operatie + " intre tipuri incompatibile la linia " + linie + " coloana " + coloana);
		} 
	}
	
	/**
	 * Verifica compatibilitatea termenilor implicati in operatie.
	 */
	public void visit(OperatorInmultit nod){ 
		// se apeleaza metoda visitInmultitsiPlus pentru ca operatiile efectuate 
		// sunt identice pentru inmultire si adunare
		visitInmultitsiPlus(nod, '*');
	}
	
	/**
	 * Verifica compatibilitatea termenilor implicati in operatie.
	 */
	public void visit(OperatorPlus nod){
		// se apeleaza metoda visitInmultitsiPlus pentru ca operatiile efectuate 
		// sunt identice pentru inmultire si adunare
		visitInmultitsiPlus(nod, '+');
	}
	
	/** 
	 * Transmite la nivel superior tipul termenului (Boolean).
	 */
	public void visit (ValoareBool nod){
		tip = evalexpresie.Boolean;
	}
	
	/** 
	 * Transmite la nivel superior tipul termenului (Integer).
	 */
	public void visit (ValoareInt nod) { 
		tip = evalexpresie.Integer;
	}
	
	/**
	 * Verifica daca variabila este declarata. In cazul in care nu este afiseaza 
	 * un mesaj de atentionare.
	 */
	public void visit(Variabila nod){ 
		
		// Daca ajungem sa vizitam o variabila dorim sa o cautam in hashmap
		// si sa vedem daca exista. In cazul in care nu exista vom da un mesaj de atentionare
		char cvariabila = nod.getValoare().charAt(0); 
		//System.out.println(cvariabila);
		//System.out.println(variabile);

			if (variabile.containsKey(cvariabila)) { 
				// vom seta tipul variabile de la nedefinit la cel specific variabilei
				tip = variabile.get(cvariabila);
			}
			else if( tip != evalexpresie.NedefinitSemnalat ) { 
				// altfel inseamna ca variabila nu este definita ( apare pentru prima data in limbaj)
				tip = evalexpresie.NedefinitSemnalat;
				eroare++;
				out.println(cvariabila + " nedeclarata la linia " + linie + " coloana " + coloana );
			}

	}
	
	
	/**
	 * Determina daca secventa de cod are vreo eroare.
	 * @return True/False daca are sau nu erori.
	 */
	public boolean hasErors() { 
		
		if (this.eroare > 0 ) { 
			return true;
		} else { 
			return false;
		}
	}
	
	
}
