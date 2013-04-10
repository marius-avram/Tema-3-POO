package parsetree;

import visitors.Visitor;

/**
 * Contine metoda accept care va fi implementata de Node si descendentii sai.
 * @author Marius Avram
 *
 */
public interface Visitable {
	
	void accept (Visitor visitor);

}
