$basePath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$extensions = @("*.java","*.js","*.ts","*.tsx","*.jsx","*.dart","*.rs","*.html","*.css")
$total = 0
$extCounts = @{}
$extLines = @{}

foreach ($ext in $extensions) {
    $files = Get-ChildItem -Path $basePath -Recurse -Include $ext -File -ErrorAction SilentlyContinue
    $count = $files.Count
    $extCounts[$ext] = $count
    $extLines[$ext] = 0
    foreach ($f in $files) {
        $lines = (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
        if ($lines) { $extLines[$ext] += $lines; $total += $lines }
    }
}

Write-Host "TOTAL:$total"
foreach ($ext in $extensions) {
    Write-Host "$($ext):$($extCounts[$ext]):$($extLines[$ext])"
}
