TITLE = ccal
ANTLRPATH = /nix/store/xj0m2x4kzs3jg6j02n1b1h3vq5xg17fw-antlr-4.8/share/java/antlr-4.8-complete.jar
CLASSFLAGS = -classpath "${ANTLRPATH}:."

${TITLE}: ${TITLE}.class

${TITLE}.class: ${TITLE}Lexer.class ${TITLE}Parser.class

%Lexer.java %Parser.java %.tokens %Listener.java %BaseListener.java %Visitor.java %BaseVisitor.java: %.g4
	antlr -visitor $<

%.class: %.java
	javac ${CLASSFLAGS} $<

clean:
	rm -f *.class  ${TITLE}*r.* *.interp *.tokens

test: ${TITLE}
	java ${CLASSFLAGS} $< *.ccal

.PHONY: clean test ${TITLE}
