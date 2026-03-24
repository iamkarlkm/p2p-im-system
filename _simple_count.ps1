# 统计代码文件
Write-Host "=== 统计中 ==="
$files = Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Include '*.java', '*.js', '*.ts', '*.tsx', '*.jsx', '*.dart', '*.rs', '*.html', '*.css' -Recurse -File
$totalLines = 0

foreach ($file in $files) {
    $lineCount = (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    $totalLines += $lineCount
}

Write-Host "总文件数: $($files.Count)"
Write-Host "总代码行数: $totalLines"

# 按类型统计
$java = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.java' -Recurse -File).Count
$js = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.js' -Recurse -File).Count
$ts = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.ts' -Recurse -File).Count
$dart = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.dart' -Recurse -File).Count
$html = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.html' -Recurse -File).Count
$css = (Get-ChildItem -Path 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects' -Filter '*.css' -Recurse -File).Count

Write-Host ""
Write-Host "文件类型分布:"
Write-Host "  Java: $java files"
Write-Host "  JavaScript: $js files"
Write-Host "  TypeScript: $ts files"
Write-Host "  Dart: $dart files"
Write-Host "  HTML: $html files"
Write-Host "  CSS: $css files"