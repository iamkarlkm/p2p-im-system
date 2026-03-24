$projectPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

# 定义要统计的文件类型
$filePatterns = @(
    "*.java",
    "*.js", 
    "*.ts",
    "*.tsx",
    "*.jsx",
    "*.dart",
    "*.rs",
    "*.html",
    "*.css"
)

# 统计函数
function Get-CodeStats($path) {
    $totalFiles = 0
    $totalLines = 0
    $typeStats = @{}
    
    foreach ($pattern in $filePatterns) {
        $files = Get-ChildItem -Path $path -Recurse -Include $pattern -File -ErrorAction SilentlyContinue
        $count = $files.Count
        
        if ($count -gt 0) {
            $lines = 0
            foreach ($file in $files) {
                try {
                    $lineCount = (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
                    $lines += $lineCount
                } catch {
                    Write-Host "Error reading $($file.FullName): $_"
                }
            }
            
            $typeName = switch ($pattern) {
                "*.java" { "Java" }
                "*.js" { "JavaScript" }
                "*.ts" { "TypeScript" }
                "*.tsx" { "TypeScript" }
                "*.jsx" { "JavaScript" }
                "*.dart" { "Dart" }
                "*.rs" { "Rust" }
                "*.html" { "HTML" }
                "*.css" { "CSS" }
                default { $pattern }
            }
            
            # 合并 TypeScript 和 JavaScript 统计
            if ($typeName -eq "TypeScript") {
                if (-not $typeStats.ContainsKey("JavaScript/TypeScript")) {
                    $typeStats["JavaScript/TypeScript"] = @{Files=0; Lines=0}
                }
                $typeStats["JavaScript/TypeScript"].Files += $count
                $typeStats["JavaScript/TypeScript"].Lines += $lines
            } elseif ($typeName -eq "JavaScript") {
                if (-not $typeStats.ContainsKey("JavaScript/TypeScript")) {
                    $typeStats["JavaScript/TypeScript"] = @{Files=0; Lines=0}
                }
                $typeStats["JavaScript/TypeScript"].Files += $count
                $typeStats["JavaScript/TypeScript"].Lines += $lines
            } else {
                if (-not $typeStats.ContainsKey($typeName)) {
                    $typeStats[$typeName] = @{Files=0; Lines=0}
                }
                $typeStats[$typeName].Files += $count
                $typeStats[$typeName].Lines += $lines
            }
            
            $totalFiles += $count
            $totalLines += $lines
        }
    }
    
    # 合并 HTML 和 CSS
    if ($typeStats.ContainsKey("HTML") -or $typeStats.ContainsKey("CSS")) {
        $htmlFiles = if ($typeStats.ContainsKey("HTML")) { $typeStats["HTML"].Files } else { 0 }
        $cssFiles = if ($typeStats.ContainsKey("CSS")) { $typeStats["CSS"].Files } else { 0 }
        $htmlLines = if ($typeStats.ContainsKey("HTML")) { $typeStats["HTML"].Lines } else { 0 }
        $cssLines = if ($typeStats.ContainsKey("CSS")) { $typeStats["CSS"].Lines } else { 0 }
        
        $typeStats["HTML/CSS"] = @{
            Files = $htmlFiles + $cssFiles
            Lines = $htmlLines + $cssLines
        }
        
        if ($typeStats.ContainsKey("HTML")) { $typeStats.Remove("HTML") }
        if ($typeStats.ContainsKey("CSS")) { $typeStats.Remove("CSS") }
    }
    
    return @{
        TotalFiles = $totalFiles
        TotalLines = $totalLines
        TypeStats = $typeStats
    }
}

# 执行统计
$stats = Get-CodeStats $projectPath

Write-Host "=== 代码量统计 ==="
Write-Host "总文件数: $($stats.TotalFiles)"
Write-Host "总代码行数: $($stats.TotalLines)"
Write-Host ""
Write-Host "文件类型分布:"
foreach ($type in $stats.TypeStats.Keys | Sort-Object) {
    Write-Host "  - $type`: $($stats.TypeStats[$type].Files)个文件 ($($stats.TypeStats[$type].Lines)行)"
}

# 返回统计结果
$stats