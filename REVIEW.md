# Review — dinofx / EngineFX 2.0.0

Data: 10/09/2026. Revisão de todas as alterações pendentes em relação a `435f8bd`, incluindo arquivos preparados, não preparados e novos.

## Resultado e correções

| Prioridade | Falha | Correção |
| --- | --- | --- |
| P1 | Menu filtrava cenas ocultas, mas Enter usava o índice visual como índice de configuração | Lista explícita de destinos originais; teste com entrada intermediária oculta |
| P2 | Metadados `menu` ausentes podiam ocultar cenas devido à desserialização sem construtor | Ausência/null preserva visibilidade; teste com configuração antiga |
| P1 | Level001 ainda passava 1 pixel por segundo depois da migração de AndarEmTile | Velocidade ajustada para 60 pixels/s, preservando a referência de 60 FPS |
| P2 | Movimento guardava referência mutável ao alvo e podia não terminar com eixo sem velocidade | Cópia defensiva, validação e chegada limitada por eixo; testes a 30/60/144 Hz |
| P2 | Navegação do menu podia selecionar índice inválido e resetava o timer mesmo sem avançar | Limites calculados antes de Enter e repetição temporizada apenas ao mudar |
| P1 | build.ps1 podia instalar o projeto errado conforme CWD e dependia de caminho fixo de JDK | POMs explícitos, JDK por ambiente e build integrado validado da pasta pai |
| P1 | Smoke havia removido a inicialização FFM e podia reportar sucesso em intervalo vazio de cenas | FFM antes de LWJGL, intervalo validado, contagem real de cenas e cleanup em finally |
| P2 | Empacotamento incluía descritores de módulo sobrepostos | Exclusão dos module-info no shaded JAR, mantendo Multi-Release e serviços Nashorn |

As falhas da engine que afetavam o consumidor — fonte no JAR, cache, memória STB, lifecycle, colisão, texto e TMX — foram corrigidas no repositório da engine. A compatibilidade agora é verificada conjuntamente.

## Evidências

- JUnit do jogo: **4 testes aprovados**, zero falhas/erros.
- JUnit da engine: **27 testes aprovados**, zero falhas/erros.
- Empacotamento com engine 2.0.0 e JAR `target/dino.jar`.
- Smoke FIFO a partir de pasta vazia: Menu, Spaceship, QuedaLivre, Level001, Level002 e TiledMapGame, duas vezes; 120 frames por visita, total de **1.440 frames**.
- Cada visita inclui resize em dois pontos. O harness também verifica Nashorn, compartilhamento de imagens do JAR e crescimento de um buffer Vulkan.
- OpenJDK 27, Windows x64, LWJGL 3.4.3, AMD Radeon RX 9060 XT, Vulkan 1.4.349, driver reportado 8389003.
- Execução com FFM e `--sun-misc-unsafe-memory-access=deny`.
- MAILBOX solicitado explicitamente: indisponível para a superfície local.
- Khronos/sync validation: camada ausente, erro -6. O teste de driver permanece pendente.

O [handoff anterior](docs/reviews/HANDOFF.md) está preservado como histórico. Nesta revisão, antes das correções, o smoke externo falhou ao carregar `font.ttf`, apesar dos seis testes antigos da engine passarem.

## Como repetir

Na raiz do jogo, com o checkout irmão da engine:

```powershell
.\build.ps1 -Smoke -PresentMode fifo
```

Para tentar outro modo em ambiente compatível: `-PresentMode mailbox`. A opção explícita falha quando não suportada, sem fallback silencioso.

## Documentação e pendências

O conjunto principal foi organizado em [README.md](README.md), [AGENTS.md](AGENTS.md), [BluePrint.md](BluePrint.md) e [PRD.md](PRD.md). O blueprint antigo foi substituído, com o histórico preservado no Git.

O smoke não verifica cada pixel nem simula o jogo completo. Faltam aceite visual com interação, apresentação em MAILBOX, validation layers, testes longos e unificação da física de QuedaLivre. Esses limites não impedem registrar as correções implementadas, mas impedem declarar concluída toda a migração Vulkan.

## Atualização — EngineFX 3.0 / Etapa 1C (13/09/2026)

O consumidor foi migrado para `enginefx:enginefx:3.0.0`, que remove Nashorn e todas as APIs JavaScript. Foi acrescentada `LuaHiddenDemo`, uma cena `menu: false` que carrega `res/scripts/hidden_demo.lua` por `ClasspathResourceResolver`. Ela usa somente `engine.state`, registra `setup`, recebe `update` em segundos e exige `dispose` antes de fechar o runtime.

O smoke do JAR passa pelas sete cenas duas vezes. Em execução focada, a cena Lua passou as duas visitas a partir da pasta vazia criada pelo build, confirmando carregamento, callback e descarte do módulo. A suíte unitária do DinoFX terminou com 5 testes verdes; a EngineFX 3.0 terminou com 44. A validação visual, MAILBOX e as limitações já registradas continuam inalteradas.


## Revisão dos blocos 1B/1C — 13/09/2026

A [revisão consolidada da 3.0](docs/reviews/2026-09-13-lua-blocks-review.md) registra as correções posteriores: runtime Lua revisado, 61 testes da engine, 5 do consumidor, smoke completo de 1.680 frames e smoke parcial de 240 frames. Foram corrigidos o contador de descartes em seleção parcial e a proteção de setup da demo. Esta evidência substitui os totais preliminares da implementação acima.

## Atualização do bootstrap explícito — 13/09/2026

`application.json` agora declara ids estáveis `dinofx:*`; `Main.scenes()` é o único local que associa esses ids às factories Java. O smoke também entrega o mesmo registry ao controlador antes de inicializar GLFW, preservando o perfil de runtime centralizado da engine. `mvnw.cmd -q test` terminou com 5 testes verdes; `build.ps1 -Smoke -PresentMode fifo` percorreu sete cenas duas vezes, com 1.680 frames, resize, assets do JAR, cache e lifecycle Lua. A compilação/execução Native Image permanece evidência da engine e aguarda GraalVM/MSVC no job Windows.


## Revisão do bloco 1D — 13/09/2026

O [review atual do consumidor](docs/reviews/2026-09-13-native-bootstrap-review.md) registra as correções da verificação de cenas e a validação final: 5 testes e 1.680 frames FIFO contra a engine com 69 testes. O gate Native Image integrado da engine passou localmente com GraalVM 25.3.4.1; a afirmação preliminar acima de que aguardava toolchain foi superada. Não houve execução remota do workflow nem build nativo do DinoFX.


## Revisão integrada de gameplay — 14/09/2026

Consumo da EngineFX 3.1.0 confirmado com 5 testes e `build.ps1 -Smoke -PresentMode fifo`: sete cenas duas vezes, 1.680 frames, assets, Lua, resize e crescimento de buffer. A engine passou 74 testes, preserva escala explícita de sprites e permite resize/maximize com proporção mantida e mouse remapeado. A revisão detalhada está no repositório irmão `enginefx/docs/reviews/2026-09-14-gameplay.md`. Gameplay manual de todos os exemplos, drivers adicionais e publicação permanecem pendentes.
