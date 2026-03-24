$projects = @('im-backend', 'im-desktop', 'im-mobile')
foreach ($proj in $projects) {
    $path = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\$proj"
    if (Test-Path $path) {
        $files = Get-ChildItem -Path $path -Recurse -Include *.py,*.js,*.ts,*.json,*.md,*.yml,*.yaml,*.toml,*.txt,*.java,*.dart,*.rs -File
        $totalLines = 0
        foreach ($f in $files) {
            $totalLines += (Get-Content $f.FullName | Measure-Object -Line).Lines
        }
        Write-Host "$proj`:$($files.Count):$totalLines"
    }
}
