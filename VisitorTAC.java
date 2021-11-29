public class VisitorTAC extends ccalBaseVisitor<String> {
	@Override
	public String visitFunction(ccalParser.FunctionContext ctx) {
		System.out.println(ctx.ID().getText());
		return null;
	}
}
