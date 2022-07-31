import org.antlr.v4.runtime.tree.TerminalNode;

public class VisitorTAC extends ccalBaseVisitor<String> {
	SymbolTable st = new SymbolTable();

	@Override
	public String visitProgram(ccalParser.ProgramContext ctx) {
		jump("main");
		return super.visitProgram(ctx);
	}

	@Override
	public String visitMain(ccalParser.MainContext ctx) {
		String id = "main";
		label(id);
		st = st.sub(id);
		super.visitMain(ctx);
		st = st.parent;
		return id;
	}

	@Override
	public String visitFunction(ccalParser.FunctionContext ctx) {
		st = st.sub(ctx.ID().getText());
		String id = st.parent.declare(
				ctx.ID().getText(),
				new Symbol(tsig(ctx.type()).takes()) //TODO after conversion to <Symbol>
		);
		System.out.println(id + ":");
		super.visitFunction(ctx);
		System.out.println("return " + visit(ctx.expression()) + ";");
		st = st.parent;
		return id;
	}
	@Override
	public String visitNemp_parameter_list(ccalParser.Nemp_parameter_listContext ctx) {
		String out = visit(ctx.parameter());
		try
			{ out += " " + visit(ctx.nemp_parameter_list()); }
		catch (NullPointerException e) {}
		return out;
	}
	@Override
	public String visitParameter(ccalParser.ParameterContext ctx) {
		String id = st.declare(ctx.ID().getText(), ctx.type().getText());
		threeAddressCode(id, "getparam");
		return id;
	}


	@Override
	public String visitFunction_call(ccalParser.Function_callContext ctx) {
		int argc = 0;

		for (ccalParser.Nemp_arg_listContext arg = ctx.arg_list().nemp_arg_list(); arg != null; arg = arg.nemp_arg_list()) {
			argc++;
			System.out.println("param " + globalID(arg.ID()));
		}
		return (String.format("call %s %d", ctx.ID().getText(), argc));
	}

	@Override
	public String visitVar_decl(ccalParser.Var_declContext ctx) {
		return st.declare(ctx.ID().getText(), ctx.type().getText());
	}

	@Override
	public String visitConst_decl(ccalParser.Const_declContext ctx) {
		String v = deref(visit(ctx.expression()));
		String id = st.declare( ctx.ID().getText(), new ConstSymbol( tsig(ctx.type()), v));
		threeAddressCode(id, v, null, null);

		return id;
	}
	@Override
	public String visitAssignment(ccalParser.AssignmentContext ctx) {
		String id = st.assign(ctx.ID().getText(), visit(ctx.expression()));
		try {
			threeAddressCode (
				id,
				st.getValue(id),
				null,
				null
			);
		} catch (UnassignedSymbol e) {
			threeAddressCode (
				id,
				e.toString(),
				null,
				null
			);

		}
		return id;

	}

	//expression
	@Override
	public String visitFrag_expr(ccalParser.Frag_exprContext ctx) {
		return visit(ctx.frag());
	}
	@Override
	public String visitArithmetic_expr(ccalParser.Arithmetic_exprContext ctx) {
		String id = st.temporary(new Symbol("integer", visit(ctx.frag())));
		threeAddressCode (
			id,
			visit(ctx.frag()),
			ctx.binary_arith_op().getText(),
			visit(ctx.expression())
		);
		return id;
	}

	//frag
	@Override
	public String visitId_frag(ccalParser.Id_fragContext ctx) {
		return globalID(ctx.ID());
	}
	@Override
	public String visitNeg_frag(ccalParser.Neg_fragContext ctx) {
		if ( !isInt(st.get(ctx.ID().getText())) )
			throw new OperatorMismatch(ctx.SUB().getText(), "integer");
		return globalID(ctx.ID());
	}
	@Override
	public String visitNum_literal(ccalParser.Num_literalContext ctx) {
		return ctx.NUMBER().getText();
	}
	@Override
	public String visitBool_literal(ccalParser.Bool_literalContext ctx) {
		return ctx.bool().getText();
	}
	@Override
	public String visitExpression_frag(ccalParser.Expression_fragContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public String visitBranch(ccalParser.BranchContext ctx) {
		String id = st.label();

		jumpIfNot(visit(ctx.condition()), conditionLabel(id, false));

		label(conditionLabel(id, true));
		st = st.sub("pass");
		visit(ctx.statement_block(0));
		st = st.parent;
		jump(id + ".end");

		label(conditionLabel(id, false));
		st = st.sub("skip");
		visit(ctx.statement_block(1));
		st = st.parent;

		label(id + ".end");

		return null;
	}

	//condition
	@Override
	public String visitCondition(ccalParser.ConditionContext ctx) {
		if (ctx.OR() != null)
			return visitOr(ctx);
		if (ctx.AND() != null)
			return visitAnd(ctx);
		if (ctx.comp_op() != null)
			return visitComparison(ctx);
		return visit(ctx.condition(0));
	}

	public String visitOr(ccalParser.ConditionContext ctx) {
		jumpIf(visit(ctx.condition(0)), conditionLabel(true));
		return visit(ctx.condition(1));
	}
	public String visitAnd(ccalParser.ConditionContext ctx) {
		jumpIfNot(visit(ctx.condition(0)), conditionLabel(false));
		return visit(ctx.condition(1));
	}

	public String visitComparison(ccalParser.ConditionContext ctx) {
		return visit(ctx.expression(0)) + ctx.comp_op().getText() + visit(ctx.expression(1));
	}

	String globalID(TerminalNode idNode) {
		return st.getFullID(idNode.getText());
	}

	String deref(String s) {
		try {
			return st.getValue(s);
		} catch(UnknownSymbol e) {
			return s;
		}
	}

	static boolean isInt(Symbol s) {
		return s.type.equals(new TypeSignature("integer"));
	}

	static TypeSignature tsig(ccalParser.TypeContext ctx) {
		return new TypeSignature(ctx.getText());
	}
	String conditionLabel(String name, boolean b) {
			return name + "." + (b ? "pass" : "skip");
	}
	String conditionLabel(boolean b) {
			return st.entryID(b ? "pass" : "skip");
	}

	static void label(String s){
		System.out.println(s + ":");
	}
	static void jump(String l){
		System.out.println("goto " + l);
	}
	static void jumpIf(String condition, String l) {
		System.out.println(String.format("if %s goto %s", condition, l));
	}
	static void jumpIfNot(String condition, String l) {
		System.out.println(String.format("ifz %s goto %s", condition, l));
	}

	void threeAddressCode(String a0, String a1) {
		threeAddressCode(a0, a1, null, null);
	}
	static void threeAddressCode(String a0, String a1, String op, String a2) {
		System.out.println(String.format(
				"%s %s %s",
				Utils.concatIfAllNonzero(a0, "="),
				a1,
				Utils.concatIfAllNonzero(op, a2)
				));
	}
}
