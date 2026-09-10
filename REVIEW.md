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
