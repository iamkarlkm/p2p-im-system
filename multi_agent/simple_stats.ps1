# 统计代码文件数量和行数 - 简化版
$codeDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

# 文件类型扩展名
$extensions = @("*.java", "*.js", "*.ts", "*.tsx", "*.jsx", "*.dart", "*.rs", "*.html", "*.css")

Write-Host "正在扫描目录: $codeDir"
Write-Host "要统计的文件类型: $($extensions -join ', ')"

# 初始化计数器
$totalFiles = 0
$totalLines = 0
$javaCount = 0
$jsCount = 0
$dartCount = 0
$rustCount = 0
$htmlCssCount = 0

# 统计每种类型的行数
$javaLines = 0
$jsLines = 0
$dartLines = 0
$rustLines = 0
$htmlCssLines = 0

# 递归查找所有文件
foreach ($ext in $extensions) {
    try {
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
                    $javaCount++
                    $javaLines += $lineCount
                }
                ".js" { 
                    $jsCount++
                    $jsLines += $lineCount
                }
                ".ts" { 
                    $jsCount++
                    $jsLines += $lineCount
                }
                ".tsx" { 
                    $jsCount++
                    $jsLines += $lineCount
                }
                ".jsx" { 
                    $jsCount++
                    $jsLines += $lineCount
                }
                ".dart" { 
                    $dartCount++
                    $dartLines += $lineCount
                }
                ".rs" { 
                    $rustCount++
                    $rustLines += $lineCount
                }
                ".html" { 
                    $htmlCssCount++
                    $htmlCssLines += $lineCount
                }
                ".css" { 
                    $htmlCssCount++
                    $htmlCssLines += $lineCount
                }
            }
        }
    } catch {
        Write-Host "扫描 $ext 时出错: $_"
    }
}

# 输出结果
Write-Host ""
Write-Host "代码统计结果:"
Write-Host "总文件数: $totalFiles"
Write-Host "总代码行数: $totalLines"
Write-Host ""
Write-Host "文件类型分布:"
Write-Host "Java: $javaCount 个文件, $javaLines 行"
Write-Host "JavaScript: $jsCount 个文件, $jsLines 行"
Write-Host "Dart: $dartCount 个文件, $dartLines 行"
Write-Host "Rust: $rustCount 个文件, $rustLines 行"
Write-Host "HTML/CSS: $htmlCssCount 个文件, $htmlCssLines 行"

# 保存到临时文件
$statsContent = @"
总文件数: $totalFiles
总代码行数: $totalLines
---
Java: $javaCount 个文件, $javaLines 行
JavaScript: $jsCount 个文件, $jsLines 行
Dart: $dartCount 个文件, $dartLines 行
Rust: $rustCount 个文件, $rustLines 行
HTML/CSS: $htmlCssCount 个文件, $htmlCssLines 行
"@

Set-Content -Path "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\temp_code_stats.txt" -Value $statsContent

# 返回统计结果
@{
    totalFiles = $totalFiles
    totalLines = $totalLines
    javaCount = $javaCount
    javaLines = $javaLines
    jsCount = $jsCount
    jsLines = $jsLines
    dartCount = $dartCount
    dartLines = $dartLines
    rustCount = $rustCount
    rustLines = $rustLines
    htmlCssCount = $htmlCssCount
    htmlCssLines = $htmlCssLines
}