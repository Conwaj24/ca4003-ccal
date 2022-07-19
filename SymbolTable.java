import java.util.HashMap;

public class SymbolTable
{
	int tempCount = 0;
	HashMap<String,Symbol> data = new HashMap<>();;
	SymbolTable parent = null;

	public SymbolTable() {}
	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
	}

	public Symbol get(String id){
		Symbol out = data.get(id);
		if (out == null && parent != null)
			return parent.get(id);
		return out;
	}

	public void declare(String id, Symbol s) {
		data.put(id, s);
	}
	public void declare(String id, String type) {
		data.put(id, new Symbol(type, null));
	}

	public void assign(String id, String value) {
		get(id).value = value;
	}

	public String temporary(Symbol s) {
		String id = "t" + tempCount++;
		data.put(id, s);
		return id;
	}
}
