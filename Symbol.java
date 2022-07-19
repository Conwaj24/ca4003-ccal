public class Symbol {
	public TypeSignature t;
	public String value;

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
}
