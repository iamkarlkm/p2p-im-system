$kDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
if (Test-Path $kDir) {
    $files = Get-ChildItem $kDir -File -EA SilentlyContinue
    $count = @($files).Count
    Write-Host "Knowledge files total: $count"
    Write-Host "---"
    $files | Sort-Object LastWriteTime -Descending | Select-Object -First 20 | ForEach-Object {
        Write-Host "$($_.LastWriteTime.ToString('yyyy-MM-dd HH:mm')) - $($_.Name)"
    }
} else {
    Write-Host "NO_DIR"
}
