# Revisão do consumidor EngineFX 3.0 — blocos 1B/1C

Data: 13/09/2026. Revisão contra os planos geral/detalhado da raiz compartilhada. O relatório dos problemas e correções da engine está na [revisão conjunta](../../../enginefx/docs/reviews/2026-09-13-lua-blocks-review.md).

## Resultado

O DinoFX consome `enginefx:enginefx:3.0.0`. A demo `LuaHiddenDemo`, oculta no menu, carrega `res/scripts/hidden_demo.lua` do JAR, recebe deltas em segundos e confirma setup/update/dispose. Nashorn, verificação direta de JS e merge de serviços foram removidos. As seis cenas visíveis mantêm seus índices e comportamento de menu.

A revisão corrigiu o contador do smoke parcial: o número esperado de descartes Lua acompanha somente as visitas realizadas. Antes, selecionar apenas o menu exigia dois descartes de uma cena nunca visitada. A mensagem de sucesso só menciona lifecycle Lua quando houve visita Lua. A demo protege setup repetido para não substituir um runtime ainda aberto.

## Evidência

Windows x64, OpenJDK 25.0.2, Maven Wrapper 3.9.16, LWJGL 3.4.3. GPU AMD Radeon RX 9060 XT, API Vulkan 1.4.349, driver 8389003. Maven offline e dependências já instaladas.

- EngineFX: 61 testes; DinoFX: 5 testes. Zero falhas, erros ou ignorados.
- `build.ps1 -Smoke -PresentMode fifo`: sete cenas, duas visitas de 120 frames por cena, **1.680 frames**. Setup/update/descarte Lua, cache de imagem, resize e crescimento do buffer GPU verificados.
- Smoke parcial com `enginefx.smoke.firstScene=0` e `enginefx.smoke.lastScene=0`: menu em duas visitas, **240 frames**, sucesso sem exigir descartes Lua.
- JARs executados a partir de diretórios vazios, com FFM e `--sun-misc-unsafe-memory-access=deny`.
- Inspeção do JAR: engine 3.0.0, módulo da demo e runtime Lua presentes; sem `.js`, Nashorn, `SceneJs`, `ScriptJsComponent` ou descritores de serviços.

Logs locais em `../.review/blocos4-5/` a partir da raiz do checkout. O harness e os fontes versionados permitem repetir a validação; `target` e logs locais não entram no commit.

## Limites

A etapa 1C está validada no JVM/JAR. O bootstrap ainda usa o adaptador reflexivo transitório; sua remoção e o Native Image integrado pertencem à 1D. Não houve aceite visual por pixels, gameplay manual, MAILBOX, validation layers, sessão prolongada, instalação sem cache ou teste multiplataforma. A prova nativa isolada anterior não comprova esse runtime integrado.
