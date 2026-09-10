# Orientações para trabalhar no dinofx

## Escopo

Jogo de exemplos que consome `enginefx:enginefx:2.0.0`. Leia [README.md](README.md), [BluePrint.md](BluePrint.md), [PRD.md](PRD.md) e [REVIEW.md](REVIEW.md).

O ponto de entrada [Main.java](src/main/java/br/com/game/Main.java) apenas chama `Executor.loadGame(args)`. Comportamento de inicialização é definido por [application.json](src/main/resources/application.json) e pelas cenas.

## Build e validação

- JDK 25+, Maven Wrapper; configuração atual de nativos para Windows x64.
- `.\build.ps1` instala o checkout irmão da engine e empacota o jogo.
- `.\mvnw.cmd test` executa JUnit em `src/test/java`.
- `.\build.ps1 -Smoke -PresentMode fifo` valida o JAR em pasta vazia e janela oculta.
- Testes unitários não substituem o smoke; o smoke não substitui gameplay manual, goldens ou validação de driver.
- Fontes Java existentes usam `windows-1252` no POM. Preserve encoding; documentação é UTF-8.
- Não edite arquivos gerados em `target/`. Assets têm como fonte de verdade `src/main/resources/`.

## Convenções

- Preserve nomes de pacotes e diretórios, inclusive `niveis`, `mapas`, `imagens` e `mensages`.
- Cenas extendem `Scene`; comportamento reutilizável fica em `br.com.game.script`.
- O menu deriva automaticamente de `application.json`. `menu: false` oculta a entrada sem mudar o índice de destino da cena. Não mantenha uma segunda lista fixa.
- A engine cria uma nova instância por visita. Inicialize estado da cena no setup e não dependa de campos preservados entre visitas.
- Use `add/remove/clearScene` e `addComponente`; as listas públicas são de leitura.
- Use `Time.getDeltaTime()` no update variável e o argumento de `fixedUpdate(float)` para física. `AndarEmTile.mover` recebe velocidades em pixels/segundo.
- Mouse exige dono: `Mouse.infInstace().addListener(this, callback)`. O descarte da cena libera a inscrição; não adicione limpeza global de input.
- Spritesheets devem ter grade uniforme. `Sprite(name, cx, cy)` espera colunas/linhas compatíveis com a imagem.
- Todo recurso usa caminho relativo exato com extensão. Não use nome base, caminho absoluto ou raiz dependente da máquina.
- O caminho `image source` do TMX é relativo ao mapa. Flags de flip/rotação e funcionalidades não implementadas devem falhar explicitamente.
- Evite dependências diretas de janela/dispositivo Vulkan em gameplay. O harness em `src/test` pode acessar o backend para verificações de integração.

## Onde alterar

| Comportamento | Arquivo/pasta |
| --- | --- |
| Metadados e ordem de cenas | [application.json](src/main/resources/application.json) |
| Seleção e navegação | [Menu.java](src/main/java/br/com/game/niveis/examples/Menu.java) |
| Movimento em tiles | [AndarEmTile.java](src/main/java/br/com/game/script/AndarEmTile.java) |
| Exemplos de gameplay | `src/main/java/br/com/game/niveis/examples/` |
| Smoke GPU/JAR | [GameMigrationSmokeApp.java](src/test/java/br/com/game/GameMigrationSmokeApp.java) |

Ao alterar o contrato da engine, valide ambos os repositórios. Atualize o PRD quando houver nova evidência de aceite e o blueprint quando mudar o fluxo real.
