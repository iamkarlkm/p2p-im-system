<#
代码量统计脚本 - PowerShell版本
统计指定目录下的代码文件
#>

# 定义目录路径
$projectsDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$knowledgeDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
$logDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs"

# 确保日志目录存在
if (-not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir -Force
}

# 需要统计的文件类型
$codeExtensions = @{
    "Java" = @(".java")
    "JavaScript/TypeScript" = @(".js", ".ts", ".tsx", ".jsx")
    "Dart" = @(".dart")
    "Rust" = @(".rs")
    "HTML/CSS" = @(".html", ".css")
}

# 初始化统计变量
$stats = @{
    totalFiles = 0
    totalLines = 0
    byType = @{}
    byExtension = @{}
}

foreach ($typeName in $codeExtensions.Keys) {
    $stats.byType[$typeName] = @{files = 0; lines = 0}
}

# 统计代码文件
Write-Host "正在统计代码文件..." -ForegroundColor Green

Get-ChildItem -Path $projectsDir -Recurse -File | ForEach-Object {
    $extension = $_.Extension.ToLower()
    
    # 检查文件类型
    $fileType = $null
    foreach ($typeName in $codeExtensions.Keys) {
        if ($codeExtensions[$typeName] -contains $extension) {
            $fileType = $typeName
            break
        }
    }
    
    if ($fileType) {
        $stats.totalFiles++
        
        if (-not $stats.byExtension.ContainsKey($extension)) {
            $stats.byExtension[$extension] = 0
        }
        $stats.byExtension[$extension]++
        
        $stats.byType[$fileType].files++
        
        # 统计行数
        try {
            $lineCount = (Get-Content $_.FullName -ErrorAction Stop).Count
            $stats.totalLines += $lineCount
            $stats.byType[$fileType].lines += $lineCount
        } catch {
            Write-Warning "无法读取文件: $($_.FullName)"
        }
    }
}

# 统计知识库文件
Write-Host "正在统计知识库文件..." -ForegroundColor Green

$knowledgeStats = @{
    totalFiles = 0
    files = @()
}

if (Test-Path $knowledgeDir) {
    $knowledgeFiles = Get-ChildItem -Path $knowledgeDir -Recurse -Filter "*.md" -File
    $knowledgeStats.totalFiles = $knowledgeFiles.Count
    $knowledgeStats.files = $knowledgeFiles | ForEach-Object { $_.Name } | Select-Object -First 5
}

# 获取路线图信息
Write-Host "正在检查路线图..." -ForegroundColor Green

$roadmapInfo = @{
    exists = $false
    featureCount = 0
}

$roadmapPath = Join-Path $projectsDir "roadmap.md"
if (Test-Path $roadmapPath) {
    try {
        $content = Get-Content $roadmapPath -Raw -ErrorAction Stop
        $roadmapInfo.exists = $true
        # 统计标题数量作为功能估算
        $featureCount = ($content -split "`n" | Where-Object { $_ -match "^#+" }).Count
        $roadmapInfo.featureCount = $featureCount
    } catch {
        $roadmapInfo.error = $_.Exception.Message
    }
}

# 获取开发计划状态
Write-Host "正在检查开发计划..." -ForegroundColor Green

$devPlanStatus = @{
    exists = $false
    features = @()
    statusCounts = @{
        "✅ 已完成" = 0
        "🔄 开发中" = 0
        "📋 待开发" = 0
        "⚠️ 待人工解决" = 0
    }
}

$devPlanPath = Join-Path $projectsDir "development_plan.md"
if (Test-Path $devPlanPath) {
    try {
        $content = Get-Content $devPlanPath -ErrorAction Stop
        
        foreach ($line in $content) {
            $line = $line.Trim()
            if ($line) {
                $status = $null
                $featureName = $null
                
                if ($line -match "✅") {
                    $status = "✅ 已完成"
                    $featureName = $line -replace "✅", "" -replace "\[X\]", "" -replace "\[x\]", ""
                } elseif ($line -match "🔄") {
                    $status = "🔄 开发中"
                    $featureName = $line -replace "🔄", "" -replace "\[-\]", "" -replace "\[ \]", ""
                } elseif ($line -match "📋") {
                    $status = "📋 待开发"
                    $featureName = $line -replace "📋", "" -replace "\[ \]", "" -replace "\[-\]", ""
                } elseif ($line -match "⚠️") {
                    $status = "⚠️ 待人工解决"
                    $featureName = $line -replace "⚠️", "" -replace "\[!\?\]", "" -replace "\[!\]", ""
                }
                
                if ($status -and $featureName) {
                    $featureName = $featureName.Trim()
                    if ($featureName) {
                        $feature = @{name = $featureName; status = $status}
                        $devPlanStatus.features += $feature
                        $devPlanStatus.statusCounts[$status]++
                    }
                }
            }
        }
        
        $devPlanStatus.exists = $true
    } catch {
        $devPlanStatus.error = $_.Exception.Message
    }
}

# 生成报告
$currentTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$report = @"
## 综合监控报告 - $currentTime

### 代码量统计（统一口径）
- 总文件数：$($stats.totalFiles)
- 总代码行数：$($stats.totalLines)
- 文件类型分布：
"@

foreach ($typeName in $stats.byType.Keys | Sort-Object) {
    $typeStats = $stats.byType[$typeName]
    $report += "  - $typeName : $($typeStats.files)个文件，$($typeStats.lines)行`n"
}

$report += @"

### 架构设计情况
"@

if ($roadmapInfo.exists) {
    $report += "- 规划中功能：$($roadmapInfo.featureCount)个`n"
    $report += "- 最近更新：已检测到roadmap.md文件`n"
} else {
    $report += "- 规划中功能：未找到roadmap.md文件`n"
}

$report += @"

### 学习情况
- 新增知识文件：$($knowledgeStats.totalFiles)个
- 新知识点：
"@

if ($knowledgeStats.files.Count -gt 0) {
    foreach ($file in $knowledgeStats.files) {
        $report += "  - $file`n"
    }
} else {
    $report += "  - 暂无新增知识文件`n"
}

$report += @"

### 开发功能列表
"@

if ($devPlanStatus.exists -and $devPlanStatus.features.Count -gt 0) {
    $report += "| 功能名称 | 状态 |`n|----------|------|`n"
    
    $count = 0
    foreach ($feature in $devPlanStatus.features) {
        if ($count -lt 15) {  # 只显示前15个
            $report += "| $($feature.name) | $($feature.status) |`n"
            $count++
        }
    }
    
    $counts = $devPlanStatus.statusCounts
    $totalFeatures = $devPlanStatus.features.Count
    $completionRate = if ($totalFeatures -gt 0) { [math]::Round(($counts['✅ 已完成'] / $totalFeatures) * 100, 1) } else { 0 }
    
    $report += @"

状态统计：
- ✅ 已完成：$($counts['✅ 已完成'])个
- 🔄 开发中：$($counts['🔄 开发中'])个
- 📋 待开发：$($counts['📋 待开发'])个
  - ⚠️ 待人工解决：$($counts['⚠️ 待人工解决'])个

### 总结
- 代码量变化：$($stats.totalFiles)个文件，$($stats.totalLines)行代码
- 功能模块变化：$totalFeatures个功能，完成率$completionRate%
- 知识库变化：$($knowledgeStats.totalFiles)个知识文件
"@
} else {
    $report += @"
- 未找到development_plan.md文件或文件为空

### 总结
- 代码量变化：$($stats.totalFiles)个文件，$($stats.totalLines)行代码
- 功能模块变化：开发计划文件未找到
- 知识库变化：$($knowledgeStats.totalFiles)个知识文件
"@
}

# 保存报告
$logFile = Join-Path $logDir "code_volume_monitor.md"
$report | Out-File -FilePath $logFile -Encoding UTF8

Write-Host "监控报告已保存到: $logFile" -ForegroundColor Green
Write-Host "`n=== 监控报告内容 ===" -ForegroundColor Cyan
Write-Host $report