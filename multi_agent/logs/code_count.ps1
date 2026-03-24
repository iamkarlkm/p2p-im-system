$baseDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$extensions = @(".py", ".js", ".ts", ".md", ".json", ".yaml", ".yml", ".txt", ".java", ".dart", ".rs")

$projects = @("im-backend", "im-desktop", "im-mobile")
$results = @{}

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path $projectDir) {
        $files = Get-ChildItem -Path $projectDir -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $extensions -contains $_.Extension.ToLower() }
        $totalLines = 0
        foreach ($file in $files) {
            try {
                $lines = (Get-Content $file.FullName -Encoding UTF8 -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
                $totalLines += $lines
            } catch {}
        }
        $results[$project] = @{
            "files" = $files.Count
            "lines" = $totalLines
        }
        Write-Output "DEBUG: $project - Files: $($files.Count), Lines: $totalLines"
    } else {
        Write-Output "DEBUG: $project - Not found"
        $results[$project] = @{"files" = 0; "lines" = 0}
    }
}

$totalFiles = ($results.Values | ForEach-Object { $_.files } | Measure-Object -Sum).Sum
$totalLines = ($results.Values | ForEach-Object { $_.lines } | Measure-Object -Sum).Sum

Write-Output "---"
Write-Output "TOTAL_FILES:$totalFiles"
Write-Output "TOTAL_LINES:$totalLines"
foreach ($project in $projects) {
    if ($results[$project]) {
        Write-Output "$project`:$($results[$project].files)`:$($results[$project].lines)"
    }
}
