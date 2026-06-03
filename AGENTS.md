# AGENTS.md

## Project Scope

This repository is a small Maven-based Java game that runs on the `enginefx` library. Most gameplay behavior lives in scene classes under `src/main/java/br/com/game/niveis/examples`, while startup and scene registration are driven by `src/main/resources/application.json`.

For a deeper architectural reference, see [Project_Architecture_Blueprint.md](Project_Architecture_Blueprint.md).

## Build And Run

- Build sources: `mvn compile`
- Package shaded jar: `mvn package`
- Run packaged jar: `java --enable-native-access=ALL-UNNAMED -jar target/dino.jar` (the flag silences LWJGL/Vulkan native-access warnings on Java 25)
- Tests: `mvn test` is valid but there is currently no `src/test` tree

## Control Surface

- Maven entrypoint and packaging: [pom.xml](pom.xml)
- Java entrypoint: [src/main/java/br/com/game/Main.java](src/main/java/br/com/game/Main.java)
- Runtime config and scene list: [src/main/resources/application.json](src/main/resources/application.json)
- Boot scene and scene selector menu: [src/main/java/br/com/game/niveis/examples/Menu.java](src/main/java/br/com/game/niveis/examples/Menu.java)
- Example custom script: [src/main/java/br/com/game/script/AndarEmTile.java](src/main/java/br/com/game/script/AndarEmTile.java)

`Main` only delegates to `Executor.loadGame(args)`. If behavior changes at startup, inspect `application.json` and the scene classes before changing `Main`.

## Conventions That Matter

- Preserve the existing package and asset naming, including Portuguese directory names such as `niveis`, `mapas`, `imagens`, and `mensages`.
- Treat `src/main/resources` as the source of truth for maps, audio, fonts, and images. Do not edit generated files under `target/`.
- When adding, removing, or reordering scenes, update both the `scenes` array in `application.json` and the hardcoded menu entries in `Menu.java`. The menu uses `ControleBase.getInstance().nextScene(current)`, so menu order and config order must stay aligned.
- Scene behavior is typically implemented in `Scene.setup()` and `Scene.update(long time)`. Follow the existing style in neighboring scene classes instead of introducing a new application structure.
- The engine rebuilds a fresh scene instance on every switch, so scene fields do not persist across visits. Put per-visit initialization in `setup()`.
- The engine frame loop is uncapped. Scale movement by `Time.getDeltaTime()` (pixels per second), as `Spaceship` and `Level002` do; fixed pixels-per-frame movement runs too fast at high frame rates.
- Sprite sheets must be a uniform grid for `new Sprite(name, cx, cy)`. Use uniform assets (for example `Sonic_anim.png`); non-uniform sheets drift and clip.
- TMX `<image source>` paths must be relative to `src/main/resources` (for example `../imagens/...`), never absolute machine paths, or the map fails to load at runtime.
- Custom reusable behavior belongs under `src/main/java/br/com/game/script` and is attached to `GameObject` instances from scene classes.

## Editing Guidance

- Prefer small edits in the owning scene or script class instead of changing unrelated engine bootstrapping.
- If a change touches gameplay assets or TMX maps, verify the referenced resource exists under `src/main/resources` and that the resource name matches exactly.
- Keep validation narrow: use `mvn compile` for code changes and `java -jar target/dino.jar` when a runtime behavior check is needed.