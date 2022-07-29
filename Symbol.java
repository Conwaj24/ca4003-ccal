public class Symbol {
	TypeSignature t;
	private String value;

	Symbol(TypeSignature t, String value) {
		this.t = t;
		this.value = value;
	}
	Symbol(String type, String value) {
		this(new TypeSignature(type), value);
	}
	Symbol(TypeSignature t) {
		this(t, null);
	}

	public String get() {
		return value;
	}

	public void assign(Symbol s) {
		if (t.equals(s.t))
			value = s.get();
	}
	public void assign(String s) {
		value = s;
	}
}

class ConstSymbol extends Symbol {
	ConstSymbol(TypeSignature t, String value) {
		super(t, value);
	}
	ConstSymbol(String type, String value) {
		super(type, value);
	}
	public void assign(String s) {
		throw new AssignToConst("¯\\_(ツ)_/¯");
	}
}
