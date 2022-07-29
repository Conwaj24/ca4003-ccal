public class SemanticError extends RuntimeException{
	public SemanticError(String s) {
		super(red(s));
	}
	static String red(String s) {
		return String.format("\u001B[31m%s\u001B[0m", s);
	}
}

class UnknownSymbol extends SemanticError {
	UnknownSymbol(String id) {
		super(String.format("Could not find symbol: %s", id));
	}
}

class UnassignedSymbol extends SemanticError {
	UnassignedSymbol(String id) {
		super(String.format("Symbol, %s, has no value", id));
	}
}

class AssignToConst extends SemanticError {
	AssignToConst(String id) {
		super(String.format("Const, %s, cannot be overwritten", id));
	}
}

class OperatorMismatch extends SemanticError {
	OperatorMismatch(String op, String expectedType) {
		super(String.format("Operator, %s, can only be used with type, %s", op, expectedType));
	}
}
