antlr ccal.g4 &&
javac -classpath '/nix/store/xj0m2x4kzs3jg6j02n1b1h3vq5xg17fw-antlr-4.8/share/java/antlr-4.8-complete.jar' *.java &&
for file in *.ccal; do
	java ccal < $file
done;
