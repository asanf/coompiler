

import java.io.PrintStream;

class StringTable extends AbstractTable {
    /** Creates a new StringSymbol object.
     * 
     * @see StringSymbol
     * */
    protected AbstractSymbol getNewSymbol(String s, int len, int index) {
	return new StringSymbol(s, len, index);
    }

    /** Generates code for all string constants in the string table.  
     * @param stringclasstag the class tag for String
     * @param s the output stream
     * */
    public void codeStringTable(int stringclasstag, PrintStream s) {
	StringSymbol sym = null;
	for (int i = tbl.size() - 1; i >= 0; i--) {
	    try {
		sym = (StringSymbol)tbl.elementAt(i);
	    } catch (ArrayIndexOutOfBoundsException ex) {
		Utilities.fatalError("Unexpected exception: " + ex);
	    }
	    sym.codeDef(stringclasstag, s);
	}
    }
}
