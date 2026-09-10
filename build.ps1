param(
    [switch]$SkipTests,
    [switch]$Smoke,
    [ValidateSet('auto', 'fifo', 'mailbox')][string]$PresentMode = 'auto'
)

$ErrorActionPreference = 'Stop'
$engineBuild = Join-Path $PSScriptRoot '..\enginefx\build.ps1'
if (-not (Test-Path -LiteralPath $engineBuild)) { throw "enginefx sibling checkout is required: $engineBuild" }
& $engineBuild -SkipTests:$SkipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$buildArgs = @('-B', '-f', (Join-Path $PSScriptRoot 'pom.xml'), 'package')
if ($SkipTests) { $buildArgs += '-DskipTests' }
& "$PSScriptRoot\mvnw.cmd" @buildArgs
if ($LASTEXITCODE -ne 0 -or -not $Smoke) { exit $LASTEXITCODE }

# Always exercise the packaged resources without development assets in the working directory.
$smokeDirectory = Join-Path $PSScriptRoot ('target/smoke-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $smokeDirectory | Out-Null
Push-Location -LiteralPath $smokeDirectory
try {
    & java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=deny '-Denginefx.smoke.hidden=true' "-Denginefx.vulkan.presentMode=$PresentMode" -cp "$PSScriptRoot\target\test-classes;$PSScriptRoot\target\dino.jar" br.com.game.GameMigrationSmokeApp
    $smokeExit = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $smokeExit
