$baseDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$extensions = @("*.java", "*.js", "*.ts", "*.tsx", "*.jsx", "*.dart", "*.rs", "*.html", "*.css", "*.yml", "*.yaml", "*.json", "*.toml", "*.xml")

$results = @{}
$totalFiles = 0
$totalLines = 0

foreach ($ext in $extensions) {
    $files = Get-ChildItem -Path $baseDir -Filter $ext -Recurse -File -ErrorAction SilentlyContinue
    $count = $files.Count
    $totalFiles += $count
    $results[$ext] = $count
    
    # Count lines
    $lines = 0
    foreach ($f in $files) {
        $lines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    }
    $totalLines += $lines
    $results["${ext}_lines"] = $lines
}

Write-Host "Total files: $totalFiles"
Write-Host "Total lines: $totalLines"
foreach ($key in $results.Keys | Sort-Object) {
    Write-Host "$key : $($results[$key])"
}
