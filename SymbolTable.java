import java.util.*;

public class SymbolTable
{
	public SymbolTable parent = null;
	int tempCount = 0;
	HashMap<String,Symbol> data = new HashMap<>();;
	String namespace = "";

	public SymbolTable() { }
	public SymbolTable(SymbolTable parent, String namespace) {
		this.parent = parent;
		this.namespace = namespace;
	}

	public SymbolTable sub(String namespace){
		return new SymbolTable(this, namespace);
	}

	 String entryID(String id) {
		String out = Utils.concatTrailingNonzeroes(namespace, id);
		if (parent != null)
			return parent.entryID(out);
		return out;
	}
	String basename(String entryID) {
		String[] items = entryID.split("\\.");
		if (items.length == 0)
			return null;
		return items[items.length -1];
	}

	public Symbol get(String id){
		id = basename(id);
		Symbol out = data.get(id);
		if (out == null && parent != null)
			return parent.get(id);
		return out;
	}

	public Set<Map.Entry<String,Symbol>> getLocals(){
		return data.entrySet();
	}

	public Set<Map.Entry<String,Symbol>> getAll(){
		Set<Map.Entry<String,Symbol>> out = getLocals();
		if (parent == null)
			out.addAll(parent.getLocals());
		return out;
	}

	public String declare(String id, Symbol s) {
		data.put(id, s);
		return entryID(id);
	}
	public String declare(String id, String type) {
		return declare(id, new Symbol(type, null));
	}

	public void assign(String id, String value) {
		id = basename(id);
		get(id).value = value;
	}

	public String temporary(Symbol s) {
		String id = "t" + tempCount++;
		data.put(id, s);
		return entryID(id);
	}
}
