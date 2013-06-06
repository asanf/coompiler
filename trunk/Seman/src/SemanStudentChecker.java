import java.util.Enumeration;
import java.util.Set;
//TODO anche in caso di errore bisogna restituire il tipo corretto
import java.util.HashSet;

public class SemanStudentChecker implements SemanStudentVisitor {
	
	public SemanStudentChecker(ClassTable class_table)
	{
		this.cTable = class_table;
	}

	@Override
	public Object visit(programc program, Object table) {
		SymbolTable scope=(SymbolTable)table;
		Enumeration classes = program.classes.getElements();
		
		while(classes.hasMoreElements()){
			class_c c = (class_c)classes.nextElement();
				
			//controllo se esiste il metodo main in class Main
			if(c.name.equals(TreeConstants.Main)){
				Object f=c.simboli.lookup(TreeConstants.main_meth, SymbolTable.Kind.METHOD);
				if(f==null)
					cTable.semantError(c).println(c.lineNumber + ": No \'main\' method in class Main" + c.simboli);
					
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
			if(f.type_decl.equals(TreeConstants.SELF_TYPE))
				cTable.semantError().println(f.lineNumber + ": Formal parameter "+ f.name + "cannot have type SELF_TYPE");
			if(f.name.equals(TreeConstants.self))
				cTable.semantError().println(f.lineNumber + ": self cannot be the name of a formal parameter");
			else if(scope.probe(f.name, SymbolTable.Kind.OBJECT)!=null)
				cTable.semantError().println(f.lineNumber+": formal parameter "+f.name+" is multiply defined.");
			else{
				scope.addId(f.name, SymbolTable.Kind.OBJECT, f.type_decl);
				visit(f, table);
			}
		}
		AbstractSymbol inferred_type = (AbstractSymbol)visit(m.expr, scope);
		scope.exitScope();
		
		AbstractSymbol return_type = m.return_type;
		
		/*
		 * Se i due sono uguali esco direttamente
		 */
		if(m.return_type.equals(inferred_type))
			return return_type;
			
		
		/*
		 * Se sono diversi, devo considerare il caso in cui uno dei due sia SELF_TYPE
		 */
		if(return_type.equals(TreeConstants.SELF_TYPE) || inferred_type.equals(TreeConstants.SELF_TYPE)){
			// se uno dei due lo è in ogni caso devo recuperare il "valore attuale" di self_type
			AbstractSymbol mySelf = (AbstractSymbol)scope.lookup(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT);
			
			
			// a seconda se uno o entrambi sono SELF_TYPE, li aggiorno
			if(return_type.equals(TreeConstants.SELF_TYPE))
				return_type = mySelf;
			
			if(inferred_type.equals(TreeConstants.SELF_TYPE))
				inferred_type = mySelf;
		}
		
		// do errore se inferred_type non è compatibile con return_type
		if(!cTable.isAncestor(return_type, inferred_type)){
			cTable.semantError().println(m.lineNumber + ": inferred return type " + inferred_type + " of method " + m.name + " does not conform to declared return type " + m.return_type);
		}
		
		// restituisco m.return_type perché sicuramente non è stato modificato
		// (se era SELF_TYPE è rimasto SELF_TYPE
		return m.return_type;
		
	}
	
	@Override
	public Object visit(attr a, Object table) {
		AbstractSymbol t = (AbstractSymbol) visit(a.init,table);
		if(!t.equals(TreeConstants.No_type))
			if(!cTable.isAncestor(a.type_decl, t))
				cTable.semantError().println(a.lineNumber + ": type mismatch : " + a.type_decl + " <= " + t);
		return a.type_decl;
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
		AbstractSymbol expr_type = TreeConstants.No_type;
		if(e instanceof assign)
			expr_type = (AbstractSymbol)visit((assign)e,table);
		else if(e instanceof static_dispatch)
			expr_type = (AbstractSymbol)visit((static_dispatch)e,table);
		else if(e instanceof dispatch)
			expr_type = (AbstractSymbol)visit((dispatch)e,table);
		else if(e instanceof cond)
			expr_type = (AbstractSymbol)visit((cond)e,table);
		else if(e instanceof loop)
			expr_type = (AbstractSymbol)visit((loop)e,table);
		else if(e instanceof typcase)
			expr_type = (AbstractSymbol)visit((typcase)e,table);
		else if(e instanceof block)
			expr_type = (AbstractSymbol)visit((block)e,table);
		else if(e instanceof let)
			expr_type = (AbstractSymbol)visit((let)e,table);
		else if(e instanceof plus)
			expr_type = (AbstractSymbol)visit((plus)e,table);
		else if(e instanceof sub)
			expr_type = (AbstractSymbol)visit((sub)e,table);
		else if(e instanceof mul)
			expr_type = (AbstractSymbol)visit((mul)e, table);
		else if(e instanceof divide)
			expr_type = (AbstractSymbol)visit((divide)e,table);
		else if(e instanceof neg)
			expr_type = (AbstractSymbol)visit((neg)e,table);
		else if(e instanceof lt)
			expr_type = (AbstractSymbol)visit((lt)e,table);
		else if(e instanceof eq)
			expr_type = (AbstractSymbol)visit((eq)e,table);
		else if(e instanceof leq)
			expr_type = (AbstractSymbol)visit((leq)e,table);
		else if(e instanceof comp)
			expr_type = (AbstractSymbol)visit((comp)e, table);
		else if(e instanceof int_const)
			expr_type = (AbstractSymbol)visit((int_const)e,table);
		else if(e instanceof bool_const)
			expr_type = (AbstractSymbol)visit((bool_const)e,table);
		else if(e instanceof string_const)
			expr_type = (AbstractSymbol)visit((string_const)e,table);
		else if(e instanceof new_)
			expr_type = (AbstractSymbol)visit((new_)e,table);
		else if(e instanceof isvoid)
			expr_type = (AbstractSymbol)visit((isvoid)e,table);
		else if(e instanceof no_expr)
			expr_type = (AbstractSymbol)visit((no_expr)e,table);
		else if(e instanceof object)
			expr_type = (AbstractSymbol)visit((object)e,table);
		
		return expr_type;
	}

	
	@Override
	public Object visit(typcase tc, Object table) {
		AbstractSymbol tc_expr_type=(AbstractSymbol)visit(tc.expr, table);
		AbstractSymbol tc_cases_type=(AbstractSymbol)visit(tc.cases, table);
		
		tc.set_type(tc_cases_type);
		return tc_cases_type;
	}
	
	@Override
	public Object visit(Cases case_list, Object table) {
		//TODO restituire SELF_TYPE se tutti i branch danno self_type
		SymbolTable scope = (SymbolTable)table;
		Enumeration cases = case_list.getElements();
		Set<AbstractSymbol> tipi = new HashSet<AbstractSymbol>();
		boolean all_self_type = true;
		AbstractSymbol b_type;
		AbstractSymbol common_type;
		AbstractSymbol self_type = (AbstractSymbol) scope.lookup(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT);
		
		branch b=(branch)cases.nextElement();
		
		common_type=(AbstractSymbol)visit(b,table);
		
		if(common_type.equals(TreeConstants.SELF_TYPE))
			common_type = self_type;
		else
			all_self_type = false;
		
		tipi.add(b.type_decl);
		
		while(cases.hasMoreElements()){
			
			b=(branch)cases.nextElement();
			b_type=(AbstractSymbol)visit(b,table);
		
			if(b_type.equals(TreeConstants.SELF_TYPE))
				b_type = self_type;
			else
				all_self_type = false;
			
			common_type = cTable.nearestCommonAncestor(common_type, b_type);
			
			if(tipi.contains(b.type_decl))
				cTable.semantError().println(b.lineNumber + ": Duplicate " + b_type + " in case statement.");
			else
				tipi.add(b.type_decl);
		}
		
		if(all_self_type) return TreeConstants.SELF_TYPE;
		return common_type;
	}

	@Override
	public Object visit(branch b, Object table) {
		
		SymbolTable scope = (SymbolTable) table;
		
		scope.enterScope();
		
		scope.addId(b.name, SymbolTable.Kind.OBJECT, b.type_decl);
		AbstractSymbol b_type=(AbstractSymbol)visit(b.expr, scope);
		scope.exitScope();
		
		return b_type;
	}

	@Override
	public Object visit(assign a, Object table) {
		// object_id <- expr
		SymbolTable scope = (SymbolTable) table;
		
		// cerco l'id nella symbol table 
		AbstractSymbol var_type = (AbstractSymbol)scope.lookup(a.name, SymbolTable.Kind.OBJECT);
		
		// se non esiste lo cerco fra gli antenati
		if(var_type == null){
			AbstractSymbol current_class = (AbstractSymbol)scope.lookup(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT);
			var_type = (AbstractSymbol)cTable.isInherited(current_class, a.name, SymbolTable.Kind.OBJECT);
			if(var_type == null)
				cTable.semantError().println(a.lineNumber + ": assignment to undeclared variable " + a.name);
		// errore se sto assegnando a self
		}else if(var_type.equals(TreeConstants.self))
			cTable.semantError().println(a.lineNumber + ": cannot assign to \'self\'");
		// valuto l'espressione e recupero il suo tipo
		AbstractSymbol expr_type = (AbstractSymbol) visit(a.expr,table);
		
		
		// perché l'assegnamento sia corretto, expr_type deve essere compatibile con var_type
		if(!cTable.isAncestor(var_type, expr_type))
			cTable.semantError().println(a.lineNumber + ": type mismatch : " + var_type + " <= " + expr_type); 
		
		a.set_type(expr_type);
		return expr_type;
	}

	@Override
	public Object visit(static_dispatch sd, Object table) {
		//TODO replicare le modifiche fatte in dispatch anche qui, oppure refactor possibilmente
		SymbolTable scope = (SymbolTable) table;
		
		AbstractSymbol t0 = (AbstractSymbol) visit(sd.expr, table);
		AbstractSymbol expr_class_name = t0;
		
		if(t0.equals(TreeConstants.SELF_TYPE))
			expr_class_name = (AbstractSymbol) scope.lookup(t0, SymbolTable.Kind.OBJECT);
		
		
		if(!cTable.isAncestor(sd.type_name, expr_class_name)){
			cTable.semantError().println(sd.lineNumber + ": Tipo del Static Dispatch:"+sd.name +" non corrisponde con quello dichiarato");

		}
		class_c dispatch_class = cTable.lookup(sd.type_name);
	
		//TODO Controllare se posso mettere sempre false
		t0 = checkClassMethod(dispatch_class, sd.name, sd.actual, table, false);
		
		sd.set_type(t0);
		return t0;
		
	}

	@Override
	public Object visit(dispatch d, Object table) {
		SymbolTable scope = (SymbolTable) table;
		AbstractSymbol t0 = (AbstractSymbol) visit(d.expr, table);
		AbstractSymbol class_name = t0;
		boolean isSelfType = false;
		class_c dispatch_class;
		if(t0.equals(TreeConstants.SELF_TYPE)){
			class_name = (AbstractSymbol)scope.lookup(t0, SymbolTable.Kind.OBJECT);
			isSelfType = true;
		}
		
		
		dispatch_class = cTable.lookup(class_name);
		if(dispatch_class == null){
			cTable.semantError().println(d.lineNumber + ": il tipo " + class_name + " non è stato dichiarato");
		}else{
			t0 = checkClassMethod(dispatch_class, d.name, d.actual, table, isSelfType);
			d.set_type(t0);
		}
		
		return t0;
	}
	
	private AbstractSymbol checkClassMethod(class_c cl, AbstractSymbol methodName, Expressions actual, Object table, boolean isSelf){
		method m;
		AbstractSymbol toReturn = cl.name;
		boolean confrontabili = true;
		Enumeration<formalc> formali = null;
		Enumeration attuali;
		
		m = (method)cl.simboli.lookup(methodName, SymbolTable.Kind.METHOD);
		if( m == null) m =(method) cTable.isInherited(cl.name, methodName, SymbolTable.Kind.METHOD);
		if( m == null){
			cTable.semantError().println(actual.lineNumber + ": dispatch to undefined method " + methodName);
			confrontabili = false;
			toReturn = cl.name;
		}else{
			if(!m.return_type.equals(TreeConstants.SELF_TYPE)){
				toReturn = m.return_type;
			}
		}
				
		if(confrontabili){
			if(m.formals.getLength() != actual.getLength()){
				cTable.semantError(cl).println("Il metodo " + methodName + " è invocato con un numero errato di parametri");
				confrontabili = false;
			}else{
				formali = (Enumeration<formalc>)m.formals.getElements();
			}
			if(actual.getLength() == 0){
				confrontabili = false;
			}
		}
		
		attuali = actual.getElements();
		AbstractSymbol actualType, formalType;
			
		
		while( attuali.hasMoreElements()){
			actualType = (AbstractSymbol) visit((Expression)attuali.nextElement(), table);
			if(confrontabili){
				formalType = formali.nextElement().type_decl;
				if(actualType.equals(TreeConstants.SELF_TYPE))
					actualType = (AbstractSymbol)((SymbolTable)table).lookup(actualType, SymbolTable.Kind.OBJECT);
				if(!cTable.isAncestor(formalType, actualType))
					cTable.semantError().println(actual.lineNumber + ": tipi non compatibili: " + actualType + ". " + formalType);
			}
		}
		if(isSelf && m.return_type.equals(TreeConstants.SELF_TYPE)){
			
			return TreeConstants.SELF_TYPE;
		}
		
		return toReturn;
	}
	

	@Override
	public Object visit(cond c, Object table) {
		//if pred then then_expr else else_expr fi
		// recupero i tipi delle singole espressioni
		
		SymbolTable scope = (SymbolTable)table;
		AbstractSymbol if_type;
		AbstractSymbol pred_type = (AbstractSymbol) visit(c.pred,table);
		AbstractSymbol then_type = (AbstractSymbol) visit(c.then_exp,table);
		AbstractSymbol else_type = (AbstractSymbol) visit(c.else_exp,table);
		AbstractSymbol current_class = (AbstractSymbol)scope.lookup(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT); 
		
		// il tipo di pred deve essere un boolean
		if(!pred_type.equals(TreeConstants.Bool))
			cTable.semantError().println(c.lineNumber + ": Condizione if : " + pred_type + " invece di Bool");
		
		if(then_type.equals(TreeConstants.SELF_TYPE) && else_type.equals(TreeConstants.SELF_TYPE)){
			c.set_type(TreeConstants.SELF_TYPE);
			return TreeConstants.SELF_TYPE;
		}
		
		
		// il tipo dell'if è il primo antenato comune
		if(!then_type.equals(TreeConstants.SELF_TYPE) || !else_type.equals(TreeConstants.SELF_TYPE)){
			if(then_type.equals(TreeConstants.SELF_TYPE))
				then_type = current_class;
			if(else_type.equals(TreeConstants.SELF_TYPE))
				else_type = current_class;
			if_type = cTable.nearestCommonAncestor(then_type, else_type);
		}
		else
			if_type = TreeConstants.SELF_TYPE;
		
		c.set_type(if_type);
		return if_type;
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
		l.set_type(TreeConstants.Object_);
		return TreeConstants.Object_;
	}



	@Override
	public Object visit(block b, Object table) {
		AbstractSymbol t = (AbstractSymbol) visit(b.body, table);
		b.set_type(t);
		return t;
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
				(cTable.lookup(l.type_decl) != null))){
			cTable.semantError().println(l.lineNumber + ": il tipo " + l.type_decl + " non è stato dichiarato");
		}
		if(!(init_type.equals(TreeConstants.No_type) || cTable.isAncestor(l.type_decl, init_type)))
			cTable.semantError().println(l.lineNumber + ": Type mismatch: " + l.type_decl + " <- " + init_type);
		
		
		scope.enterScope();
		scope.addId(l.identifier, SymbolTable.Kind.OBJECT, l.type_decl);
		
		AbstractSymbol let_type = (AbstractSymbol) visit(l.body, scope);
		scope.exitScope();
		l.set_type(let_type);
		return let_type;
	}

	@Override
	public Object visit(plus e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "+" + t2);
		}
		e.set_type(TreeConstants.Int);
		return TreeConstants.Int;
	}

	@Override
	public Object visit(sub e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "-" + t2);
		}
		e.set_type(TreeConstants.Int);
		return TreeConstants.Int;
	}

	@Override
	public Object visit(mul e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "*" + t2);
		}
		e.set_type(TreeConstants.Int);
		return TreeConstants.Int;
	}

	@Override
	public Object visit(divide e, Object table) {
		AbstractSymbol t1 = (AbstractSymbol) visit(e.e1,table);
		AbstractSymbol t2 = (AbstractSymbol) visit(e.e2,table);
		if(!(t1.equals(TreeConstants.Int) && t2.equals(TreeConstants.Int))){
			cTable.semantError().println(e.lineNumber + ": non-int arguments:" + t1 + "/" + t2);
		}
		e.set_type(TreeConstants.Int);
		return TreeConstants.Int;
	}

	@Override
	public Object visit(neg e, Object table) {
		// ~ e1, negazione aritmetica
		AbstractSymbol t = (AbstractSymbol) visit(e.e1, table);
		
		// questa operazione può essere fatta solo su un intero
		if(!t.equals(TreeConstants.Int)){
			cTable.semantError().println(e.lineNumber + ": " + t + " non è Int");
		}
		
		e.set_type(TreeConstants.Int);
		// il tipo di ritorno è un intero
		return TreeConstants.Int;
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
		e.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
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
				cTable.semantError().println("Linea " + e.lineNumber + ": Non è possibile confrontare un oggetto di tipo " + t1 + " con un uno di tipo " + t2);
			}
		
		// il risultato è sempre un bool
		e.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
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
		e.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
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
		e.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
	}

	@Override
	public Object visit(int_const i, Object table) {
		
		i.set_type(TreeConstants.Int);
		return TreeConstants.Int;
	}

	@Override
	public Object visit(bool_const b, Object table) {
		b.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
	}

	@Override
	public Object visit(string_const s, Object table) {
		s.set_type(TreeConstants.Str);
		return TreeConstants.Str;
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
			n.set_type(TreeConstants.No_type);
			return TreeConstants.No_type;
		}
		// in caso contrario il tipo è valido e posso restituirlo
		n.set_type(n.type_name);
		return n.type_name;
	}

	@Override
	public Object visit(isvoid iv, Object table) {
		visit(iv.e1, table);
		iv.set_type(TreeConstants.Bool);
		return TreeConstants.Bool;
	}

	@Override
	public Object visit(no_expr ne, Object table) {
		ne.set_type(TreeConstants.No_type);
		return TreeConstants.No_type;
	}

	@Override
	public Object visit(object o, Object table) {
		/*
		 * In questo nodo devo semplicemente controllare la presenza
		 * dell'id nello scope corrente: se l'id esiste restituisco il suo tipo,
		 * altrimenti no_type
		 */
		
		
		SymbolTable scope = (SymbolTable) table;
		AbstractSymbol current_class = (AbstractSymbol)scope.lookup(TreeConstants.SELF_TYPE, SymbolTable.Kind.OBJECT);
		AbstractSymbol result = (AbstractSymbol)scope.lookup(o.name, SymbolTable.Kind.OBJECT);
		if(result == null){
			result = (AbstractSymbol)cTable.isInherited(current_class, o.name, SymbolTable.Kind.OBJECT);
			if(result == null){
				cTable.semantError().println(o.lineNumber + ": " + o.name + " non è stato dichiarato.");
				o.set_type(TreeConstants.No_type);
				return TreeConstants.No_type;
			}
		}
		o.set_type(result);
		return result;
	}
	
	

	private ClassTable cTable;
}
