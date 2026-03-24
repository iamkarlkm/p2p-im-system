$knowledgeDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
$recentFiles = Get-ChildItem -Path $knowledgeDir -File | Where-Object {$_.LastWriteTime -gt (Get-Date).AddHours(-24)}

Write-Output "Recent knowledge files (last 24 hours): $($recentFiles.Count)"
foreach ($file in $recentFiles) {
    Write-Output "$($file.Name) - $($file.LastWriteTime)"
}