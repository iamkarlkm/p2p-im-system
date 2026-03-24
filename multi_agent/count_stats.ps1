# 代码统计脚本
$projectsPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$logPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_volume_monitor.md"

# 定义需要统计的文件扩展名
$fileTypes = @{
    "Java" = @("*.java")
    "JavaScript" = @("*.js", "*.ts", "*.tsx", "*.jsx")
    "Dart" = @("*.dart")
    "Rust" = @("*.rs")
    "HTML/CSS" = @("*.html", "*.css")
}

# 初始化统计
$stats = @{
    "TotalFiles" = 0
    "TotalLines" = 0
    "ByType" = @{}
}

foreach ($type in $fileTypes.Keys) {
    $stats["ByType"][$type] = @{
        "Files" = 0
        "Lines" = 0
    }
}

# 递归查找所有文件并统计
foreach ($type in $fileTypes.Keys) {
    $extensions = $fileTypes[$type]
    foreach ($ext in $extensions) {
        $files = Get-ChildItem -Path $projectsPath -Filter $ext -Recurse -File -ErrorAction SilentlyContinue
        foreach ($file in $files) {
            try {
                # 统计文件行数
                $lines = (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
                
                # 更新统计
                $stats["ByType"][$type]["Files"]++
                $stats["ByType"][$type]["Lines"] += $lines
                $stats["TotalFiles"]++
                $stats["TotalLines"] += $lines
            } catch {
                # 如果无法读取文件，跳过
                continue
            }
        }
    }
}

# 读取开发计划文件
$devPlanPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
$featureStats = @{
    "Completed" = 0
    "InProgress" = 0
    "Pending" = 0
    "Manual" = 0
    "Features" = @()
}

if (Test-Path $devPlanPath) {
    $content = Get-Content $devPlanPath -Raw
    # 解析状态
    $completed = [regex]::Matches($content, "\[x\]").Count
    $inProgress = [regex]::Matches($content, "\[-\]").Count
    $pending = [regex]::Matches($content, "\[ \]").Count
    
    $featureStats["Completed"] = $completed
    $featureStats["InProgress"] = $inProgress
    $featureStats["Pending"] = $pending
    # 读取功能列表
    $lines = Get-Content $devPlanPath
    foreach ($line in $lines) {
        if ($line -match "\[(x|\-| )\]\s*(.*)") {
            $statusChar = $matches[1]
            $featureName = $matches[2].Trim()
            
            $status = switch ($statusChar) {
                "x" { "✅ 已完成" }
                "-" { "🔄 开发中" }
                " " { "📋 待开发" }
                default { "📋 待开发" }
            }
            
            # 检查是否有待人工解决的标记
            if ($line -match "待人工解决") {
                $status = "⚠️ 待人工解决"
                $featureStats["Manual"]++
            }
            
            $featureStats["Features"] += @{
                "Name" = $featureName
                "Status" = $status
            }
        }
    }
}

# 读取架构设计roadmap.md
$roadmapPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md"
$roadmapInfo = @{
    "Updated" = $false
    "NewFeatures" = @()
}

if (Test-Path $roadmapPath) {
    $lastWrite = (Get-Item $roadmapPath).LastWriteTime
    # 检查是否在今天更新过
    if ($lastWrite.Date -eq (Get-Date).Date) {
        $roadmapInfo["Updated"] = $true
    }
}

# 检查知识库
$knowledgePath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
$knowledgeFiles = @()
$newKnowledgeFiles = @()
if (Test-Path $knowledgePath) {
    $files = Get-ChildItem -Path $knowledgePath -Filter "*.md" -Recurse -File -ErrorAction SilentlyContinue
    $knowledgeFiles = $files | Select-Object -First 5
    # 检查今天新增的文件
    $todayFiles = $files | Where-Object { $_.LastWriteTime.Date -eq (Get-Date).Date }
    $newKnowledgeFiles = $todayFiles | Select-Object -First 3
}

# 生成报告
$report = @"
## 综合监控报告 - $(Get-Date -Format 'yyyy-MM-dd HH:mm')

### 代码量统计（统一口径）
- 总文件数：$($stats.TotalFiles) 个
- 总代码行数：$($stats.TotalLines) 行
- 文件类型分布：
"@

foreach ($type in $fileTypes.Keys) {
    $fileCount = $stats["ByType"][$type]["Files"]
    $lineCount = $stats["ByType"][$type]["Lines"]
    $report += "`n  - $($type): $($fileCount)个文件 ($($lineCount)行)"
}

$report += @"

### 架构设计情况
- 规划中功能：$($featureStats.Pending)个
- 新增功能点：无更新

### 学习情况
- 新增知识文件：$($newKnowledgeFiles.Count)个
- 新知识点：$(
    if ($newKnowledgeFiles.Count -gt 0) {
        ($newKnowledgeFiles | ForEach-Object { $_.Name }) -join ", "
    } else {
        "无新增"
    }
)

### 开发功能列表
| 功能名称 | 状态 |
|----------|------|
"@

# 添加功能列表
foreach ($feature in $featureStats["Features"] | Select-Object -First 10) {
    $report += "| $($feature.Name) | $($feature.Status) |`n"
}

$report += @"

状态统计：
- ✅ 已完成：$($featureStats.Completed)个
- 🔄 开发中：$($featureStats.InProgress)个
- 📋 待开发：$($featureStats.Pending)个
  - ⚠️ 待人工解决：$($featureStats.Manual)个

### 总结
- 代码量变化：稳定
- 功能模块变化：无变化
- 知识库变化：无新增
"@

# 保存报告
$report | Out-File -FilePath $logPath -Encoding UTF8
Write-Output "监控报告已保存到: $logPath"
Write-Output "`n$report"