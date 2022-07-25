public class Symbol {
	TypeSignature t;
	String value;

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
		else
			new UnassignedSymbol("hi");
	}
	public void assign(String s) {
		value = s;
	}
}
