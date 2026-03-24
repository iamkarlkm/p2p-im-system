$base = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$files = Get-ChildItem -Path $base -Recurse -Include *.java,*.js,*.ts,*.tsx,*.jsx,*.dart,*.rs,*.html,*.css

$java = $files | Where-Object { $_.Extension -eq '.java' }
$jsFiles = $files | Where-Object { $_.Extension -in '.js','.ts','.tsx','.jsx' }
$dart = $files | Where-Object { $_.Extension -eq '.dart' }
$rs = $files | Where-Object { $_.Extension -eq '.rs' }
$web = $files | Where-Object { $_.Extension -in '.html','.css' }

Write-Host "Java files: $($java.Count)"
Write-Host "JS/TS files: $($jsFiles.Count)"
Write-Host "Dart files: $($dart.Count)"
Write-Host "Rust files: $($rs.Count)"
Write-Host "HTML/CSS files: $($web.Count)"
Write-Host "Total files: $($files.Count)"

$totalLines = 0
foreach ($f in $files) {
    $l = (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    $totalLines += $l
}
Write-Host "Total lines: $totalLines"
