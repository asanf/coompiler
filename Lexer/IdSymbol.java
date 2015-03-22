

import java.io.PrintStream;

/** String table entry for identifiers. */
class IdSymbol extends AbstractSymbol {
    /* Creates a new symbol.
     * 
     * @see AbstractSymbol
     * */
    public IdSymbol(String str, int len, int index) {
	super(str, len, index);
    }

    /** Returns a copy of this symbol */
    public Object clone() {
	return new IdSymbol(str, str.length(), index);
    }
}

