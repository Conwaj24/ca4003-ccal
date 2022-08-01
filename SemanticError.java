import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class SemanticError extends Throwable{
	public void display(Token t, String s) {
		System.err.println(boldWhite(position(t)) + " " + s);
	}

	static String position(Token t) {
		return String.format("%d:%d", t.getLine(), t.getCharPositionInLine());
	}
	static String red(String s) {
		return String.format("\u001B[31m%s\u001B[0m", s);
	}
	static String boldWhite(ParseTree t) {
		return boldWhite(t.getText());
	}
	static String boldWhite(String s) {
		return String.format("\u001B[37;1m%s\u001B[0m", s);
	}
}

class UnknownSymbol extends SemanticError {
	void display(TerminalNode id) {
		super.display(id.getSymbol(), red("Could not find symbol: ") + boldWhite(id) );
	}
}

class UnassignedSymbol extends SemanticError {
	void display(TerminalNode id) {
		super.display(id.getSymbol(), red("Symbol, ") + boldWhite(id) + red(", has no value") );
	}
}

class AssignToConst extends SemanticError {
	void display(TerminalNode id) {
		super.display(id.getSymbol(), red("Const, ") +  boldWhite(id) + red(" cannot be overwritten"));
	}
}

class OperatorMismatch extends SemanticError {
	void display(Token t, String op, String expectedType) {
		super.display(t, red("Operator, ") + boldWhite(op) + red(", can only be used with type, ") + boldWhite(expectedType) );
	}
	void display(ParserRuleContext op, String expectedType) {
		display(op.getStart(), op.getText(), expectedType);
	}
	void display(TerminalNode op, String expectedType) {
		display(op.getSymbol(), op.getText(), expectedType);
	}
}

class AssignmentMismatch extends SemanticError {
	void display(TerminalNode id, String rOperand, String expectedType) {
		super.display(id.getSymbol(), red("Cannot assign ") + boldWhite(id) + red(" to ") + boldWhite(rOperand) + red(", must be of type; ") + boldWhite(expectedType) );
	}
	void display(TerminalNode id, String rOperand, TypeSignature expectedType) {
		display(id, rOperand, expectedType.name);
	}
}
