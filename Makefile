TITLE = ccal

${TITLE}: ${TITLE}.class

${TITLE}.class: ${TITLE}Lexer.class ${TITLE}Parser.class VisitorTAC.class SemanticError.class

VisitorTAC.class: Utils.class SymbolTable.class TypeSignature.class
SymbolTable.class: Utils.class Symbol.class
Symbol.class: TypeSignature.class

%Lexer.java %Parser.java %.tokens %Listener.java %BaseListener.java %Visitor.java %BaseVisitor.java: %.g4
	antlr -visitor $<

%.class: %.java
	javac -g $<

clean:
	rm -f *.class  ${TITLE}*r.* *.interp *.tokens *.ir

testfile = 2.ccl
test: ${TITLE}
	java $< ${testfile}

debug: ${TITLE}
	rlwrap jdb $< ${testfile}

%.ir: %.ccl ${TITLE}
	java ${TITLE} $< > $@

.PHONY: clean test ${TITLE} debug
