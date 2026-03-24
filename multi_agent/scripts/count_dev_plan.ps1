$devPlanPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
$content = Get-Content $devPlanPath -Raw

# 统计功能状态
$completed = [regex]::Matches($content, "状态.*已完成|\[x\]|\[✅\]").Count
$inprogress = [regex]::Matches($content, "状态.*开发中|\[🔄\]").Count
$planned = [regex]::Matches($content, "状态.*待开发|\[📋\]").Count
$manual = [regex]::Matches($content, "状态.*待人工解决|\[⚠️\]").Count

# 从开头获取初始状态
$firstLines = (Get-Content $devPlanPath | Select-Object -First 20) -join "`n"
$initialStats = [regex]::Match($firstLines, "已完成功能:\s*(\d+).*?待开发功能:\s*(\d+)")

if ($initialStats.Success) {
    $initialCompleted = [int]$initialStats.Groups[1].Value
    $initialPlanned = [int]$initialStats.Groups[2].Value
} else {
    $initialCompleted = 0
    $initialPlanned = 0
}

# 提取功能表格
$features = @()
$featureLines = $content -split "`n"

foreach ($line in $featureLines) {
    if ($line -match "^###\s+(\d+)\.\s+(.+)$") {
        $features += @{
            Number = $matches[1]
            Name = $matches[2]
        }
    } elseif ($line -match "^\*\*状态\*\*:\s*(.+)$") {
        if ($features.Count -gt 0) {
            $features[-1].Status = $matches[1]
        }
    }
}

# 输出结果
Write-Host "=== 开发计划统计 ==="
Write-Host "已记录功能数量: $($features.Count)"
Write-Host "状态统计 (基于正则匹配):"
Write-Host "- ✅ 已完成: $completed 个"
Write-Host "- 🔄 开发中: $inprogress 个"
Write-Host "- 📋 待开发: $planned 个"
Write-Host "- ⚠️ 待人工解决: $manual 个"

Write-Host ""
Write-Host "文件头统计:"
Write-Host "- 已完成功能: $initialCompleted 个"
Write-Host "- 待开发功能: $initialPlanned 个"

Write-Host ""
Write-Host "=== 最新功能 ==="
if ($features.Count -gt 0) {
    Write-Host "最后 $([math]::Min(5, $features.Count)) 个功能:"
    $features | Select-Object -Last 5 | ForEach-Object {
        Write-Host "- #$($_.Number): $($_.Name) - $($_.Status)"
    }
}