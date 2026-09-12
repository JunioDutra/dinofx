# PRD — dinofx

Atualizado em 12/09/2026. Estado e próximos passos do jogo de exemplos consumidor da EngineFX.

## Objetivo

Demonstrar a engine 2D em cenas pequenas e reproduzíveis, permitindo validar sprites, texto, mapas, input, áudio, scripts e física sem implementação Vulkan no gameplay.

## Andamento

| Frente | Estado |
| --- | --- |
| Consumo da EngineFX 2.1.0 | Implementado |
| JAR executável com assets e provedor Nashorn | Implementado e exercitado fora do checkout |
| Caminhos explícitos de recursos | Migrados nas cenas ativas |
| Menu gerado pelos metadados | Implementado e testado, incluindo cenas ocultas e acionamentos por borda sem bloqueio temporal |
| Execução no JDK 25 | Validada no 25.0.2: 5 testes e smoke integrado FIFO; instalação limpa e CI pendentes |
| Movimento de tiles por segundo | Implementado e testado em 30/60/144 Hz |
| Inscrição de mouse pertencente à cena | Implementado |
| Build integrado e documentos principais | Implementado |
| Smoke das seis cenas em FIFO | Aprovado no ambiente registrado no review |
| Aceite visual, MAILBOX e camada Khronos | Pendente por cobertura/ambiente |
| Física uniforme em todos os exemplos | Pendente; QuedaLivre ainda possui integração própria |

## Próximos passos

### 1. Validar gameplay e apresentação

Executar cada cena com interação real, principalmente tiros/colisões, sequência de comandos, movimento negativo, navegação prolongada, áudio e troca repetida de cenas. Registrar imagens/vídeo e ambiente de GPU/driver.

Repetir os testes de apresentação em superfície com MAILBOX e ambiente com validation layers. O ambiente local não oferece esses dois requisitos.

**Aceite:** nenhuma regressão de comportamento/flicker reproduzível; zero erros Vulkan quando a camada estiver disponível. Smoke oculto sem interação não atende sozinho esse critério.

### 2. Unificar física e ampliar regressões

Migrar QuedaLivre para um modelo de passo fixo com resposta de chão e salto definidos. Exercitar deslocamento/colisão no loop completo a 30/60/144 Hz e testar interações entre vários objetos.

Expandir testes para a fila de comandos de Level001, navegação completa do menu e conversão de coordenadas de mouse/câmera em Level002.

**Aceite:** deslocamentos e resultados consistentes entre taxas, alvos atingidos sem ultrapassagem, nenhuma inscrição residual ao retornar ao menu.

### 3. Consolidar distribuição e exemplo de desenvolvimento

Adicionar CI em JDK 25, validar instalação limpa e criar distribuição com runtime empacotado. Documentar um minijogo construído a partir de configuração, uma cena e alguns assets.

Novos formatos TMX, Unicode completo e outros sistemas operacionais dependem da evolução da engine e devem manter critérios de integração explícitos.

## Dependências e limites

O roadmap Vulkan detalhado pertence à engine. Testes offscreen, lifetime avançado de swapchain, pools e uploads são responsabilidades dela. O jogo fornece cenários de aceite e deve ser testado junto a mudanças de contrato.

O [review EngineFX 2.1](docs/reviews/2026-09-12-enginefx-2.1.md) registra a validação atual; [REVIEW.md](REVIEW.md) preserva a anterior. Não inferir estabilidade de sessão longa, suporte multiplataforma ou ausência de flicker a partir dos testes unitários.
