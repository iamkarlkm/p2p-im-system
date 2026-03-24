$projectsPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$exclude = "target|build|node_modules|\.git"

function Get-CodeStats($exts) {
    $files = @()
    foreach ($ext in $exts) {
        $files += Get-ChildItem -Path $projectsPath -Filter $ext -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.FullName -notmatch $exclude }
    }
    $count = $files.Count
    $lines = 0
    foreach ($f in $files) {
        $content = Get-Content $f.FullName -Raw -ErrorAction SilentlyContinue
        if ($content) {
            $lines += ($content -split "`n").Count
        }
    }
    return @{count=$count; lines=$lines; files=$files}
}

$java = Get-CodeStats @("*.java")
$js = Get-CodeStats @("*.js","*.ts","*.tsx","*.jsx")
$dart = Get-CodeStats @("*.dart")
$rust = Get-CodeStats @("*.rs")
$htmlcss = Get-CodeStats @("*.html","*.css")

$totalFiles = $java.count + $js.count + $dart.count + $rust.count + $htmlcss.count
$totalLines = $java.lines + $js.lines + $dart.lines + $rust.lines + $htmlcss.lines

Write-Output "JAVA_COUNT=$($java.count)"
Write-Output "JAVA_LINES=$($java.lines)"
Write-Output "JS_COUNT=$($js.count)"
Write-Output "JS_LINES=$($js.lines)"
Write-Output "DART_COUNT=$($dart.count)"
Write-Output "DART_LINES=$($dart.lines)"
Write-Output "RUST_COUNT=$($rust.count)"
Write-Output "RUST_LINES=$($rust.lines)"
Write-Output "HTMLCSS_COUNT=$($htmlcss.count)"
Write-Output "HTMLCSS_LINES=$($htmlcss.lines)"
Write-Output "TOTAL_FILES=$totalFiles"
Write-Output "TOTAL_LINES=$totalLines"
