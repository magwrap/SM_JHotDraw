{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs { inherit system; };
      jdk = pkgs.openjdk17;
    in {
      packages.default = pkgs.maven.buildMavenPackage {
        pname = "jhotdraw";
        version = "9.1-SNAPSHOT";
        src = ./.;

        mvnJdk = jdk;

        mvnHash = "sha256-fKteUJzzFsIFWSAQYLaiuPItH+FYMJsYViMVTfcwt+4=";

        nativeBuildInputs = with pkgs; [ makeWrapper ];

        mvnParameters = "-DskipTests";

        installPhase = ''
          runHook preInstall
          mkdir -p $out/share/java $out/bin
          find . -path '*/target/*.jar' -not -name 'original-*' -exec cp {} $out/share/java/ \;
          classpath=$(find $out/share/java -name '*.jar' | sort | tr '\n' ':')
          makeWrapper ${jdk}/bin/java $out/bin/jhotdraw \
            --add-flags "-cp" \
            --add-flags "$classpath" \
            --add-flags "org.jhotdraw.samples.svg.Main"
          runHook postInstall
        '';

        meta.mainProgram = "jhotdraw";
      };

      apps.svg = flake-utils.lib.mkApp {
        drv = pkgs.writeShellScriptBin "jhotdraw-svg" ''
          classpath=$(find "${self.packages.${system}.default}/share/java" -name '*.jar' | sort | tr '\n' ':')
          exec ${jdk}/bin/java -cp "$classpath" org.jhotdraw.samples.svg.Main "$@"
        '';
      };
      apps.draw = flake-utils.lib.mkApp {
        drv = pkgs.writeShellScriptBin "jhotdraw-draw" ''
          classpath=$(find "${self.packages.${system}.default}/share/java" -name '*.jar' | sort | tr '\n' ':')
          exec ${jdk}/bin/java -cp "$classpath" org.jhotdraw.samples.draw.Main "$@"
        '';
      };

      devShells.default = pkgs.mkShell {
        name = "java-dev-shell";
        packages = with pkgs; [
          jdk
          maven
          gradle
          coursier
        ];
        JAVA_HOME = "${jdk}";
      };
    });
}
