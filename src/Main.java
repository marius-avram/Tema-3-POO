import java.io.*;
import parsetree.Node;
import visitors.*;

/**
 * Clasa contine metoda main. Au loc prelucrari simple de deschidere a unor fisiere
 * si de apelare a metodelor accept din Node si clasele extinse din acesta.
 * @author Marius Avram
 *
 */
public class Main {
	
	public static void main (String[] args) {
	
		String numeFisierIntrare = args[0];
		File fisierIntrare = new File(numeFisierIntrare);
		
		Node nod = new Node(); 
		try {
			nod = nod.constructieArbore(fisierIntrare);
		} catch (FileNotFoundException e ) { 
			//Fisierul nu a fost gasit
		}
		
		// Creeaza Parse Tree si scrie-l in fisierul prestabilit
		String numeFisierParseTree = numeFisierIntrare + "_pt";
		PrintWriter out = null;
		try { 
			out = new PrintWriter(numeFisierParseTree);
			out.write(nod.toString());
		} catch (IOException e) { 
			System.out.print("Problema cu fisierul de iesire _pt");
		} finally { 
			if (out != null) { 
				out.close();
			}
		}
		
		// Afiseaza erorile de limbaj in cazul in ca exista vreuna
		out = null;
		String numeFisierSemanticVisitor = numeFisierIntrare + "_sv";
		SemanticVisitor semanticVisitor = null;
		try { 
			out = new PrintWriter(numeFisierSemanticVisitor);
			semanticVisitor = new SemanticVisitor(out);
			nod.accept(semanticVisitor);
		} catch (IOException e) { 
			System.out.println("Problema cu fisierul de iesire _sv");
		} finally { 
			if (out != null) { 
				out.close();
			}
		}//Termina apel SemanticVisitor
		
		// Doar in cazul in care nu au existat erori la pasul anterior vom folosi 
		// ResultVisitor
		out = null;
		String numeFisierResultVisitor = numeFisierIntrare + "_rv";
		ResultVisitor resultVisitor;
		if (!semanticVisitor.hasErors()) {
			try { 
				out = new PrintWriter(numeFisierResultVisitor);
				resultVisitor = new ResultVisitor(out);
				nod.accept(resultVisitor);
				resultVisitor.display();
			} catch (IOException e) { 
				System.out.println("Problema cu fisierul de iesire _sv");
			} finally { 
				if (out != null) { 
					out.close();
				}
			}
	
		}//Termina apel ResultVisitor
		
	
	}
	
}
