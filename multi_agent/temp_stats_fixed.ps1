# 统计代码文件数量和行数
$projectDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$fileTypes = @("*.java", "*.js", "*.ts", "*.tsx", "*.jsx", "*.dart", "*.rs", "*.html", "*.css")
$stats = @{}

foreach ($type in $fileTypes) {
    $files = Get-ChildItem -Path $projectDir -Recurse -Include $type -File
    $totalLines = 0
    foreach ($file in $files) {
        try {
            $lines = (Get-Content $file.FullName -ErrorAction Stop).Count
            $totalLines += $lines
        } catch {
            # 忽略无法读取的文件
        }
    }
    $stats[$type] = @{
        Count = $files.Count
        Lines = $totalLines
    }
}

# 按文件类型分组统计
$javaCount = if ($stats["*.java"]) { $stats["*.java"].Count } else { 0 }
$jsCount = if ($stats["*.js"]) { $stats["*.js"].Count } else { 0 }
$tsCount = if ($stats["*.ts"]) { $stats["*.ts"].Count } else { 0 }
$tsxCount = if ($stats["*.tsx"]) { $stats["*.tsx"].Count } else { 0 }
$jsxCount = if ($stats["*.jsx"]) { $stats["*.jsx"].Count } else { 0 }
$dartCount = if ($stats["*.dart"]) { $stats["*.dart"].Count } else { 0 }
$rustCount = if ($stats["*.rs"]) { $stats["*.rs"].Count } else { 0 }
$htmlCount = if ($stats["*.html"]) { $stats["*.html"].Count } else { 0 }
$cssCount = if ($stats["*.css"]) { $stats["*.css"].Count } else { 0 }

# 计算总数
$totalFiles = 0
$totalLines = 0
foreach ($key in $stats.Keys) {
    $totalFiles += $stats[$key].Count
    $totalLines += $stats[$key].Lines
}

# 准备报告
Write-Host "# 综合监控报告 - $(Get-Date -Format 'yyyy-MM-dd HH:mm')"
Write-Host ""
Write-Host "## 代码量统计（统一口径）"
Write-Host "- 总文件数：$totalFiles"
Write-Host "- 总代码行数：$totalLines"
Write-Host "- 文件类型分布："
Write-Host "  - Java: $javaCount 个文件"
Write-Host "  - JavaScript: $(($jsCount + $jsxCount)) 个文件"
Write-Host "  - TypeScript: $(($tsCount + $tsxCount)) 个文件"
Write-Host "  - Dart: $dartCount 个文件"
Write-Host "  - Rust: $rustCount 个文件"
Write-Host "  - HTML/CSS: $(($htmlCount + $cssCount)) 个文件"