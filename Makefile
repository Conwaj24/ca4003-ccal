TITLE = ccal

${TITLE}: ${TITLE}.class

${TITLE}.class: ${TITLE}Lexer.class ${TITLE}Parser.class VisitorTAC.class

%Lexer.java %Parser.java %.tokens %Listener.java %BaseListener.java %Visitor.java %BaseVisitor.java: %.g4
	antlr -visitor $<

%.class: %.java
	javac -g $<

clean:
	rm -f *.class  ${TITLE}*r.* *.interp *.tokens

testfile = 1.ccl
test: ${TITLE}
	java $< ${testfile}

debug: ${TITLE}
	rlwrap jdb $< ${testfile}

.PHONY: clean test ${TITLE}
