# Review handoff — dinofx

## Escopo alterado

- Atualização para dependência `enginefx 2.0.0`.
- Migração de imagens, fontes, áudio e mapas para caminhos relativos explícitos com extensão.
- Metadados `title` e `menu` nas definições de cena.
- Smoke configurável para selecionar cenas durante diagnóstico.

## Validação executada

```powershell
$env:JAVA_HOME='C:/Users/dev/AppData/Local/mise/installs/java/27.0.0'
$env:PATH="$env:JAVA_HOME/bin;$env:PATH"
.\mvnw.cmd package
java --enable-native-access=ALL-UNNAMED -Denginefx.smoke.hidden=true -cp 'target\test-classes;target\dino.jar' br.com.game.GameMigrationSmokeApp
```

Resultado: exit code 0; seis cenas completadas duas vezes, com resize, recursos empacotados e Nashorn.

## Pontos para revisão cuidadosa

- O JAR sombreado atualmente é `target/dino.jar`; confirmar se esse é o nome/formato desejado para distribuição.
- O JDK 27 emite aviso do backend legado Unsafe do LWJGL. Não houve falha funcional no smoke, mas vale acompanhar a compatibilidade de runtime.
- O aceite visual/manual de FIFO e MAILBOX, com validation layers e vídeo, continua pendente conforme o roadmap.
