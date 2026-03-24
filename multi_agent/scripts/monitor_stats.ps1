$projectPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$outputPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_stats_report.md"

# 确保日志目录存在
if (!(Test-Path "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs")) {
    New-Item -ItemType Directory -Path "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs" -Force
}

# 定义要统计的文件扩展名
$extensions = @(
    "*.java",
    "*.js",
    "*.ts",
    "*.tsx",
    "*.jsx",
    "*.dart",
    "*.rs",
    "*.html",
    "*.css"
)

Write-Host "开始统计项目代码量..."
Write-Host "项目路径: $projectPath"

# 统计每种类型的文件
$stats = @{}
$totalFiles = 0
$totalLines = 0

foreach ($ext in $extensions) {
    Write-Host "搜索扩展名: $ext"
    
    # 获取所有文件
    $files = Get-ChildItem -Path $projectPath -Filter $ext -Recurse -File
    
    if ($files.Count -gt 0) {
        $fileCount = $files.Count
        $lineCount = 0
        
        foreach ($file in $files) {
            try {
                $content = Get-Content $file.FullName -ErrorAction SilentlyContinue
                if ($content) {
                    $lineCount += $content.Count
                }
            } catch {
                Write-Host "  读取文件时出错: $($file.FullName)"
            }
        }
        
        $typeName = switch ($ext) {
            "*.java" { "Java" }
            "*.js" { "JavaScript" }
            "*.ts" { "TypeScript" }
            "*.tsx" { "TypeScript (React)" }
            "*.jsx" { "JavaScript (React)" }
            "*.dart" { "Dart" }
            "*.rs" { "Rust" }
            "*.html" { "HTML" }
            "*.css" { "CSS" }
        }
        
        $stats[$typeName] = @{
            FileCount = $fileCount
            LineCount = $lineCount
        }
        
        $totalFiles += $fileCount
        $totalLines += $lineCount
        
        Write-Host "  $typeName: $fileCount 个文件, $lineCount 行"
    }
}

# 检查架构设计情况
$roadmapPath = "$projectPath\roadmap.md"
$planningCount = 0
$newFeatures = @()

if (Test-Path $roadmapPath) {
    Write-Host "检查架构设计文件: $roadmapPath"
    
    try {
        $roadmapContent = Get-Content $roadmapPath -Raw
        $lastModified = (Get-Item $roadmapPath).LastWriteTime
        
        # 简单分析规划中功能
        $lines = $roadmapContent -split "`n"
        foreach ($line in $lines) {
            if ($line -match "^\s*-\s*\[.*\]\s*.+" -or $line -match "^\s*(\*|\+)\s*.+") {
                $planningCount++
            }
            if ($line -match "新增" -or $line -match "新功能") {
                $newFeatures += $line.Trim()
            }
        }
        
        Write-Host "  规划中功能: $planningCount 个"
    } catch {
        Write-Host "  读取架构设计文件时出错"
    }
}

# 检查学习情况
$knowledgePath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
$newKnowledgeFiles = @()
$newKnowledgePoints = @()

if (Test-Path $knowledgePath) {
    Write-Host "检查知识库目录: $knowledgePath"
    
    try {
        # 获取24小时内修改的文件
        $recentFiles = Get-ChildItem -Path $knowledgePath -Recurse -File | 
                      Where-Object { $_.LastWriteTime -gt (Get-Date).AddHours(-24) }
        
        $newKnowledgeFiles = $recentFiles | ForEach-Object { $_.Name }
        $newKnowledgeCount = $recentFiles.Count
        
        Write-Host "  最近24小时新增/修改的文件: $newKnowledgeCount 个"
    } catch {
        Write-Host "  检查知识库时出错"
    }
}

# 检查开发功能列表
$devPlanPath = "$projectPath\development_plan.md"
$completedCount = 0
$inProgressCount = 0
$pendingCount = 0
$manualResolveCount = 0
$featureList = @()

if (Test-Path $devPlanPath) {
    Write-Host "读取开发功能列表: $devPlanPath"
    
    try {
        $devPlanContent = Get-Content $devPlanPath -Raw
        $lines = $devPlanContent -split "`n"
        
        foreach ($line in $lines) {
            if ($line -match "^\s*\|\s*([^|]+)\s*\|\s*([^|]+)\s*\|") {
                $featureName = $matches[1].Trim()
                $status = $matches[2].Trim()
                
                $featureList += @{
                    Name = $featureName
                    Status = $status
                }
                
                if ($status -match "✅" -or $status -match "完成") {
                    $completedCount++
                } elseif ($status -match "🔄" -or $status -match "开发中") {
                    $inProgressCount++
                } elseif ($status -match "📋" -or $status -match "待开发") {
                    $pendingCount++
                } elseif ($status -match "⚠️" -or $status -match "待人工解决") {
                    $manualResolveCount++
                }
            }
        }
        
        Write-Host "  开发功能总数: $($featureList.Count) 个"
    } catch {
        Write-Host "  读取开发计划时出错"
    }
}

# 生成报告
$currentTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$report = @"
## 综合监控报告 - $currentTime

### 代码量统计（统一口径）
- 总文件数：$totalFiles
- 总代码行数：$totalLines
- 文件类型分布：
"@

foreach ($key in $stats.Keys) {
    $fileCount = $stats[$key].FileCount
    $lineCount = $stats[$key].LineCount
    $report += "`n  - $key: ${fileCount}个文件, ${lineCount}行"
}

$report += @"

### 架构设计情况
- 规划中功能：$planningCount 个
- 新增功能点：
"@

if ($newFeatures.Count -gt 0) {
    foreach ($feature in $newFeatures) {
        $report += "`n  - $feature"
    }
} else {
    $report += "`n  - 无新增功能点"
}

$report += @"

### 学习情况
- 新增知识文件：$($newKnowledgeFiles.Count) 个
"@

if ($newKnowledgeFiles.Count -gt 0) {
    $report += "`n- 新知识点："
    foreach ($file in $newKnowledgeFiles) {
        $report += "`n  - $file"
    }
} else {
    $report += "`n- 新知识点：无新增"
}

$report += @"

### 开发功能列表
| 功能名称 | 状态 |
|----------|------|
"@

foreach ($feature in $featureList) {
    $report += "| $($feature.Name) | $($feature.Status) |`n"
}

$report += @"
状态统计：
- ✅ 已完成：$completedCount 个
- 🔄 开发中：$inProgressCount 个
- 📋 待开发：$pendingCount 个
  - ⚠️ 待人工解决：$manualResolveCount 个

### 总结
- 代码量变化：需要与上次统计数据对比
- 功能模块变化：待对比
- 知识库变化：待对比

---
*统计路径：$projectPath*
*知识库路径：$knowledgePath*
"@

# 保存报告
$report | Out-File -FilePath $outputPath -Encoding UTF8
Write-Host "报告已保存到: $outputPath"

# 显示报告摘要
Write-Host "`n=== 监控报告摘要 ==="
Write-Host "代码量: $totalFiles 个文件, $totalLines 行"
Write-Host "开发功能: ✅$completedCount 🔄$inProgressCount 📋$pendingCount ⚠️$manualResolveCount"
Write-Host "架构设计: $planningCount 个规划功能"
Write-Host "知识库: $($newKnowledgeFiles.Count) 个新文件"