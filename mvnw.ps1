[CmdletBinding()]
param(
  [Parameter(ValueFromRemainingArguments = $true)]
  [string[]]$Arguments
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Read distributionUrl from .mvn/wrapper/maven-wrapper.properties
$propFile = Join-Path $scriptDir ".mvn/wrapper/maven-wrapper.properties"
if (-not (Test-Path $propFile)) {
  Write-Error "Cannot find $propFile"
  exit 1
}

$distributionUrl = (Get-Content -Raw $propFile | ConvertFrom-StringData).distributionUrl
if (-not $distributionUrl) {
  Write-Error "cannot read distributionUrl property in $propFile"
  exit 1
}

$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = if ($env:MAVEN_USER_HOME) { $env:MAVEN_USER_HOME } else { "$HOME/.m2" }
$MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"
$mvnCmd = Join-Path $MAVEN_HOME "bin/mvn.cmd"

if (-not (Test-Path $mvnCmd)) {
  Write-Host "Maven wrapper downloading from $distributionUrl ..."
  $tempZip = New-TemporaryFile
  $tempDir = New-Item -ItemType Directory -Path "$($tempZip.FullName)_dir"
  $tempZip.Delete()

  try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $webclient = New-Object System.Net.WebClient
    $zipPath = Join-Path $tempDir.FullName $distributionUrlName
    $webclient.DownloadFile($distributionUrl, $zipPath)

    Expand-Archive -Path $zipPath -DestinationPath $tempDir.FullName

    $extracted = Get-ChildItem -Path $tempDir.FullName -Directory | Where-Object {
      Test-Path (Join-Path $_.FullName "bin/mvn.cmd")
    } | Select-Object -First 1

    if (-not $extracted) {
      Write-Error "Could not find Maven executable in extracted archive"
      exit 1
    }

    if (-not (Test-Path $MAVEN_HOME_PARENT)) {
      New-Item -ItemType Directory -Path $MAVEN_HOME_PARENT -Force | Out-Null
    }

    if (Test-Path $MAVEN_HOME) {
      Remove-Item $MAVEN_HOME -Recurse -Force -ErrorAction SilentlyContinue
    }

    Move-Item -Path $extracted.FullName -Destination $MAVEN_HOME -Force
  } finally {
    if (Test-Path $tempDir.FullName) {
      Remove-Item $tempDir.FullName -Recurse -Force -ErrorAction SilentlyContinue
    }
  }
}

if (-not (Test-Path $mvnCmd)) {
  Write-Error "Maven executable not found at $mvnCmd"
  exit 1
}

& "$mvnCmd" @Arguments
exit $LASTEXITCODE
