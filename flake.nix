
{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs { inherit system; };
    in {
      devShells.default = pkgs.mkShell {
        # Show a friendly prompt
        name = "java-dev-shell";

        packages = with pkgs; [
          openjdk17            
          maven                
          gradle              
          coursier           
        ];

        JAVA_HOME = "${pkgs.openjdk17}";
      };
    });
}
