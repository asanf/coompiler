import java.util.Enumeration;


public class Checker implements Visitor {
	
	public Checker(ClassTable class_table)
	{
		this.cTable = class_table;
	}

	@Override
	public Object visit(programc program, Object table) {
		Enumeration classes = program.classes.getElements();
		
		while(classes.hasMoreElements()){
			class_c c = (class_c)classes.nextElement();
			c.buildSymbolTable();
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
		
		return null;
	}

	@Override
	public Object visit(Feature f, Object table) {
		SymbolTable scope=(SymbolTable) table;
		
		if(f instanceof attr)
			visit((attr)f,scope);
		else if(f instanceof method)
			visit((method)f,scope);
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
	public Object visit(Formals formal_list, Object table) {
		Enumeration formals=formal_list.getElements();
		while(formals.hasMoreElements()){
			formalc f=(formalc)formals.nextElement();
			visit(f,table);
		}
		return null;
	}

	@Override
	public Object visit(Expressions expr_list, Object table) {
		// TODO Auto-generated method stub
		Enumeration exprs = expr_list.getElements();
		while(exprs.hasMoreElements()){
			visit((Expression)exprs.nextElement(),table);
		}
		return null;
	}

	@Override
	public Object visit(Cases case_list, Object table) {
		// TODO Auto-generated method stub
		Enumeration cases = case_list.getElements();
		while(cases.hasMoreElements()){
			visit((branch)cases.nextElement(),table);
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
			scope.addId(f.name, SymbolTable.Kind.OBJECT, f.type_decl);
		}
		visit(m.expr, scope);
		scope.exitScope();
		return null;
	}

	public Object visit(Expression e, Object table){
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
	public Object visit(attr a, Object table) {
		visit(a.init,table);
		return null;
	}

	@Override
	public Object visit(formalc formal, Object table) {
		//qui la visita deve occuparsi solo di aggiungere il type 
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
		// TODO Auto-generated method stub
		visit(a.expr,table);
		return null;
	}

	@Override
	public Object visit(static_dispatch sd, Object table) {
		
		return null;
	}

	@Override
	public Object visit(dispatch d, Object table) {
		/*
		 * effettuare controllo con la firma 
		 */
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(cond c, Object table) {
		// TODO Auto-generated method stub
		visit(c.pred,table);
		visit(c.then_exp,table);
		visit(c.else_exp,table);
		return null;
	}

	@Override
	public Object visit(loop l, Object table) {
		// TODO Auto-generated method stub
		visit(l.pred,table);
		visit(l.body,table);
		
		return null;
	}

	@Override
	public Object visit(typcase tc, Object table) {
		visit(tc.expr, table);
		visit(tc.cases, table);
		
		return null;
	}

	@Override
	public Object visit(block b, Object table) {
		visit(b.body, table);
		return null;
	}

	@Override
	public Object visit(let l, Object table) {
		SymbolTable scope = (SymbolTable) table;
		/*
		 * La visita di init va fatta prima di enterscope/addId
		 * perché se id esiste, init deve fare riferimento a quella 
		 * dello scope precedente
		 */
		visit(l.init, scope);
		scope.enterScope();
		scope.addId(l.identifier, SymbolTable.Kind.OBJECT, l.type_decl);
		
		visit(l.body, scope);
		scope.exitScope();
		return null;
	}

	@Override
	public Object visit(plus e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(sub e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(mul e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(divide e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(neg e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		return null;
	}

	@Override
	public Object visit(lt e, Object table) {
		visit(e.e1, table);
		visit(e.e2, table);
		return null;
	}

	@Override
	public Object visit(eq e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		AbstractSymbol t1 = e.e1.get_type();
		AbstractSymbol t2 = e.e2.get_type();
		if( t1.equals(TreeConstants.Int) || 
			t1.equals(TreeConstants.Str) ||
			t1.equals(TreeConstants.Bool)||
			t2.equals(TreeConstants.Int) ||
			t2.equals(TreeConstants.Str) ||
			t2.equals(TreeConstants.Bool))
			
			if(!t1.equals(t2)){
				cTable.semantError().append("Linea " + e.lineNumber + ": Non è possibile confrontare un oggetto di tipo " + t1 + " con un uno di tipo " + t2);
				return e.set_type(TreeConstants.No_type);
			}
			
		if(!(cTable.isAncestor(t1, t2) || cTable.isAncestor(t2, t1))){
			//TODO controllare se è vero
			//TODO errore se sono diversi
			cTable.semantError().append("");
		}
		return null;
	}

	@Override
	public Object visit(leq e, Object table) {
		
		visit(e.e1,table);
		visit(e.e2,table);
		
		if(!e.e1.get_type().equals(TreeConstants.Int) ||
			!e.e2.get_type().equals(TreeConstants.Int)){
			cTable.semantError().append("line " + e.lineNumber + ": Confronto fra due oggetti non Int");
			return e.set_type(TreeConstants.No_type);
		}
		
		return e.set_type(TreeConstants.Bool);
	}

	@Override
	public Object visit(comp e, Object table) {
		Object result = visit(e.e1, table);
		e.set_type(e.e1.get_type());
		return result;
	}

	@Override
	public Object visit(int_const i, Object table) {
		i.set_type(TreeConstants.Int);
		return cTable.lookup(TreeConstants.Int);
	}

	@Override
	public Object visit(bool_const b, Object table) {
		b.set_type(TreeConstants.Bool);
		return cTable.lookup(TreeConstants.Bool);
	}

	@Override
	public Object visit(string_const s, Object table) {
		s.set_type(TreeConstants.Str);
		return cTable.lookup(TreeConstants.Str);
	}

	@Override
	public Object visit(new_ n, Object table) {
		/*
		 * Controllo che la classe esista, 
		 * in caso positivo la restituisco al genitore 
		 * per controllare se il tipo è compatibile
		 */
		if(n.type_name.equals(TreeConstants.SELF_TYPE)){
			n.set_type(TreeConstants.SELF_TYPE);
			return TreeConstants.SELF_TYPE;
		}
		
		Object result = cTable.lookup(n.type_name);
		
		if(result == null){
			cTable.semantError().append("Line " + n.lineNumber + ": Il tipo " + n.type_name + " non esiste.");
			return n.set_type(TreeConstants.No_type);
		}
		
		n.set_type(((class_c)result).name);
		return result;
	}

	@Override
	public Object visit(isvoid iv, Object table) {
		visit(iv.e1, table);
		iv.set_type(TreeConstants.Bool);
		return cTable.lookup(TreeConstants.Bool);
	}

	@Override
	public Object visit(no_expr ne, Object table) {
		return TreeConstants.No_type;
	}

	@Override
	public Object visit(object o, Object table) {
		
		SymbolTable scope = (SymbolTable) table;
		class_c result = (class_c)scope.lookup(o.name, SymbolTable.Kind.OBJECT);
		if(result == null){
			cTable.semantError().append(o.name + " non è stato dichiarato.");
			return o.set_type(TreeConstants.No_type);
		} else {
			o.set_type(result.name);
		}
		return result;
	}

	private ClassTable cTable;
}
