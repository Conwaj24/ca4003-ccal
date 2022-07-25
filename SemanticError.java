public class SemanticError extends Throwable{
	public SemanticError(String s) {
		super(red(s));
		raise(this);
	}
	static String red(String s) {
		return String.format("\u001B[31m%s\u001B[0m");
	}
	<E extends Throwable> void raise(Throwable e) throws E {
		throw (E) e;
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


