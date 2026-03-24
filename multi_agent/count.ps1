param($path)

$codeTypes = @('*.java','*.js','*.ts','*.tsx','*.jsx','*.dart','*.rs','*.html','*.css')
$results = @{}
$totalFiles = 0
$totalLines = 0

foreach ($type in $codeTypes) {
    $files = Get-ChildItem -Path $path -Recurse -Include $type -File -ErrorAction SilentlyContinue
    if ($files -eq $null) { continue }
    
    $fileCount = $files.Count
    $lineCount = 0
    
    foreach ($file in $files) {
        try {
            $content = Get-Content $file.FullName -ErrorAction SilentlyContinue
            if ($content) {
                $lineCount += $content.Count
            }
        } catch {
            Write-Host "Error reading $($file.FullName)"
        }
    }
    
    $key = switch ($type) {
        '*.java' { 'Java' }
        '*.js' { 'JavaScript' }
        '*.ts' { 'TypeScript' }
        '*.tsx' { 'TypeScript' }
        '*.jsx' { 'JavaScript' }
        '*.dart' { 'Dart' }
        '*.rs' { 'Rust' }
        '*.html' { 'HTML' }
        '*.css' { 'CSS' }
    }
    
    if ($results.ContainsKey($key)) {
        $results[$key].files += $fileCount
        $results[$key].lines += $lineCount
    } else {
        $results[$key] = @{files = $fileCount; lines = $lineCount}
    }
    
    $totalFiles += $fileCount
    $totalLines += $lineCount
}

Write-Host "总文件数: $totalFiles"
Write-Host "总代码行数: $totalLines"
Write-Host "文件类型分布:"
$sortedKeys = $results.Keys | Sort-Object
foreach ($key in $sortedKeys) {
    Write-Host "  $key : $($results[$key].files)个文件, $($results[$key].lines)行"
}