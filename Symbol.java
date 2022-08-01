public class Symbol {
	public TypeSignature type;
	private String value;

	Symbol(TypeSignature t, String value) {
		this.type = t;
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

	public void assign(Symbol s) throws AssignmentMismatch{
		if (type.equals(s.type))
			value = s.get();
		else
			throw new AssignmentMismatch();
	}
	public void assign(String s) throws AssignToConst {
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
	public void assign(String s) throws AssignToConst {
		throw new AssignToConst();
	}
}
