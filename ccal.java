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
	static void compile(InputStream in) throws Exception{
		ccalLexer lexer = new ccalLexer (CharStreams.fromStream(in));
		CommonTokenStream tokens = new CommonTokenStream (lexer);
		ccalParser parser = new ccalParser (tokens);

		ParseTree tree = parser.program();
  
		//System.out.println (tree.toStringTree(parser));
		VisitorTAC tac = new VisitorTAC();
		System.out.println(tac.visit(tree));
	}
}
