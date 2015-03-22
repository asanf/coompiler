

import java.io.PrintStream;

class IntTable extends AbstractTable {
    /** Creates a new IntSymbol object.
     * 
     * @see IntSymbol
     * */
    protected AbstractSymbol getNewSymbol(String s, int len, int index) {
	return new IntSymbol(s, len, index);
    }

    /** Generates code for all int constants in the int table.  
     * @param intclasstag the class tag for Int
     * @param s the output stream
     * */
    public void codeStringTable(int intclasstag, PrintStream s) {
	IntSymbol sym = null;
	for (int i = tbl.size() - 1; i >= 0; i--) {
	    try {
		sym = (IntSymbol)tbl.elementAt(i);
	    } catch (ArrayIndexOutOfBoundsException ex) {
		Utilities.fatalError("Unexpected exception: " + ex);
	    }
	    sym.codeDef(intclasstag, s);
	}
    }
}

