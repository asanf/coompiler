

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java_cup.runtime.Symbol;

/** The lexer driver class */
class Lexer {

    /** Loops over lexed tokens, printing them out to the console */
    public static void main(String[] args) {
    	args = Flags.handleFlags(args);   

	for (int i = 0; i < args.length; i++) {
	    FileReader file = null;       

	    try {
		file = new FileReader(args[i]);  
		
		System.out.println("#name \"" + args[i] + "\"");  

		CoolLexer lexer = new CoolLexer(file);   
		lexer.set_filename(args[i]);                  

		Symbol s;
		while ((s = lexer.next_token()).sym != TokenConstants.EOF) {
		    Utilities.dumpToken(System.out, lexer.get_curr_lineno(), s);
		}
	    } catch (FileNotFoundException ex) {
		Utilities.fatalError("Could not open input file " + args[i]);
	    } catch (IOException ex) {
		Utilities.fatalError("Unexpected exception in lexer");
	    }
	}
    }
}
