$base = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
$exts = @('*.java','*.js','*.ts','*.tsx','*.jsx','*.dart','*.rs','*.html','*.css')
$extNames = @{'.java'='Java';'.js'='JavaScript';'.ts'='TypeScript';'.tsx'='TypeScript';'.jsx'='JavaScript';'.dart'='Dart';'.rs'='Rust';'.html'='HTML';'.css'='CSS'}
$results = @()
$grouped = @{}
foreach ($e in $exts) {
    $files = Get-ChildItem $base -Recurse -File -Filter $e -ErrorAction SilentlyContinue
    foreach ($f in $files) {
        $lines = (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
        $ext = $e
        $cat = $extNames[$e]
        if (-not $grouped[$cat]) { $grouped[$cat] = @{Files=0;Lines=0} }
        $grouped[$cat].Files++
        $grouped[$cat].Lines += $lines
        $results += [PSCustomObject]@{Ext=$e;Lines=$lines;Path=$f.FullName}
    }
}
$totalFiles = ($results | Measure-Object).Count
$totalLines = ($results | Measure-Object -Sum -Property Lines).Sum
Write-Host "TOTAL_FILES:$totalFiles"
Write-Host "TOTAL_LINES:$totalLines"
foreach ($k in $grouped.Keys) {
    Write-Host "EXT:$k`:$($grouped[$k].Files)`:$($grouped[$k].Lines)"
}
