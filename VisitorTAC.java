import org.antlr.v4.runtime.tree.TerminalNode;

public class VisitorTAC extends ccalBaseVisitor<String> {
	SymbolTable st = new SymbolTable();

	@Override
	public String visitProgram(ccalParser.ProgramContext ctx) {
		System.out.println("goto main");
		return super.visitProgram(ctx);
	}

	@Override
	public String visitMain(ccalParser.MainContext ctx) {
		System.out.println("main:");
		st = st.sub("main");
		super.visitMain(ctx);
		st = st.parent;
		return null;
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
		System.out.println(threeAddressCode(id, "getparam"));
		return id;
	}


	@Override
	public String visitId_frag(ccalParser.Id_fragContext ctx) {
		return globalID(ctx.ID());
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
	public String visitExpression_frag(ccalParser.Expression_fragContext ctx) {
		return visit(ctx.expression());
	}
	@Override
	public String visitAssignment(ccalParser.AssignmentContext ctx) {
		String id = st.assign(ctx.ID().getText(), visit(ctx.expression()));
		System.out.println(threeAddressCode (
			id,
			st.getValue(id),
			null,
			null
		));
		return id;

	}

	@Override
	public String visitArithmetic_expr(ccalParser.Arithmetic_exprContext ctx) {
		String id = st.temporary(new Symbol("integer", visit(ctx.frag())));
		System.out.println(threeAddressCode (
			id,
			visit(ctx.frag()),
			ctx.binary_arith_op().getText(),
			visit(ctx.expression())
		));
		return id;
	}

	@Override
	public String visitNum_literal(ccalParser.Num_literalContext ctx) {
		return ctx.NUMBER().getText();
	}

	String globalID(TerminalNode idNode) {
		return st.getFullID(idNode.getText());
	}
	static TypeSignature tsig(ccalParser.TypeContext ctx) {
		return new TypeSignature(ctx.getText());
	}
	static String threeAddressCode(String a0, String a1) {
		return threeAddressCode(a0, a1, null, null);
	}
	static String threeAddressCode(String a0, String a1, String op, String a2) {
		return String.format(
				"%s %s %s",
				Utils.concatIfAllNonzero(a0, "="),
				a1,
				Utils.concatIfAllNonzero(op, a2)
				);
	}
}
