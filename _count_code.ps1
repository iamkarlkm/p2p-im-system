$basePath = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
$fileTypes = @('*.java', '*.js', '*.ts', '*.tsx', '*.jsx', '*.dart', '*.rs', '*.html', '*.css')
$totalFiles = 0
$totalLines = 0
$typeCounts = @{}

foreach ($type in $fileTypes) {
    $files = Get-ChildItem -Path $basePath -Filter $type -Recurse -File -ErrorAction SilentlyContinue
    $typeCount = $files.Count
    $typeCounts[$type] = $typeCount
    $totalFiles += $typeCount
    
    foreach ($file in $files) {
        try {
            $lineCount = (Get-Content $file.FullName | Measure-Object -Line).Lines
            $totalLines += $lineCount
        } catch {
            # 跳过无法读取的文件
        }
    }
}

Write-Host '=== 代码统计结果 ==='
Write-Host "总文件数: $totalFiles"
Write-Host "总代码行数: $totalLines"
Write-Host ''
Write-Host '文件类型分布:'
foreach ($key in $typeCounts.Keys) {
    $count = $typeCounts[$key]
    Write-Host "  $key : $count files"
}