TITLE = ccal

${TITLE}: ${TITLE}.class

${TITLE}.class: ${TITLE}Lexer.class ${TITLE}Parser.class VisitorTAC.class

%Lexer.java %Parser.java %.tokens %Listener.java %BaseListener.java %Visitor.java %BaseVisitor.java: %.g4
	antlr -visitor $<

%.class: %.java
	javac $<

clean:
	rm -f *.class  ${TITLE}*r.* *.interp *.tokens

test: ${TITLE}
	java $< expressions.ccl

.PHONY: clean test ${TITLE}
