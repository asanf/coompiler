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
			visit((typcase)cases.nextElement(),table);
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

	public Object visit(Expression expr, Object table){
		return null;
	}
	
	@Override
	public Object visit(attr a, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(formalc formal, Object table) {
		//qui la visita deve occuparsi solo di aggiungere il type 
		return null;
	}

	@Override
	public Object visit(branch b, Object table) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
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
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(eq e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(leq e, Object table) {
		// TODO Auto-generated method stub
		visit(e.e1,table);
		visit(e.e2,table);
		return null;
	}

	@Override
	public Object visit(comp e, Object table) {
		visit(e.e1, table);		
		return null;
	}

	@Override
	public Object visit(int_const i, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(bool_const b, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(string_const s, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(new_ n, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(isvoid iv, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(no_expr ne, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(object o, Object table) {
		SymbolTable scope = (SymbolTable) table;
		if(scope.lookup(o.name, SymbolTable.Kind.OBJECT) == null){
			cTable.semantError().append(o.name + " non è stato dichiarato.");
		}
		return null;
	}

	private ClassTable cTable;
}
