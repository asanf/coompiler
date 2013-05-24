import java.util.Enumeration;


public class Checker implements Visitor {

	@Override
	public Object visit(programc program, Object table) {
		
		return null;
	}

	@Override
	public Object visit(Classes class_list, Object table) {
		// TODO Auto-generated method stub
	
		
		return null;
	}

	@Override
	public Object visit(Class_ cl, Object table) {
		// TODO Auto-generated method stub
		
		
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Expressions expr_list, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Cases case_list, Object table) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		return null;
	}

	@Override
	public Object visit(static_dispatch sd, Object table) {
		// TODO Auto-generated method stub
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
		SymbolTable scope = (SymbolTable) table;
		visit(l.pred,scope);
		visit(l.body,scope);
		
		return null;
	}

	@Override
	public Object visit(typcase tc, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(block b, Object table) {
		// TODO Auto-generated method stub
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
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(sub e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(mul e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(divide e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(neg e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		return null;
	}

	@Override
	public Object visit(lt e, Object table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(eq e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(leq e, Object table) {
		// TODO Auto-generated method stub
		SymbolTable scope = (SymbolTable) table;
		visit(e.e1,scope);
		visit(e.e2,scope);
		return null;
	}

	@Override
	public Object visit(comp e, Object table) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
