param(
    [Parameter(Mandatory = $true)]
    [string]$BaseUrl,

    [Parameter(Mandatory = $false)]
    [string]$ProjectId,

    [Parameter(Mandatory = $false)]
    [string]$NodeId,

    [Parameter(Mandatory = $false)]
    [string]$OutputDir
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    $OutputDir = Join-Path $PSScriptRoot "..\\..\\samples\\regression\\runs\\$timestamp"
}

$resolvedOutputDir = [System.IO.Path]::GetFullPath($OutputDir)
New-Item -ItemType Directory -Force -Path $resolvedOutputDir | Out-Null

function Write-JsonFile {
    param(
        [string]$Path,
        [object]$Data
    )

    $json = $Data | ConvertTo-Json -Depth 100
    [System.IO.File]::WriteAllText($Path, $json, [System.Text.Encoding]::UTF8)
}

function Fetch-Json {
    param([string]$Url)
    Write-Host "GET $Url"
    return Invoke-RestMethod -Uri $Url -Method Get
}

$status = Fetch-Json "$BaseUrl/foundation/status"
Write-JsonFile (Join-Path $resolvedOutputDir "status.json") $status

$smoke = Fetch-Json "$BaseUrl/foundation/status/smoke"
Write-JsonFile (Join-Path $resolvedOutputDir "smoke.json") $smoke

if (-not [string]::IsNullOrWhiteSpace($ProjectId) -and -not [string]::IsNullOrWhiteSpace($NodeId)) {
    $snapshot = Fetch-Json "$BaseUrl/foundation/projects/$ProjectId/nodes/$NodeId/regression-snapshot"
    Write-JsonFile (Join-Path $resolvedOutputDir "node-regression-snapshot.json") $snapshot
}

Write-Host "Regression export written to $resolvedOutputDir"
