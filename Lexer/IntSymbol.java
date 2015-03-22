

// This is a project skeleton file for PA5, but a regular support code
// for other assignments.

import java.io.PrintStream;

/** String table entry for integer constants
 * 
 * @see AbstractSymbol
 * */
class IntSymbol extends AbstractSymbol {
    /* Creates a new symbol.
     * 
     * @see AbstractSymbol
     * */
    public IntSymbol(String str, int len, int index) {
	super(str, len, index);
    }

    /** Generates code for the integer constant definition.  This method
     * is incomplete; you get to finish it up in programming assignment
     * 5.
     * @param intclasstag the class tag for string object
     * @param s the output stream
     *
     * */
    public void codeDef(int intclasstag, PrintStream s) {
	// Add -1 eye catcher
	s.println(CgenSupport.WORD + "-1");
	codeRef(s); s.print(CgenSupport.LABEL); // label
	s.println(CgenSupport.WORD + intclasstag); // tag
	s.println(CgenSupport.WORD + (CgenSupport.DEFAULT_OBJFIELDS + 
				      CgenSupport.INT_SLOTS)); // size
	s.print(CgenSupport.WORD);

	/* Add code to reference the dispatch table for class Int here */

	s.println("");		// dispatch table
	s.println(CgenSupport.WORD + str); // integer value
    }

    /** Emits a reference to this integer constant.
     * @param s the output stream
     * */
    public void codeRef(PrintStream s) {
	s.print(CgenSupport.INTCONST_PREFIX + index);
    }

    /** Returns a copy of this symbol */
    public Object clone() {
	return new IntSymbol(str, str.length(), index);
    }
}

