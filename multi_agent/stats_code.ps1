# 统计代码文件数量和行数
$codeDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$logFile = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_stats.txt"

# 文件类型扩展名
$extensions = @("*.java", "*.js", "*.ts", "*.tsx", "*.jsx", "*.dart", "*.rs", "*.html", "*.css")

Write-Host "正在扫描目录: $codeDir"
Write-Host "要统计的文件类型: $($extensions -join ', ')"

# 初始化计数器
$totalFiles = 0
$totalLines = 0
$typeCounts = @{
    "Java" = @{ count = 0; lines = 0 }
    "JavaScript" = @{ count = 0; lines = 0 }
    "Dart" = @{ count = 0; lines = 0 }
    "Rust" = @{ count = 0; lines = 0 }
    "HTML/CSS" = @{ count = 0; lines = 0 }
}

# 递归查找所有文件
foreach ($ext in $extensions) {
    $files = Get-ChildItem -Path $codeDir -Recurse -Include $ext -ErrorAction SilentlyContinue
    
    foreach ($file in $files) {
        $totalFiles++
        
        # 统计行数
        try {
            $lineCount = (Get-Content $file.FullName | Measure-Object -Line).Lines
            $totalLines += $lineCount
        } catch {
            $lineCount = 0
        }
        
        # 按类型分类
        switch ($file.Extension) {
            ".java" { 
                $typeCounts["Java"].count++; $typeCounts["Java"].lines += $lineCount
            }
            ".js" { 
                $typeCounts["JavaScript"].count++; $typeCounts["JavaScript"].lines += $lineCount
            }
            ".ts" { 
                $typeCounts["JavaScript"].count++; $typeCounts["JavaScript"].lines += $lineCount
            }
            ".tsx" { 
                $typeCounts["JavaScript"].count++; $typeCounts["JavaScript"].lines += $lineCount
            }
            ".jsx" { 
                $typeCounts["JavaScript"].count++; $typeCounts["JavaScript"].lines += $lineCount
            }
            ".dart" { 
                $typeCounts["Dart"].count++; $typeCounts["Dart"].lines += $lineCount
            }
            ".rs" { 
                $typeCounts["Rust"].count++; $typeCounts["Rust"].lines += $lineCount
            }
            ".html" { 
                $typeCounts["HTML/CSS"].count++; $typeCounts["HTML/CSS"].lines += $lineCount
            }
            ".css" { 
                $typeCounts["HTML/CSS"].count++; $typeCounts["HTML/CSS"].lines += $lineCount
            }
        }
    }
}

# 输出结果
Write-Host "`n代码统计结果:"
Write-Host "总文件数: $totalFiles"
Write-Host "总代码行数: $totalLines"
Write-Host "`n文件类型分布:"
Write-Host "Java: $($typeCounts['Java'].count) 个文件, $($typeCounts['Java'].lines) 行"
Write-Host "JavaScript: $($typeCounts['JavaScript'].count) 个文件, $($typeCounts['JavaScript'].lines) 行"
Write-Host "Dart: $($typeCounts['Dart'].count) 个文件, $($typeCounts['Dart'].lines) 行"
Write-Host "Rust: $($typeCounts['Rust'].count) 个文件, $($typeCounts['Rust'].lines) 行"
Write-Host "HTML/CSS: $($typeCounts['HTML/CSS'].count) 个文件, $($typeCounts['HTML/CSS'].lines) 行"

# 保存到临时文件
$statsContent = @"
总文件数: $totalFiles
总代码行数: $totalLines
---
Java: $($typeCounts['Java'].count) 个文件, $($typeCounts['Java'].lines) 行
JavaScript: $($typeCounts['JavaScript'].count) 个文件, $($typeCounts['JavaScript'].lines) 行
Dart: $($typeCounts['Dart'].count) 个文件, $($typeCounts['Dart'].lines) 行
Rust: $($typeCounts['Rust'].count) 个文件, $($typeCounts['Rust'].lines) 行
HTML/CSS: $($typeCounts['HTML/CSS'].count) 个文件, $($typeCounts['HTML/CSS'].lines) 行
"@

Set-Content -Path "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\temp_code_stats.txt" -Value $statsContent

# 返回统计结果
@{
    totalFiles = $totalFiles
    totalLines = $totalLines
    typeCounts = $typeCounts
}