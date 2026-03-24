$basePath = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'

$projects = @('im-backend', 'im-desktop', 'im-mobile')
$results = @()

foreach ($proj in $projects) {
    $projPath = Join-Path $basePath $proj
    if (Test-Path $projPath) {
        $files = Get-ChildItem -Path $projPath -Recurse -File -Include *.java,*.js,*.dart,*.rs,*.toml,*.yml,*.yaml,*.html,*.css
        $totalLines = 0
        foreach ($f in $files) {
            $lines = (Get-Content $f.FullName).Count
            $totalLines += $lines
        }
        $results += [PSCustomObject]@{
            Project = $proj
            Files = $files.Count
            Lines = $totalLines
        }
    }
}

$results | Format-Table -AutoSize

Write-Output "---"
Write-Output "Total files: $(($results | Measure-Object -Property Files -Sum).Sum)"
Write-Output "Total lines: $(($results | Measure-Object -Property Lines -Sum).Sum)"
