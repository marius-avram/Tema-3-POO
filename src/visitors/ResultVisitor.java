package visitors;
import parsetree.*;
import java.util.*;
import java.io.*;

/**
 * O implementare a interfetei Visitor(Vizitator). Evalueaza expresiile si afiseaza valorile 
 * variabilelor in ordine alfabetica.
 * @author Marius Avram
 *
 */
public class ResultVisitor implements Visitor {
	
	private Map<Character, Object> variabile;
	private PrintWriter out;
	private Object valExpresie;
	
	/**
	 * Constructorul de baza. In acest caz nu primeste ca parametru un PrintWriter,
	 * de aceea va face afisarile in consola. Foloseste PrintWriter ca decorator 
	 * pentru System.out. 
	 */
	public ResultVisitor(){ 

		this.variabile = new TreeMap<Character, Object>();
		this.out = new PrintWriter(System.out,true);
	}
	
	/**
	 * Constructor.
	 * @param out PrintWriter-ul catre care va directiona afisarile (va contine
	 * fisierul de iesire).
	 */
	public ResultVisitor(PrintWriter out) { 
		this();
		this.out = out;
	}
	
	
	/**
	 * Efectueaza Operatia'SAU' in functie de tipul variabilelor : Integer sau Boolean.
	 * Dupa caz va efectua sau logic ori o adunare intre intregi.
	 * @param n1 primul termen al operatiei
	 * @param n2 al doilea termen al operatiei
	 * @return rezultatul operatiei. Poate fi intreg sau boolean in functie de parametrii
	 * primiti.In cazul in care parametri sunt incompatibili/nu sunt intregi sau booleeni
	 * returneaza null.
	 */
	public <T> Object operatieSau( T n1, T n2) { 
		Object result;
		if ( (n1 instanceof Boolean) && (n2 instanceof Boolean)){
			result = (Boolean)n1 || (Boolean)n2;
			return (Boolean)result;
		} else if ((n1 instanceof Integer) && (n2 instanceof Integer)){
			result = (Integer)n1 + (Integer)n2;
			return (Integer)result;
		}
		return null;
	}
	
	/**
	 * Efectueaza Operatia 'SI' in functie de tipul variabilelor : Integer sau Boolean.
	 * Dupa caz va efectua si logic ori o inmultire intre intregi.
	 * @param n1 primul termen al operatiei
	 * @param n2 primul termen al operatiei
	 * @return rezultatul operatiei. Poate fi intreg sau boolean in functie de parametrii
	 * primiti.In cazul in care parametri sunt incompatibili/nu sunt intregi sau booleeni
	 * returneaza null.
	 */
	public <T> Object operatieSi( T n1, T n2) { 
		

		Object result;
		if ( (n1 instanceof Boolean) && (n2 instanceof Boolean)){
			result = (Boolean)n1 && (Boolean)n2;
			return (Boolean)result;
		} else if ((n1 instanceof Integer) && (n2 instanceof Integer)){
			result = (Integer)n1 * (Integer)n2;
			return (Integer)result;
		}

		return null;
	}
	
	
	/** 
	 * Va apela metoda accept pentru toti fiii radacinii generice 'r'.
	 */
	public void visit(Node nod){ 
		
		if (nod.getValoare().charAt(0) == 'r') { 
			for (int i=0; i< nod.getCopii().size(); i++){ 
				nod.getCopii().get(i).accept(this);
			}
		}
	}
	
	/**
	 * Daca se intalneste operatorul egal se introduce in map valoarea 
	 * care vine din recursivitate corespunzatoare membrului stang (variabila).
	 * Este posibil ca variabila sa exista deja. In acest caz se va inlocui cu noua valoare
	 */
	public void visit(OperatorEgal nod){ 
		
		// Variabila este copilul stang
		Node variabila = nod.getCopii().get(0); 
		char cvariabila = variabila.getValoare().charAt(0);
		// Se obtine o noua valoare pentru valExpresie care reprezinta 
		// valoarea corespunzatoare variabilei (copilul stang).
		nod.getCopii().get(1).accept(this);
		variabile.put (cvariabila, valExpresie);

	}
	
	/**
	 * Face inmultirea/SI dintre copilul stang si cel drept.
	 */
	public void visit(OperatorInmultit nod){
		
		Object valst;
		// Accesam copilul stang
		nod.getCopii().get(0).accept(this);
		// Retinem valoarea copilului stang
		valst = valExpresie;
		// Accesam copilul drept
		nod.getCopii().get(1).accept(this);
		// Efectueaza operatia
		valExpresie = operatieSi(valst,valExpresie);
	}
	
	/**
	 * Face adunarea/SAU dintre copilul stang si cel drept.
	 */
	public void visit(OperatorPlus nod){
		Object valst ;
		// Accesam copilul stang
		nod.getCopii().get(0).accept(this);
		// Retinem valoarea copilului stang
		valst = valExpresie;
		nod.getCopii().get(1).accept(this);
		// Efectueaza operatia
		valExpresie = operatieSau(valst,valExpresie);
		
	}
	
	/**
	 * Acceseaza un operand Booleean.
	 */
	public void visit (ValoareBool nod){
		// Este necesar doar sa transmitem catre nivelurile superioare 
		// (din recursivitate) noua valoare a operandului.
		valExpresie = Boolean.parseBoolean(nod.getValoare());
	}

	/**
	 * Acceseaza un operand Intreg.
	 */
	public void visit (ValoareInt nod){
		// Este necesar doar sa transmitem catre nivelurile superioare 
		// (din recursivitate) noua valoare a operandului.
		valExpresie = Integer.parseInt(nod.getValoare());
	}
	
	/**
	 * Acceseaza o variabila. Acesta accesare are loc doar pentru o variabila 
	 * deja existenta (din TreeMap), pentru ca vizitam doar termenii din dreapta
	 * operatorului egal. De aceea se va dori sa se afle valoarea acesteia.
	 */
	public void visit(Variabila nod){
		
		char cvariabila = nod.getValoare().charAt(0);
		if ( variabile.containsKey(cvariabila)) { 
			valExpresie = variabile.get(cvariabila);
		}
		else { 
			valExpresie = 0;
		}
	}
	
	/**
	 * Face afisarea in ordine alfabetica a valorilor variabilelor. Nu este nevoie 
	 * de nici o sortare aditionala deoarece s-a folosit TreeMap care sorteaza automat 
	 * elementele in functie de ordinea lor naturala. 
	 */
	public void display(){ 
		
		for ( Map.Entry<Character, Object> entry : this.variabile.entrySet()){ 
			out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	
}
