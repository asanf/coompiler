

// This is a project skeleton file for PA5, but a regular support code
// for other assignments.

import java.io.PrintStream;

/** String table entry for string constants. */
class StringSymbol extends AbstractSymbol {
    /* Creates a new symbol.
     * 
     * @see AbstractSymbol
     * */
    public StringSymbol(String str, int len, int index) {
	super(str, len, index);
    }

    /** Generates code for the string constant definition.  This method
     * is incomplete; you get to finish it up in programming assignment
     * 5.
     * @param stringclasstag the class tag for string object
     * @param s the output stream
     *
     * */
    public void codeDef(int stringclasstag, PrintStream s) {
	IntSymbol lensym = (IntSymbol)AbstractTable.inttable.addInt(str.length());
	
	// Add -1 eye catcher
	s.println(CgenSupport.WORD + "-1");
	codeRef(s); s.print(CgenSupport.LABEL); // label
	s.println(CgenSupport.WORD + stringclasstag); // tag
	s.println(CgenSupport.WORD + (CgenSupport.DEFAULT_OBJFIELDS +
				      CgenSupport.STRING_SLOTS +
				      (str.length() + 4) / 4)); // object size
	s.print(CgenSupport.WORD);

	/* Add code to reference the dispatch table for class String here */

	s.println("");		// dispatch table
	s.print(CgenSupport.WORD); lensym.codeRef(s); s.println(""); // length
	CgenSupport.emitStringConstant(str, s); // ascii string
	s.print(CgenSupport.ALIGN); // align to word
    }

    /** Emits a reference to this string constant.
     * @param s the output stream
     * */
    public void codeRef(PrintStream s) {
	s.print(CgenSupport.STRCONST_PREFIX + index);
    }

    /** Returns a copy of this symbol */
    public Object clone() {
	return new StringSymbol(str, str.length(), index);
    }
}

