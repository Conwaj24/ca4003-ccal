{ pkgs ? import <nixpkgs> {} }:

(pkgs.buildFHSUserEnv {
  name = "antlr-fhs-env";
  targetPkgs = pkgs: with pkgs; [ 
    antlr4
    jdk8
    eclipses.eclipse-java
  ];
  profile = '' 
    export SHELL=zsh
    export CLASSPATH="${pkgs.antlr4}/share/java/antlr-4.8-complete.jar:."
  '';
  extraOutputsToInstall = [ "dev" ];
  runScript = ''$SHELL'';
}).env
