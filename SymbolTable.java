import java.util.*;

class SymbolContext {
	public SymbolTable symbolTable;
	public Symbol symbol;

	SymbolContext(SymbolTable t, Symbol s) {
		symbolTable = t;
		symbol = s;
	}
}

public class SymbolTable
{
	public SymbolTable parent = null;
	int tempCount = 0;
	int labelCount = 0;
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

	SymbolContext getContext(String id) throws UnknownSymbol {
		id = basename(id);
		Symbol out = data.get(id);
		if (out == null) {
			if (parent == null)
				throw new UnknownSymbol();
			return parent.getContext(id);
		}
		return new SymbolContext (this, out);
	}

	void checkForValue(Symbol s) throws UnassignedSymbol {
		if (s.get() == null)
			throw new UnassignedSymbol();
	}

	public String getFullID(String id) throws UnknownSymbol, UnassignedSymbol {
		SymbolContext sc = getContext(id);
		checkForValue(sc.symbol);
		return sc.symbolTable.entryID(basename(id));
	}

	public String getValue(String id) throws UnassignedSymbol, UnknownSymbol {
		Symbol s = get(id);
		checkForValue(s);
		return s.get();
	}
	public Symbol get(String id) throws UnknownSymbol{
		return getContext(id).symbol;
	}

	public String declare(String id, Symbol s) {
		data.put(id, s);
		return entryID(id);
	}
	public String declare(String id, String type, String value) {
		return declare(id, new Symbol(type, value));
	}
	public String declare(String id, String type) {
		return declare(id, type, null);
	}

	public String assign(String id, String value) throws UnknownSymbol, AssignToConst{
		SymbolContext sc = getContext(id);
		sc.symbol.assign(value);
		return sc.symbolTable.entryID(basename(id));
	}

	public String temporary(Symbol s) {
		String id = "t" + tempCount++;
		data.put(id, s);
		return entryID(id);
	}

	public String label() {
		return entryID("L" + labelCount++);
	}
}
