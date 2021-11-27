TITLE = ccal

${TITLE}:
# this isn't how makefile are supposed to be used but doing it the proper way would be too long
	antlr ccal.g4
	javac -classpath '/nix/store/xj0m2x4kzs3jg6j02n1b1h3vq5xg17fw-antlr-4.8/share/java/antlr-4.8-complete.jar' *.java
	grun ccal r -gui *.ccal
  

%.class: %.java
	javac -classpath '/nix/store/xj0m2x4kzs3jg6j02n1b1h3vq5xg17fw-antlr-4.8/share/java/antlr-4.8-complete.jar' $<

clean:
	-rm -f *.class  ${TITLE}*er.* *.interp *.tokens

test: ${TITLE}
	for file in *.ccal; do \
		java ccal < $$file; \
	done

.PHONY: clean test
