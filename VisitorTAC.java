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
		//enter scope
		return super.visitMain(ctx);
		//exit scope
	}

	@Override
	public String visitFunction(ccalParser.FunctionContext ctx) {
		//enter scope
		String id = st.declare(
				ctx.ID().getText(),
				new Symbol(tsig(ctx.type()).takes()) //TODO after conversion to <Symbol>
		);
		System.out.println(id + ":");
		super.visitFunction(ctx);
		return id;
	}

	@Override
	public String visitFunction_call(ccalParser.Function_callContext ctx) {
		int argc = 0;

		for (ccalParser.Nemp_arg_listContext arg = ctx.arg_list().nemp_arg_list(); arg != null; arg = arg.nemp_arg_list()) {
			argc++;
			System.out.println("param " + arg.ID().getText());
		}
		System.out.println(String.format("call %s %d", ctx.ID().getText(), argc));
		return super.visitFunction_call(ctx);
	}

	@Override
	public String visitVar_decl(ccalParser.Var_declContext ctx) {
		String id = ctx.ID().getText();
		st.declare(id, ctx.type().getText());
		return id;
	}
	@Override
	public String visitExpression_frag(ccalParser.Expression_fragContext ctx) {
		return visit(ctx.expression());
	}
	@Override
	public String visitAssignment(ccalParser.AssignmentContext ctx) {

		String id = ctx.ID().getText();
		System.out.println(threeAddressCode (
			id,
			visit(ctx.expression()),
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
	public String visitId_frag(ccalParser.Id_fragContext ctx) {
		return ctx.ID().getText(); }
	@Override
	public String visitNum_literal(ccalParser.Num_literalContext ctx) {
		return ctx.NUMBER().getText();
	}

	static TypeSignature tsig(ccalParser.TypeContext ctx) {
		return new TypeSignature(ctx.getText());
	}
	static String threeAddressCode(String a0, String a1, String op, String a2) {
		return String.format(
				"%s %s %s",
				concatIfAllNonzero(a0, "="),
				a1,
				concatIfAllNonzero(op, a2)
				);
	}
	static boolean nonzero(String s) {
		try {
			return !s.isEmpty();
		} catch (java.lang.NullPointerException e) {
			return false;
		}
	}
	static String concatIfAllNonzero(String... args) {
		return concatIfAllNonzero(args, " ");
	}
	static String concatIfAllNonzero(String[] args, String separator) {
		if (args.length == 0)
			return "";

		String out = args[0];
		if (!nonzero(out))
			return "";

		for ( int i = 1; i < args.length; i++ ) {
			String s = args[i];
			if (!nonzero(s))
				return "";
			out = out.concat(separator);
			out = out.concat(s);
		}
		return out;
	}
}
