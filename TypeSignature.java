public class TypeSignature
{
	public String name;

	TypeSignature(String name){
		this.name = name;
	}
	public FunctionSignature takes(TypeSignature... ts) {
		return new FunctionSignature(name, ts);
	}
	public boolean equals(TypeSignature t) {
		return name.equalsIgnoreCase(t.name);
	}
}

class FunctionSignature extends TypeSignature {
	public TypeSignature[] inputs;

	FunctionSignature(String returnType, TypeSignature... inputs) {
		super(returnType);
		this.inputs = inputs;
	}

	public boolean equals(FunctionSignature t) {
		return super.equals(t) && inputsEqual(t.inputs);
	}

	boolean inputsEqual(TypeSignature[] ts) {
		if (inputs.length != ts.length)
			return false;
		for (int i = 0; i < ts.length; i++) {
			if (! (inputs[i].equals(ts[i])))
				return false;
		}
		return true;
	}

	public boolean equals(TypeSignature t) {
		return false;
	}
}

