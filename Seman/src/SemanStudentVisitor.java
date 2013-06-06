
public interface SemanStudentVisitor {

	public Object visit(programc program, Object table);
	public Object visit(Classes class_list, Object table);
	public Object visit(Class_ cl, Object table);
	public Object visit(Feature f, Object table);
	public Object visit(class_c c, Object table);
	public Object visit(Features feature_list, Object table);
	public Object visit(Formals formal_list, Object table);
	public Object visit(Expressions expr_list, Object table);
	public Object visit(Expression expr, Object table);
	public Object visit(Cases case_list, Object table);
	public Object visit(method m, Object table);
	public Object visit(attr a, Object table);
	public Object visit(formalc formal, Object table);
	public Object visit(branch b, Object table);
	public Object visit(assign a, Object table);
	public Object visit(static_dispatch sd, Object table);
	public Object visit(dispatch d, Object table);
	public Object visit(cond c, Object table);
	public Object visit(loop l, Object table);
	public Object visit(typcase tc, Object table);
	public Object visit(block b, Object table);
	public Object visit(let l, Object table);
	public Object visit(plus e, Object table);
	public Object visit(sub e, Object table);
	public Object visit(mul e, Object table);
	public Object visit(divide e, Object table);
	public Object visit(neg e, Object table);
	public Object visit(lt e, Object table);
	public Object visit(eq e, Object table);
	public Object visit(leq e, Object table);
	public Object visit(comp e, Object table);
	public Object visit(int_const i, Object table);
	public Object visit(bool_const b, Object table);
	public Object visit(string_const s, Object table);
	public Object visit(new_ n, Object table);
	public Object visit(isvoid iv, Object table);
	public Object visit(no_expr ne, Object table);
	public Object visit(object o, Object table);
}
