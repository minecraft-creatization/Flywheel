# build.ps1 - the full Gradle build (jar / remapJar), which compile.ps1 deliberately does not run.
#
# compile.ps1 runs `compileJava compileClientJava` only, so a tree can have .class files and an empty
# build/libs: a failing javac stops Gradle before `jar`, and once javac passes nothing else asks for a
# jar. Create-26.3 depends on this tree's jar, so building it is part of the split.
#
# Usage: .\build.ps1 [logName]
param([string]$LogName = "flywheel")
$ErrorActionPreference = 'Continue'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $root 'build'
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }
$log = Join-Path $logDir "build-$LogName.log"
$previousJavaToolOptions = $env:JAVA_TOOL_OPTIONS
$env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=$env:TEMP\gradle-unix-fallback-$([guid]::NewGuid())"
try {
    & (Join-Path $root 'gradlew.bat') --no-daemon -I ..\gradle-mirror.init.gradle build *> $log
    $exit = $LASTEXITCODE
} finally {
    $env:JAVA_TOOL_OPTIONS = $previousJavaToolOptions
}
Write-Output "exit=$exit  log=$log"
Write-Output '--- last 15 lines ---'
Get-Content $log | Select-Object -Last 15
Write-Output '--- build/libs ---'
Get-ChildItem (Join-Path $logDir 'libs') -ErrorAction SilentlyContinue | ForEach-Object { "$($_.Name)  $($_.Length)" }