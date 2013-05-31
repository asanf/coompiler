import java.util.Enumeration;
//TODO anche in caso di errore bisogna restituire il tipo corretto

public class Checker implements Visitor {
	
	public Checker(ClassTable class_table)
	{
		this.cTable = class_table;
	}

	@Override
	public Object visit(programc program, Object table) {
		SymbolTable scope=(SymbolTable)table;
		Enumeration classes = program.classes.getElements();
		
		while(classes.hasMoreElements()){
			class_c c = (class_c)classes.nextElement();
			c.buildSymbolTable(cTable);
			
			//controllo se esiste il metodo main in class Main
			if(c.name.str.equals("Main")){
				Object f=c.simboli.lookup(TreeConstants.main_meth, SymbolTable.Kind.METHOD);
				if(f==null)
					cTable.semantError(c).println(c.lineNumber + ": No \'main\' method in class Main");
			}
			
		}
		visit(program.classes, null);
		return null;
	}

	@Override
	public Object visit(Classes class_list, Object table) {
		Enumeration classes = class_list.getElements();
		while(classes.hasMoreElements())
			visit((class_c)classes.nextElement(), null);
		
		return null;
	}

	@Override
	public Object visit(Class_ cl, Object table) {
		//TODO Inutile per ora
		return null;
	}
	
	@Override
	public Object visit(class_c c, Object table) {
		
		visit(c.features, c.simboli);
		
		return null;
	}

	@Override
	public Object visit(Features feature_list, Object table) {
		SymbolTable scope = (SymbolTable) table;
		Enumeration features = feature_list.getElements();
				
		while(features.hasMoreElements()){
			Feature f = (Feature)features.nextElement();
			visit(f,scope);			
		}		
		//TODO valori restituiti da visit
		return null;
	}
	
	@Override
	public Object visit(Feature f, Object table) {
		SymbolTable scope=(SymbolTable) table;
		
		if(f instanceof attr){			
			visit((attr)f,scope);
		}
		else if(f instanceof method){
			visit((method)f,scope);
		}
		return null;
	}
	
	@Override
	public Object visit(method m, Object table) {
		SymbolTable scope = (SymbolTable)table;
		scope.enterScope();
		Enumeration parametri = m.formals.getElements();
		
		while(parametri.hasMoreElements()){
			formalc f = (formalc)parametri.nextElement();
			
			//alcuni controlli su formal
			if(f.type_decl.str.equals(TreeConstants.SELF_TYPE))
				cTable.semantError().println(f.lineNumber + ": Formal parameter "+ f.name + "cannot have type SELF_TYPE");
			if(f.name.str.equals(TreeConstants.self))
				cTable.semantError().println(f.lineNumber + ": self cannot be the name of a formal parameter");
			else if(scope.probe(f.name, SymbolTable.Kind.OBJECT)==null)
				cTable.semantError().println(f.lineNumber+": formal parameter "+f.name+" is multiply defined.");
			else{
				scope.addId(f.name, SymbolTable.Kind.OBJECT, f.type_decl);
				visit(f, table);
			}
		}
		visit(m.expr, scope);
		scope.exitScope();
		return null;
	}
	
	@Override
	public Object visit(attr a, Object table) {
		AbstractSymbol t = (AbstractSymbol) visit(a.init,table);
		if(!cTable.isAncestor(a.type_decl, t))
			cTable.semantError().println(a.lineNumber + ": type mismatch : " + a.type_decl + " <= " + t);
		return null;
	}

	@Override
	public Object visit(Formals formal_list, Object table) {
		//TODO inutile
		return null;
	}
	
	@Override
	public Object visit(formalc formal, Object table) {
		//TODO inutile 
		return null;
	}


	@Override
	public Object visit(Expressions expr_list, Object table) {
		Enumeration exprs = expr_list.getElements();
		AbstractSymbol t = TreeConstants.No_type;
		while(exprs.hasMoreElements()){
			t = (AbstractSymbol) visit((Expression)exprs.nextElement(),table);
		}
		return t;
	}

	@Override
	public Object visit(Expression e, Object table){
		//TODO etichetta tipo
		if(e instanceof assign)
			visit((assign)e,table);
		else if(e instanceof static_dispatch)
			visit((static_dispatch)e,table);
		else if(e instanceof dispatch)
			visit((dispatch)e,table);
		else if(e instanceof cond)
			visit((cond)e,table);
		else if(e instanceof loop)
			visit((loop)e,table);
		else if(e instanceof typcase)
			visit((typcase)e,table);
		else if(e instanceof block)
			visit((block)e,table);
		else if(e instanceof let)
			visit((let)e,table);
		else if(e instanceof plus)
			visit((plus)e,table);
		else if(e instanceof sub)
			visit((sub)e,table);
		else if(e instanceof mul)
			visit((mul)e, table);
		else if(e instanceof divide)
			visit((divide)e,table);
		else if(e instanceof neg)
			visit((neg)e,table);
		else if(e instanceof lt)
			visit((lt)e,table);
		else if(e instanceof eq)
			visit((eq)e,table);
		else if(e instanceof leq)
			visit((leq)e,table);
		else if(e instanceof comp)
			visit((comp)e, table);
		else if(e instanceof int_const)
			visit((int_const)e,table);
		else if(e instanceof bool_const)
			visit((bool_const)e,table);
		else if(e instanceof string_const)
			visit((string_const)e,table);
		else if(e instanceof new_)
			visit((new_)e,table);
		else if(e instanceof isvoid)
			visit((isvoid)e,table);
		else if(e instanceof no_expr)
			visit((no_expr)e,table);
		else if(e instanceof object)
			visit((object)e,table);
		
		return null;
	}

	
	@Override
	public Object visit(typcase tc, Object table) {
		visit(tc.expr, table);
		visit(tc.cases, table);
		
		return null;
	}
	
	@Override
	public Object visit(Cases case_list, Object table) {
		Enumeration cases = case_list.getElements();
		while(cases.hasMoreElements()){
			visit((branch)cases.nextElement(),table);
		}
		return null;
	}

	@Override
	public Object visit(branch b, Object table) {
		
		SymbolTable scope = (SymbolTable) table;
		
		scope.enterScope();
		
		scope.addId(b.name, SymbolTable.Kind.OBJECT, b.type_decl);
		visit(b.expr, scope);
		
		return null;
	}

	@Override
	public Object visit(assign a, Object table) {
		// object_id <- expr
		SymbolTable scope = (SymbolTable) table;
		
		// cerco l'id nella symbol table 
		AbstractSymbol var_type = (AbstractSymbol)scope.lookup(a.name, SymbolTable.Kind.OBJECT);
		
		// se non esiste stampo l'errore
		if(var_type == null)
			cTable.semantError().println(a.lineNumber + ": assignment to undeclared variable " + a.name);
		// errore se sto assegnando a self
		else if(var_type.str.equals(TreeConstants.self))
			cTable.semantError().println(a.lineNumber + ": cannot assign to \'self\'");
		// valuto l'espressione e recupero il suo tipo
		AbstractSymbol expr_type = (AbstractSymbol) visit(a.expr,table);
		
		
		// perché l'assegnamento sia corretto, expr_type deve essere compatibile con var_type
		if(!cTable.isAncestor(var_type, expr_type))
			cTable.semantError().println(a.lineNumber + ": type mismatch : " + var_type + " <= " + expr_type); 
		
		return a.set_type(expr_type);
	}

	@Override
	public Object visit(static_dispatch sd, Object table) {
		//TODO visita
		return null;
	}

	@Override
	public Object visit(dispatch d, Object table) {
		SymbolTable scope = (SymbolTable) table;
		AbstractSymbol t0 = (AbstractSymbol) visit(d.expr, table);
		AbstractSymbol returnType = null;
		Enumeration<formalc> formali = null;
		boolean confrontabili = true;
		method m;
		
		if(t0.equals(TreeConstants.self))
			t0 = (AbstractSymbol) scope.lookup(t0, SymbolTable.Kind.OBJECT);
		
		class_c dispatch_class = cTable.lookup(t0);
		
		m = (method)dispatch_class.simboli.lookup(d.name, SymbolTable.Kind.METHOD);
		
		if( m == null) m = (method) cTable.isInherited(t0, d.name, SymbolTable.Kind.METHOD);
		if( m == null)
			cTable.semantError().println(d.lineNumber + ": dispatch to undefined method " + d.name);
		else {
			formali = (Enumeration<formalc>) m.formals.getElements();
			if(!(d.actual.getLength() == m.formals.getLength())){
				cTable.semantError().println(d.lineNumber + ": il metodo " + d.name + " è invocato con un errato numero di parametri");
				confrontabili = false;
			}
		}
		
		Enumeration attuali = d.actual.getElements();
		AbstractSymbol actual_type, formal_type;
		//TODO ma actual type esiste?
		while(attuali.hasMoreElements()){
			actual_type = (AbstractSymbol) visit((Expression)attuali.nextElement(), table);
			
			if(confrontabili){
				formal_type = (AbstractSymbol)(formali.nextElement()).type_decl;
				if(!cTable.isAncestor(formal_type, actual_type))
					cTable.semantError().println(d.lineNumber + ": tipi non compatibili: " + actual_type + ". " + formal_type);
			}
			
		}
		
		if(m != null){
			if(m.return_type.equals(TreeConstants.SELF_TYPE))
				return d.set_type(t0);
			else 
				return d.set_type(m.return_type);
		}
		return d.set_type(t0);
	}

	@Override
	public Object visit(cond c, Object table) {
		//if pred then then_expr else else_expr fi
		// recupero i tipi delle singole espressioni
		AbstractSymbol pred_type = (AbstractSymbol) visit(c.pred,table);
		AbstractSymbol then_type = (AbstractSymbol) visit(c.then_exp,table);
		AbstractSymbol else_type = (AbstractSymbol) visit(c.else_exp,table);
		
		// il tipo di pred deve essere un boolean
		if(!pred_type.equals(TreeConstants.Bool))
			cTable.semantError().println(c.lineNumber + ": Condizione if : " + pred_type + " invece di Bool");
		
		// il tipo dell'if è il primo antenato comune
		AbstractSymbol if_type = cTable.nearestCommonAncestor(then_type, else_type);
		
		return c.set_type(if_type);
	}

	@Override
	public Object visit(loop l, Object table) {
		// while pred loop body pool
		
		// visito il predicato e ne recupero il tipo
		AbstractSymbol t = (AbstractSymbol) visit(l.pred,table);
		
		// il tipo del predicato deve essere un bool
		if(!t.equals(TreeConstants.Bool))
			cTable.semantError().println(l.lineNumber + ": La condizione del while deve restituire un bool, restituisce invece un " + t);
		
		// visito il corpo del while
		visit(l.body,table);
		
		// il tipo di un while è sempre object
		return l.set_type(TreeConstants.Object_);
	}



	@Override
	public Object visit(block b, Object table) {
		AbstractSymbol t = (AbstractSymbol) visit(b.body, table);
		return b.set_type(t);
	}

	@Override
	public Object visit(let l, Object table) {
		//let id:type <- init in body
		SymbolTable scope = (SymbolTable) table;
		//recupero il tipo dell'espressione init
		AbstractSymbol init_type = (AbstractSymbol)	visit(l.init, scope);
		//TODO controllare se è corretto preoccuparsi di no_type: init_type = no_type => non c'è espressione di inizializzazione?
		//TODO compatibilità con self type vuol dire?
		
		if(!(l.type_decl.equals(TreeConstants.SELF_TYPE) ||
				(cTable.lookup(l.type_decl) == null))){
			cTable.semantError().println(l.lineNumber + ": il tipo " + l.type_decl + " non è stato dichiarato");
		}
		if(!(init_type.equals(TreeConstants.No_type) || cTable.isAncestor(l.type_decl, init_type)))
			cTable.semantError().println(l.lineNumber + ": Type mismatch: " + l.type_decl + " <- " + init_type);
		
		
		scope.enterScope();
		scope.addId(l.identifier, SymbolTable.Kind.OBJECT, l.type_decl);
		
		AbstractSymbol let_type = (AbstractSymbol) visit(l.body, scope);
		scope.exitScope();
		return l.set_type(let_type);
	}

	@Override
	public Object visit(plus e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "+" + t2);
		}
		return e.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(sub e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "-" + t2);
		}
		return e.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(mul e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "*" + t2);
		}
		return e.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(divide e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "/" + t2);
		}
		return e.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(neg e, Object table) {
		// ~ e1, negazione aritmetica
		AbstractSymbol t = (AbstractSymbol) visit(e.e1, table);
		
		// questa operazione può essere fatta solo su un intero
		if(!t.equals(TreeConstants.Int)){
			cTable.semantError().println(e.lineNumber + ": " + t + " non è Int");
		}
		
		// il tipo di ritorno è un intero
		return e.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(lt e, Object table) {
		// e1 < e2
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1, table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2, table);
		
		// solo due interi possono essere confrontati con <
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": uno dei due operandi non è di tipo int");
		}
		return e.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(eq e, Object table) {
		// e1 = e2
		
		// recupero i tipi delle due espressioni
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		
		
		/*
		 * Se uno dei due è Int, Str, Bool allora le due espressioni
		 * devono avere lo stesso tipo per essere confrontabili
		 */
		if( t1.equals(TreeConstants.Int) || 
			t1.equals(TreeConstants.Str) ||
			t1.equals(TreeConstants.Bool)||
			t2.equals(TreeConstants.Int) ||
			t2.equals(TreeConstants.Str) ||
			t2.equals(TreeConstants.Bool))
			
			if(!t1.equals(t2)){
				cTable.semantError().append("Linea " + e.lineNumber + ": Non è possibile confrontare un oggetto di tipo " + t1 + " con un uno di tipo " + t2);
			}
		
		// il risultato è sempre un bool
		return e.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(leq e, Object table) {
		// e1 <= e2
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		
		// solo due interi possono essere confrontati
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println("line " + e.lineNumber + ": Confronto fra due oggetti non Int");
		}
		
		// il tipo di un confronto è sempre un bool
		return e.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(comp e, Object table) {
		// not e1 , not booleano
		AbstractSymbol t = (AbstractSymbol) visit(e.e1, table);
		
		// e1 deve essere un'espressione booleana
		if(!t.equals(TreeConstants.Bool)){
			cTable.semantError().println(e.lineNumber + ": " + t + " non è Bool");
		}
		
		// il tipo restituito è sempre bool
		return e.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(int_const i, Object table) {
		
		return i.set_type(TreeConstants.Int);
	}

	@Override
	public Object visit(bool_const b, Object table) {
		return b.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(string_const s, Object table) {
		
		return s.set_type(TreeConstants.Str);
	}

	@Override
	public Object visit(new_ n, Object table) {
		// new TYPE_ID

		// se il tipo è SELF_TYPE lo restituisco direttamente
		if(n.type_name.equals(TreeConstants.SELF_TYPE)){
			return n.set_type(TreeConstants.SELF_TYPE);
		}
		
		// cerco la classe di nome type_name nella class table
		Object result = cTable.lookup(n.type_name);
		
		// se non esiste stampo l'errore e restituisco no_type
		if(result == null){
			cTable.semantError().println("Line " + n.lineNumber + ": Il tipo " + n.type_name + " non esiste.");
			return n.set_type(TreeConstants.No_type);
		}
		// in caso contrario il tipo è valido e posso restituirlo
		return n.set_type(n.type_name);
	}

	@Override
	public Object visit(isvoid iv, Object table) {
		visit(iv.e1, table);
		return iv.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(no_expr ne, Object table) {
		return ne.set_type(TreeConstants.No_type);
	}

	@Override
	public Object visit(object o, Object table) {
		/*
		 * In questo nodo devo semplicemente controllare la presenza
		 * dell'id nello scope corrente: se l'id esiste restituisco il suo tipo,
		 * altrimenti no_type
		 */
		SymbolTable scope = (SymbolTable) table;
		class_c result = (class_c)scope.lookup(o.name, SymbolTable.Kind.OBJECT);
		if(result == null){
			cTable.semantError().println(o.name + " non è stato dichiarato.");
			return o.set_type(TreeConstants.No_type);
		}
		return o.set_type(result.name);
	}

	private ClassTable cTable;
}
