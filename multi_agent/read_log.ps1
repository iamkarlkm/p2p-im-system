$logPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_volume_monitor.md"
if (Test-Path $logPath) {
    $content = Get-Content $logPath -Raw
    if ($content -match "总代码行数.*?(\d+)") {
        Write-Output "LAST_LINES=$($matches[1])"
    }
    if ($content -match "总文件数.*?(\d+)") {
        Write-Output "LAST_FILES=$($matches[1])"
    }
} else {
    Write-Output "NO_LOG"
}
