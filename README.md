# dinofx

Jogo de exemplos em Java que usa **EngineFX 3.0.0** com renderização Vulkan e Lua 5.4 restrita. O repositório contém cenas, comportamento do jogo e assets; o backend gráfico e o ciclo de execução ficam na engine.

## Documentação

| Documento | Finalidade |
| --- | --- |
| [AGENTS.md](AGENTS.md) | Como alterar e validar este repositório |
| [BluePrint.md](BluePrint.md) | Estrutura e fluxo implementados |
| [PRD.md](PRD.md) | Andamento e próximos passos |
| [REVIEW.md](REVIEW.md) | Correções e evidências da revisão da migração |
| [Review EngineFX 3.0](docs/reviews/2026-09-13-lua-blocks-review.md) | Migração Lua, correções e validação atual no JDK 25 |

## Executar

Requisitos: Windows x64, JDK 25+, GPU/driver Vulkan e checkout de `enginefx` ao lado de `dinofx`. Configure `JAVA_HOME` ou o `PATH` com seu JDK. O primeiro build precisa de internet para obter Maven/dependências.

```text
pasta/
├── enginefx/
└── dinofx/
```

Na raiz de `dinofx`:

```powershell
.\build.ps1
java --enable-native-access=ALL-UNNAMED -jar .\target\dino.jar
```

O script instala a engine antes de empacotar o jogo e funciona independentemente do diretório de execução. O artefato executável é `target/dino.jar`. Não exige Maven global nem caminho fixo para o JDK.

Se a engine já estiver instalada no Maven local:

```powershell
.\mvnw.cmd clean package
```

## Cenas e controles

| Cena | Controles principais |
| --- | --- |
| Menu | Cima/baixo selecionam; Enter abre a cena |
| Spaceship | Setas movem; Espaço dispara |
| QuedaLivre | Esquerda/direita movem; Espaço aplica salto |
| Level001 | Setas montam a sequência de movimentos; Espaço inicia |
| Level002 | Setas escolhem o tile; WASD move a câmera; Espaço reposiciona a câmera |
| TiledMapGame | WASD move a câmera; Espaço reposiciona a câmera |

Esc retorna ao menu nas cenas de exemplo. Os títulos e a inclusão no menu são definidos em [application.json](src/main/resources/application.json).

## Testes e smoke

```powershell
.\mvnw.cmd test
.\build.ps1 -Smoke -PresentMode fifo
```

JUnit cobre movimentos de tile, mapeamento do menu e navegação por acionamento sem repetição ao manter a tecla. O smoke roda as seis cenas visíveis e a cena Lua oculta duas vezes, com 120 frames por visita, dois resizes por visita, assets do JAR, cache de imagens, lifecycle Lua e crescimento de buffer Vulkan. Ele cria uma pasta vazia para não depender dos assets do checkout.

Para rodar somente parte do harness depois do build, use índices de configuração inclusivos:

```powershell
java --enable-native-access=ALL-UNNAMED '-Denginefx.smoke.hidden=true' '-Denginefx.smoke.firstScene=1' '-Denginefx.smoke.lastScene=2' -cp 'target/test-classes;target/dino.jar' br.com.game.GameMigrationSmokeApp
```

`-PresentMode` aceita `auto`, `fifo` ou `mailbox`. MAILBOX explícito requer suporte da superfície. A camada Khronos é ativada por `-Denginefx.vulkan.validation=true` e precisa estar instalada.

## Criar uma cena

1. Adicione a classe em `src/main/java/br/com/game/niveis`, derivando de `Scene`.
2. Chame `super.setup()` para criar a câmera padrão e adicione os objetos/componentes.
3. Registre uma factory com id estável em `Main.scenes()` e use o mesmo id em `application.json`, com `title` e `menu`.
4. Use caminhos completos relativos a `res/`, como `imagens/player.png`, `fonts/font.ttf` e `mapas/level.tmx`.
5. Coloque comportamento reutilizável em `br.com.game.script`; use `fixedUpdate(float)` para movimento físico em pixels/segundo.
6. Rode testes e smoke com recursos empacotados. O exemplo Lua empacotado está em `scripts/hidden_demo.lua`; ele não aparece no menu e existe para o smoke validar carregamento, callback e descarte.

A migração está funcional em FIFO no ambiente validado, mas o aceite visual e de driver permanece pendente; veja [PRD.md](PRD.md). O bootstrap não resolve nomes de classes: toda cena do JSON deve existir em `Main.scenes()`.


O [review do bootstrap 1D](docs/reviews/2026-09-13-native-bootstrap-review.md) registra a validação atual com ids/factories explícitos e verificação das cenas carregadas. A distribuição do DinoFX neste checkpoint é JVM/JAR.
