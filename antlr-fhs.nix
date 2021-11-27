{ pkgs ? import <nixpkgs> {} }:

(pkgs.buildFHSUserEnv {
  name = "antlr-fhs-env";
  targetPkgs = pkgs: with pkgs; [ 
    antlr4
    jdk8
  ];
  profile = '' 
    export SHELL=zsh
  '';
  extraOutputsToInstall = [ "dev" ];
  runScript = ''$SHELL'';
}).env
