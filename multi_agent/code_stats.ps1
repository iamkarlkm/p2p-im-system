$projectsPath = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
$stats = @{}
$totalFiles = 0
$totalLines = 0

# 定义文件类型映射
$typeMapping = @{
    '*.java' = 'Java'
    '*.js' = 'JavaScript'
    '*.ts' = 'JavaScript'
    '*.tsx' = 'JavaScript'
    '*.jsx' = 'JavaScript'
    '*.dart' = 'Dart'
    '*.rs' = 'Rust'
    '*.html' = 'HTML/CSS'
    '*.css' = 'HTML/CSS'
}

Write-Host '=== 代码量统计 (统一口径) ==='

foreach ($pattern in $typeMapping.Keys) {
    $typeName = $typeMapping[$pattern]
    $files = Get-ChildItem -Path $projectsPath -Recurse -Include $pattern -File -ErrorAction SilentlyContinue
    $fileCount = $files.Count
    $lineCount = 0
    
    if ($fileCount -gt 0) {
        foreach ($file in $files) {
            try {
                $lineCount += (Get-Content $file.FullName | Measure-Object -Line).Lines
            } catch {
                # 忽略无法读取的文件
            }
        }
    }
    
    if (-not $stats.ContainsKey($typeName)) {
        $stats[$typeName] = @{Files = 0; Lines = 0}
    }
    
    $stats[$typeName].Files += $fileCount
    $stats[$typeName].Lines += $lineCount
}

# 合并 HTML/CSS
if ($stats.ContainsKey('HTML/CSS')) {
    $htmlCssFiles = $stats['HTML/CSS'].Files
    $htmlCssLines = $stats['HTML/CSS'].Lines
} else {
    $htmlCssFiles = 0
    $htmlCssLines = 0
}

# 计算总计
foreach ($typeName in $stats.Keys) {
    $totalFiles += $stats[$typeName].Files
    $totalLines += $stats[$typeName].Lines
}

# 输出结果
Write-Host "总计:"
Write-Host "- 总文件数: $totalFiles"
Write-Host "- 总代码行数: $totalLines"

Write-Host "`n文件类型分布:"
Write-Host "- Java: $($stats['Java'].Files)个文件 ($($stats['Java'].Lines)行)"
Write-Host "- JavaScript: $($stats['JavaScript'].Files)个文件 ($($stats['JavaScript'].Lines)行)"
Write-Host "- Dart: $($stats['Dart'].Files)个文件 ($($stats['Dart'].Lines)行)"
Write-Host "- Rust: $($stats['Rust'].Files)个文件 ($($stats['Rust'].Lines)行)"
Write-Host "- HTML/CSS: $htmlCssFiles个文件 ($htmlCssLines行)"

# 输出用于捕获的变量
Write-Host "`n===STATS_START==="
Write-Host "TOTAL_FILES:$totalFiles"
Write-Host "TOTAL_LINES:$totalLines"
Write-Host "JAVA_FILES:$($stats['Java'].Files)"
Write-Host "JAVA_LINES:$($stats['Java'].Lines)"
Write-Host "JAVASCRIPT_FILES:$($stats['JavaScript'].Files)"
Write-Host "JAVASCRIPT_LINES:$($stats['JavaScript'].Lines)"
Write-Host "DART_FILES:$($stats['Dart'].Files)"
Write-Host "DART_LINES:$($stats['Dart'].Lines)"
Write-Host "RUST_FILES:$($stats['Rust'].Files)"
Write-Host "RUST_LINES:$($stats['Rust'].Lines)"
Write-Host "HTMLCSS_FILES:$htmlCssFiles"
Write-Host "HTMLCSS_LINES:$htmlCssLines"
Write-Host "===STATS_END==="