/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;


/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private int semantErrors;
    private PrintStream errorStream;
    private SemanStudentInheritanceGraph graph;
    private ArrayList<class_c> badClass;
    

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
	AbstractSymbol filename 
	    = AbstractTable.stringtable.addString("<basic class>");
	
	// The following demonstrates how to create dummy parse trees to
	// refer to basic Cool classes.  There's no need for method
	// bodies -- these are already built into the runtime system.

	// IMPORTANT: The results of the following expressions are
	// stored in local variables.  You will want to do something
	// with those variables at the end of this method to make this
	// code meaningful.

	// The Object class has no parent class. Its methods are
	//        cool_abort() : Object    aborts the program
	//        type_name() : Str        returns a string representation 
	//                                 of class name
	//        copy() : SELF_TYPE       returns a copy of the object

	class_c Object_class = 
	    new class_c(0, 
		       TreeConstants.Object_, 
		       TreeConstants.No_class,
		       new Features(0)
			   .appendElement(new method(0, 
					      TreeConstants.cool_abort, 
					      new Formals(0), 
					      TreeConstants.Object_, 
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.type_name,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.copy,
					      new Formals(0),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0))),
		       filename);
	
	// The IO class inherits from Object. Its methods are
	//        out_string(Str) : SELF_TYPE  writes a string to the output
	//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
	//        in_string() : Str            reads a string from the input
	//        in_int() : Int                "   an int     "  "     "

	class_c IO_class = 
	    new class_c(0,
		       TreeConstants.IO,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.out_string,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.out_int,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_string,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_int,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0))),
		       filename);

	// The Int class has no methods and only a single attribute, the
	// "val" for the integer.

	class_c Int_class = 
	    new class_c(0,
		       TreeConstants.Int,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// Bool also has only the "val" slot.
	class_c Bool_class = 
	    new class_c(0,
		       TreeConstants.Bool,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// The class Str has a number of slots and operations:
	//       val                              the length of the string
	//       str_field                        the string itself
	//       length() : Int                   returns length of the string
	//       concat(arg: Str) : Str           performs string concatenation
	//       substr(arg: Int, arg2: Int): Str substring selection

	class_c Str_class =
	    new class_c(0,
		       TreeConstants.Str,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.Int,
					    new no_expr(0)))
			   .appendElement(new attr(0,
					    TreeConstants.str_field,
					    TreeConstants.prim_slot,
					    new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.length,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.concat,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg, 
								     TreeConstants.Str)),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.substr,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int))
						  .appendElement(new formalc(0,
								     TreeConstants.arg2,
								     TreeConstants.Int)),
					      TreeConstants.Str,
					      new no_expr(0))),
		       filename);

	/* Do something with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */
	
		graph.addVertex(Object_class);
		graph.addVertex(IO_class);
		graph.addVertex(Bool_class);
		graph.addVertex(Int_class);
		graph.addVertex(Str_class);
			
		graph.addEdge(Object_class, IO_class);
		graph.addEdge(Object_class, Int_class);
		graph.addEdge(Object_class, Str_class);
		graph.addEdge(Object_class, Bool_class);
    
    }

	


    public ClassTable(Classes cls) {
	semantErrors = 0;
	errorStream = System.err;
	Boolean main_flag = false;
	
	/* fill this in */
	graph = new SemanStudentInheritanceGraph();
	
	//inserisco le classi predefinite
	installBasicClasses();

	//inserisco le classi nel grafo
		Enumeration class_enum = cls.getElements();
		while(class_enum.hasMoreElements()){
			class_c cl=(class_c)class_enum.nextElement();
			
			if(cl.name.equals(TreeConstants.Int.str)
					|| cl.name.equals(TreeConstants.Bool.str)
					|| cl.name.equals(TreeConstants.Str.str)
					|| cl.name.equals(TreeConstants.SELF_TYPE.str)
			)
				semantError(cl).println("Redefinition of basic class " + cl.name);
			
			else if(graph.findVertex(cl.name) != null)
				semantError(cl).println("Class " + cl.name + " was previously defined.");
			
			else {
				if(cl.name.equals(TreeConstants.Main))	//controllo se esiste una classe Main
					main_flag=true;
				graph.addVertex(cl);
			}
		}
		if(!main_flag){
			semantError().println("Class Main is not defined.");
		}
		
		
		Set<class_c> vertices = graph.vertexSet();
		class_c p;
		for (class_c c : vertices) {
			if(!c.name.equals(TreeConstants.Object_)){
				if(c.parent.equals(TreeConstants.Int.str)
						|| c.parent.equals(TreeConstants.Bool.str)
						|| c.parent.equals(TreeConstants.Str.str)
						|| c.parent.equals(TreeConstants.SELF_TYPE.str)
				)
					semantError(c).println("Class " + c.name + " cannot inherits class " + c.parent);
				else if((p = graph.findVertex(c.parent)) == null)
					semantError(c).println("Class " + c.name + " inherits from undefined class " + c.parent);
				else
					graph.addEdge(p, c);
			}
		}
		checkCycles();
		
		// costruisco le tabelle dei simboli
		buildSymbolTables();

    }
    
    
    public boolean checkCycles(){
    	CycleDetector<class_c, DefaultEdge> detector = 
    			new CycleDetector<class_c, DefaultEdge>(graph);
    	Set<class_c> bad_nodes = detector.findCycles();
    	
    	if(bad_nodes.isEmpty())
    		return false;
    	for (class_c cl : bad_nodes) {
			semantError().println("Class " + cl.name + ", or an ancestor of " + cl.name + ", is involved in an inheritance cycle.");
		}
    	return true;
    }
    
      
    
    public class_c lookup(AbstractSymbol name){
    	return graph.findVertex(name);
    }
    
    
    public boolean isAncestor(AbstractSymbol ancestor, AbstractSymbol descendant){
    	class_c desc = lookup(descendant);
    	class_c anc = lookup(ancestor);
    	class_c Object_class = graph.findVertex(TreeConstants.Object_);
    	if( desc == null || anc == null)
    		return false;
    	
    	if( desc.name.equals(anc.name))
    		return true;
    	
    	if(desc.parent.equals(anc.name))
    		return true;
    	
    	while(!desc.name.equals(TreeConstants.Object_)){
 
    		desc = lookup(desc.parent);
    		if(desc.name.equals(anc.name))
    			return true;
    	}
    	return false;
    }
    
    public AbstractSymbol nearestCommonAncestor(AbstractSymbol first, AbstractSymbol second){
    	class_c f = lookup(first);
    	class_c s = lookup(second);
    	boolean result;
    	
    	
    	while(!(result = isAncestor(f.name, s.name))){
    		
    		f = lookup(f.parent);
    	}
    	
    	return f.name;
    }
    
    
    public Object isInherited(AbstractSymbol class_name, AbstractSymbol formal_name, SymbolTable.Kind kind){
    	
    	Object toReturn = null;
    	    	
    	if(class_name.equals(TreeConstants.Object_))
    		return toReturn;
    	
    	class_c curr = this.lookup(class_name);
    	
    	
    	 	
    	do {
    		curr = lookup(curr.parent);
    		
    		toReturn = curr.simboli.lookup(formal_name, kind);
    		if(toReturn != null)
    			return toReturn;
    	
    	}while(!curr.name.equals(TreeConstants.Object_));
    	
    	
    	return toReturn;
    	
    }
    
    
    public void buildSymbolTables(){
    	TopologicalOrderIterator<class_c, DefaultEdge> it = new TopologicalOrderIterator<class_c, DefaultEdge>(graph);
    	
    	while(it.hasNext()){
    		class_c current = it.next();
    		buildSymbolTable(current);
    	}
    }
    
    
    private void buildSymbolTable(class_c cl){
    	Features fl=cl.getFeatures();
    	Enumeration features=fl.getElements();
    	attr a;
    	method m;
    	//aggiungo self alla classe corrente
    	cl.simboli.addId(TreeConstants.self, SymbolTable.Kind.OBJECT, TreeConstants.SELF_TYPE);
    	cl.simboli.addId(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT, cl.name);
    	while(features.hasMoreElements()){
    		Feature f=(Feature)features.nextElement();
    		if(f instanceof attr){
    			a=(attr)f;
    			Object s=cl.simboli.lookup(a.name, SymbolTable.Kind.OBJECT);
    			if(s!=null){
    				if(a.name.equals(TreeConstants.self))
    					semantError().println(a.lineNumber + ": /'self'/ cannot be the name of attribute.");
    				else //attributo già dichiarato
    					semantError().println(a.lineNumber + "attribute " + a.name + "is multiply defined.");
    			}
    			else{
    				attr in_a=(attr)isInherited(cl.name, a.name, SymbolTable.Kind.OBJECT);
    				if(in_a!=null){
    					//attributo ereditato, quindi non ridefinibile
    					semantError().println(a.lineNumber + ": Attribute " + a.name + "is an attribute of an inherited class.");
    				}
    				else //aggiungo l'attributo alla tabella dei simobli della classe
    					
    					cl.simboli.addId(a.name, SymbolTable.Kind.OBJECT, a.type_decl);
    			}
    		}
    		else if(f instanceof method){
    			m=(method)f;
    			method s=(method)cl.simboli.lookup(m.name, SymbolTable.Kind.METHOD);
    			
    			if(s!=null){
    				//metodo già definito
    				semantError().println(m.lineNumber + ": Method " + m.name + "is multiply defined.");				
    			}
    			else{
    				method in_m=(method)isInherited(cl.name, m.name, SymbolTable.Kind.METHOD);
    				if(in_m!=null){
    					//metodo ereditato, quindi posso sovrascriverlo ma non sovraccaricarlo
    					
    					//controllo tipo di ritorno
    					if(!(in_m.return_type.equals(m.return_type))){
    						semantError().println(m.lineNumber + ": in redefinited method " + m.name + ", return type " + m.return_type + " is different from original return type " + in_m.return_type);
    						break;
    					}
    					else{ //controllo il numero di formal poi il tipo
    						Formals mf=m.formals;
    						Formals in_mf=in_m.formals;
    						Enumeration m_formals=mf.getElements();
    						Enumeration in_mf_formals=mf.getElements();
    						boolean flag_type=true;
    						boolean flag_mf=m_formals.hasMoreElements();
    						boolean flag_in_mf=in_mf_formals.hasMoreElements();
    						
    						if(flag_mf && flag_in_mf){
    							do{
        							formalc f1=(formalc)m_formals.nextElement();
        							formalc f2=(formalc)in_mf_formals.nextElement();
        							
        							if(!(f1.type_decl.equals(f2.type_decl))){
        								// tipo dei parametri non esatto
        								flag_type=false;
        							}
        								
        							flag_mf=m_formals.hasMoreElements();
            						flag_in_mf=in_mf_formals.hasMoreElements();
        						}while(flag_mf && flag_in_mf);
    						}
    						if(flag_mf!=flag_in_mf){//uno dei due aveva ancora parametri formali
    							semantError().println(m.lineNumber + ": Incompatible number of formal parameters in redefined method " + m.name + ".");
    						}
    						else if(!flag_type){ //incompatibilità di tipo dei parametri
    							semantError().println(m.lineNumber + ": In redefined method " + m.name + ", parameter type " + m.return_type + " is different from original type " + in_m.return_type + ".");
    						}    						
    					}    					
    				}
    				else //nuovo metodo da aggiungere alla tabella dei simboli
    					cl.simboli.addId(m.name, SymbolTable.Kind.METHOD, m);
    			}
    			
    		}
    	}
    }

    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_c c) {
	return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
	errorStream.print(filename + ":" + t.getLineNumber() + ": ");
	return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
	semantErrors++;
	return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
	return semantErrors != 0;
    }

    
}
			  
    
