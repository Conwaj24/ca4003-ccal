public class VisitorTAC extends ccalBaseVisitor<String> {
	int tempCount = 0;

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
		String id = "t" + tempCount++;
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
