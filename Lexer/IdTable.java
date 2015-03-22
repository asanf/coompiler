

class IdTable extends AbstractTable {
    /** Creates a new IdSymbol object. 
     * 
     * @see IdSymbol
     * */
    protected AbstractSymbol getNewSymbol(String s, int len, int index) {
	return new IdSymbol(s, len, index);
    }
}
