$files = Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Recurse -Include *.py,*.js,*.ts,*.json,*.md,*.yml,*.yaml,*.toml,*.txt,*.java,*.dart,*.rs -File
$totalLines = 0
foreach ($f in $files) {
    $totalLines += (Get-Content $f.FullName | Measure-Object -Line).Lines
}
Write-Host "TotalFiles:$($files.Count)"
Write-Host "TotalLines:$totalLines"
