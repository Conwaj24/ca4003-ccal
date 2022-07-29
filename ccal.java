import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class ccal {
	public static void main (String[] args) throws Exception {
		InputStream[] ccalFiles;
		if (args.length > 0) {
			ccalFiles = new InputStream[args.length];
			for (int i = 0; i < args.length; i++) {
				ccalFiles[i] = new FileInputStream(args[i]);
			}
		} else
			ccalFiles = new InputStream[] {System.in};

		for (InputStream in : ccalFiles)
			compile(in);
	}

	static ParseTree parse(InputStream in) throws IOException {
		ccalLexer lexer = new ccalLexer (CharStreams.fromStream(in));
		CommonTokenStream tokens = new CommonTokenStream (lexer);
		ccalParser parser = new ccalParser (tokens);

		return parser.program();
	}

	static void compile(InputStream in) throws Exception{
		ParseTree tree = parse(in);
		VisitorTAC tac = new VisitorTAC();
		tac.visit(tree);
	}
}
