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
$javaFiles = $stats["*.java"]
$jsFiles = $stats["*.js"]
$tsFiles = $stats["*.ts"]
$tsxFiles = $stats["*.tsx"]
$jsxFiles = $stats["*.jsx"]
$dartFiles = $stats["*.dart"]
$rustFiles = $stats["*.rs"]
$htmlFiles = $stats["*.html"]
$cssFiles = $stats["*.css"]

# 计算总数
$totalFiles = 0
$totalLines = 0
foreach ($key in $stats.Keys) {
    $totalFiles += $stats[$key].Count
    $totalLines += $stats[$key].Lines
}

# 准备报告
$report = @"
# 综合监控报告 - $(Get-Date -Format 'yyyy-MM-dd HH:mm')

## 代码量统计（统一口径）
- 总文件数：$totalFiles
- 总代码行数：$totalLines
- 文件类型分布：
  - Java: $($javaFiles.Count)个文件
  - JavaScript: $(($jsFiles.Count + $jsxFiles.Count))个文件
  - TypeScript: $(($tsFiles.Count + $tsxFiles.Count))个文件
  - Dart: $($dartFiles.Count)个文件
  - Rust: $($rustFiles.Count)个文件
  - HTML/CSS: $(($htmlFiles.Count + $cssFiles.Count))个文件
"@

$report