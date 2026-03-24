$projectDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$codePatterns = @("*.java", "*.js", "*.ts", "*.tsx", "*.jsx", "*.dart", "*.rs", "*.html", "*.css")

Write-Host "正在统计代码量..." -ForegroundColor Green

$stats = @{
    "Java" = @{count = 0; lines = 0}
    "JavaScript" = @{count = 0; lines = 0}
    "Dart" = @{count = 0; lines = 0}
    "Rust" = @{count = 0; lines = 0}
    "HTML/CSS" = @{count = 0; lines = 0}
}

$totalFiles = 0
$totalLines = 0

foreach ($pattern in $codePatterns) {
    $files = Get-ChildItem -Path $projectDir -Recurse -Include $pattern -ErrorAction SilentlyContinue
    
    foreach ($file in $files) {
        if ($file.Extension -eq ".java") {
            $type = "Java"
        }
        elseif ($file.Extension -in @(".js", ".ts", ".tsx", ".jsx")) {
            $type = "JavaScript"
        }
        elseif ($file.Extension -eq ".dart") {
            $type = "Dart"
        }
        elseif ($file.Extension -eq ".rs") {
            $type = "Rust"
        }
        elseif ($file.Extension -in @(".html", ".css")) {
            $type = "HTML/CSS"
        }
        
        try {
            $lines = (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
            $stats[$type].count++
            $stats[$type].lines += $lines
            $totalFiles++
            $totalLines += $lines
        } catch {
            Write-Host "无法读取文件: $($file.FullName)" -ForegroundColor Yellow
        }
    }
}

Write-Host "`n代码量统计结果：" -ForegroundColor Cyan
Write-Host "总文件数: $totalFiles"
Write-Host "总代码行数: $totalLines"
Write-Host "`n文件类型分布：" -ForegroundColor Cyan

foreach ($type in $stats.Keys) {
    $typeStats = $stats[$type]
    if ($typeStats.count -gt 0) {
        Write-Host "$type: $($typeStats.count) files, $($typeStats.lines) lines"
    }
}