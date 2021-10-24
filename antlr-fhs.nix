{ pkgs ? import <nixpkgs> {} }:

(pkgs.buildFHSUserEnv {
  name = "antlr-fhs-env";
  targetPkgs = pkgs: with pkgs; [ 
    antlr4
    jdk8
  ];
  profile = '' 
    export SHELL=zsh
    alias javac='/usr/bin/javac --class-path=${pkgs.antlr4}/share/java/*'
  '';
  extraOutputsToInstall = [ "dev" ];
  runScript = ''$SHELL'';
}).env
