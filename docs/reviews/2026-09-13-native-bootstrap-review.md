# Review do bootstrap explícito — Etapa 1D

Data: 13/09/2026. Checkpoint anterior: `414f019`. A revisão segue os planos da raiz compartilhada e o [gate integrado da engine](../../../enginefx/docs/reviews/2026-09-13-native-bootstrap.md).

## Entrega e correções

`Main.scenes()` associa os sete ids `dinofx:*` às factories Java. O mesmo registry é fornecido ao launcher e ao smoke. `application.json` contém ids e boot explícito; a engine não interpreta nomes de classe nem `@Bootable`. As anotações antigas do menu e exemplo de mapa foram removidas.

O novo smoke verificava apenas se o id estava registrado. Isso aceitaria uma cena incorreta ou uma troca perdida. A revisão restabeleceu a verificação da classe efetivamente carregada para cada id, sem criar instâncias extras. O lifecycle da demo Lua continua sendo conferido em ambas as visitas.

## Validação

- OpenJDK 25.0.2, Windows x64, EngineFX 3.0.0 revisada: 69 testes da engine e 5 testes do DinoFX, sem falhas, erros ou ignorados.
- `build.ps1 -Smoke -PresentMode fifo`: sete cenas duas vezes, 120 frames por visita, **1.680 frames**, JAR em diretório vazio; resize, cache, crescimento de buffer, cenas corretas e callbacks/descarte Lua verificados.
- GPU AMD Radeon RX 9060 XT, Vulkan 1.4.349, driver 8389003, FFM com Unsafe negado.
- JAR inspecionado: metadados Native Image da engine, configuração com ids e módulo Lua presentes; classe legada `EngineSmokeApp` ausente.
- A engine passou separadamente no EXE integrado com 32 ciclos e seis rejeições nativas. Essa prova não é um executável nativo DinoFX.

Logs locais em `../.review/bloco6/`, a partir da raiz do checkout. O gate remoto GitHub Actions não foi disparado. Gameplay manual, pixels, MAILBOX, validation layers, sessões longas e distribuição nativa do jogo continuam fora deste aceite.
