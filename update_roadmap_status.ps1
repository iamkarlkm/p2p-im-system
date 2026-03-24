# PowerShell脚本更新roadmap状态
$roadmapPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md"
$content = Get-Content -Path $roadmapPath -Raw

# 替换#129-#137功能的状态
for ($i = 129; $i -le 137; $i++) {
    $pattern = "(?s)## 🆕 功能 #$i.*?\*\*状态\*\*: 待开发"
    $replacement = "## 🆕 功能 #$i`$0**状态**: ✅ 已转移到开发计划"
    $content = $content -replace $pattern, $replacement
}

# 写入更新后的内容
Set-Content -Path $roadmapPath -Value $content -Encoding UTF8
Write-Host "已更新roadmap中#129-#137功能的状态"