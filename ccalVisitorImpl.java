import java.util.HashMap;
import java.util.Map;

public class EvalVisitor extends slpIBaseVisitor<Integer> {
	Map<String, Integer> memory = new HashMap<String, Integer>();

	@Override
	public Integer visitAssign (slpIParser.AssignContext ctx) {
		String id=ctx.ID().getText();
		int value=visit(ctx.expr());
		memory.put(id, value);
		return value;
	}

	@Override
	public Integer visitPrint (slpIParser.PrintContext ctx) {
		int numberOfExprs = (ctx.getChildCount() - 4 + 1) / 2;
		for (int i=0; i<numberOfExprs; i++)
			System.out.print(visit(ctx.expr(i))+" ");
		System.out.println();
		return 0;
	}
	@Override
	public Integer visitNumber (slpIParser.NumberContext ctx) {
		return Integer.valueOf(ctx.NUMBER().getText());
	}

	@Override
	public Integer visitId (slpIParser.IdContext ctx) {
		String id=ctx.ID().getText();
		if (memory.containsKey (id))
			return memory.get(id);
		return 0;
	}

	@Override
	public Integer visitMulOp (slpIParser.MulOpContext ctx) {
		int left=visit(ctx.expr(0));
		int right=visit (ctx.expr(1));
		if (ctx.op.getType()==slpIParser.MULT)
			return left * right;
		return left / right;
	}

	@Override
	public Integer visitPlusOp (slpIParser.PlusOpContext ctx) {
		int left=visit(ctx.expr(0));
		int right=visit (ctx.expr(1));
		if (ctx.op.getType()==slpIParser.PLUS)
			return left+right;
		return left-right;
	}

	@Override
	public Integer visitParens (slpIParser.ParensContext ctx) {
		return visit(ctx.expr());
	}
}
