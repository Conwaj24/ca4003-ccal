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
		label(id);
		super.visitFunction(ctx);
		System.out.println("return " + visit(ctx.expression()));
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
		String id = st.declare(ctx.ID().getText(), ctx.type().getText(), "somevalue");
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
		String id = st.declare( ctx.ID().getText(), new ConstSymbol( tsig(ctx.type()), visit(ctx.expression())));
		threeAddressCode(id, ctx.ID());
		return id;
	}

	@Override
	public String visitAssignment(ccalParser.AssignmentContext ctx) {
		try {
			String id = st.assign(ctx.ID().getText(), visit(ctx.expression()));
			threeAddressCode (id, ctx.ID());
			return id;
		} catch(UnknownSymbol e) {
			e.display(ctx.ID());
		} catch(AssignToConst e) {
			e.display(ctx.ID());
		}
		return ctx.ID().getText();

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
	public String visitFrag(ccalParser.FragContext ctx) {
		String out = visitNumericFrag(ctx);
		if (out != null)
			return out;
		out = visitBooleanFrag(ctx);
		if (out != null)
			return out;
		return visitSymbolicFrag(ctx);
	}

	public String visitNumericFrag(ccalParser.FragContext ctx) {
		if (ctx.SUB() != null)
			return visitNeg_frag(ctx);
		if (ctx.NUMBER() != null)
			return ctx.NUMBER().getText();
		if (ctx.frag() != null)
			return visitNumericFrag(ctx.frag());
		return null;
	}
	public String visitBooleanFrag(ccalParser.FragContext ctx) {
		if (ctx.bool() != null)
			return ctx.bool().getText();
		return null;
	}
	public String visitSymbolicFrag(ccalParser.FragContext ctx) {
		if (ctx.expression() != null)
			return visit(ctx.expression());
		if (ctx.ID() != null && ctx.SUB() == null)
			return globalID(ctx.ID());
		return null;
	}

	public String visitNeg_frag(ccalParser.FragContext ctx) {
		try {
			if ( !isInt(st.get(ctx.ID().getText())) )
				new OperatorMismatch().display(ctx.SUB(), "integer");

			String id = st.temporary(new Symbol("integer", "-" + globalID(ctx.ID())));
			threeAddressCode(id, ctx.ID());
			return id;
		} catch (UnknownSymbol e) {
			e.display(ctx.ID());
		}
		return null;
	}

	@Override
	public String visitLoop(ccalParser.LoopContext ctx) {
		String start = st.label();
		String end = st.label();

		jumpIfNot(visit(ctx.condition(), start, end), end);

		label(start);
		visit(ctx.statement_block());
		jump(end);

		label(end);

		return start;
	}
	@Override
	public String visitBranch(ccalParser.BranchContext ctx) {
		String lIf = st.label();
		String lElse = st.label();
		String lEnd = st.label();

		jumpIfNot(visit(ctx.condition(), lIf, lElse), lElse);

		label(lIf);
		visit(ctx.statement_block(0));
		jump(lEnd);

		label(lElse);
		visit(ctx.statement_block(1));

		label(lEnd);

		return lIf;
	}

	//condition
	public String visit(ccalParser.ConditionContext ctx, String passLabel, String skipLabel) {
		if (ctx.OR() != null)
			return visitOr(ctx, passLabel, skipLabel);
		if (ctx.AND() != null)
			return visitAnd(ctx, passLabel, skipLabel);
		if (ctx.comp_op() != null)
			return visitComparison(ctx);
		return visit(ctx.condition(0), passLabel, skipLabel); //this is wrong
	}
	public String visitOr(ccalParser.ConditionContext ctx, String passLabel, String skipLabel) {
		jumpIf(visit(ctx.condition(0), passLabel, skipLabel), passLabel);
		return visit(ctx.condition(1), passLabel, skipLabel);
	}
	public String visitAnd(ccalParser.ConditionContext ctx, String passLabel, String skipLabel) {
		jumpIfNot(visit(ctx.condition(0), passLabel, skipLabel), skipLabel);
		return visit(ctx.condition(1), passLabel, skipLabel);
	}
	public String visitComparison(ccalParser.ConditionContext ctx) {
		return visit(ctx.expression(0)) + ctx.comp_op().getText() + visit(ctx.expression(1));
	}

	String globalID(TerminalNode idNode) {
		try {
			return st.getFullID(idNode.getText());
		} catch (UnknownSymbol e) {
			e.display(idNode);
		} catch (UnassignedSymbol e) {
			e.display(idNode);
		}
		return idNode.getText();
	}

	String deref(String s) throws UnassignedSymbol{
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

	void threeAddressCode(String id, TerminalNode idNode) {
		try {
			threeAddressCode(id);
		} catch (UnassignedSymbol e) {
			e.display(idNode);
		} catch (UnknownSymbol e) {
			e.display(idNode);
		}
	}

	void threeAddressCode(String id) throws UnassignedSymbol, UnknownSymbol {
		threeAddressCode(id, st.getValue(id));
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
