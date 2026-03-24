$knowledgePath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
if (Test-Path $knowledgePath) {
    $files = Get-ChildItem -Path $knowledgePath -File -Recurse -ErrorAction SilentlyContinue
    if ($files) {
        $recent = $files | Sort-Object LastWriteTime -Descending | Select-Object -First 10
        foreach ($f in $recent) {
            Write-Output "FILE=$($f.Name) TIME=$($f.LastWriteTime.ToString('yyyy-MM-dd HH:mm'))"
        }
    } else {
        Write-Output "EMPTY"
    }
} else {
    Write-Output "NO_DIR"
}
