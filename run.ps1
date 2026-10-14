# run.ps1 - launch the Flywheel dev client.
#
# Flywheel is a client-only library: this run exists to prove the standalone module still initialises
# (Flywheel.onInitializeClient -> FlwImpl.init -> backend selection) and compiles its shader programs.
# The tell-tale lines are "(flywheel/backend/shaders) Loaded N shader sources" and the backend name.
#
# Usage: .\run.ps1 [logName] [-PlayWorld "World"]
param([string]$LogName = "flywheel", [string]$PlayWorld)
$ErrorActionPreference = 'Continue'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $root 'build'
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }
$log = Join-Path $logDir "run-$LogName.log"
$previousJavaToolOptions = $env:JAVA_TOOL_OPTIONS
$env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=$env:TEMP\gradle-unix-fallback-$([guid]::NewGuid())"
$gradleArguments = @('--no-daemon', '-I', '..\gradle-mirror.init.gradle', 'runClient')
if ($PlayWorld) { $gradleArguments += '-PflywheelPlayWorld'; $gradleArguments += $PlayWorld }
Write-Output "launching: gradlew $($gradleArguments -join ' ')  (log: $log)"
try {
    & (Join-Path $root 'gradlew.bat') @gradleArguments *> $log
    $exit = $LASTEXITCODE
} finally {
    $env:JAVA_TOOL_OPTIONS = $previousJavaToolOptions
}
Write-Output "exit=$exit  log=$log"