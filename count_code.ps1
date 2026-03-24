# Code statistics PowerShell script
$basePath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

# Define file patterns to count
$filePatterns = @(
    "*.java",
    "*.js", "*.ts", "*.tsx", "*.jsx",
    "*.dart",
    "*.rs",
    "*.html",
    "*.css"
)

# Initialize statistics
$totalFiles = 0
$totalLines = 0
$typeStats = @{
    Java = @{Count=0; Lines=0}
    JavaScript = @{Count=0; Lines=0}
    Dart = @{Count=0; Lines=0}
    Rust = @{Count=0; Lines=0}
    HTML = @{Count=0; Lines=0}
    CSS = @{Count=0; Lines=0}
}

# Get previous statistics (if exists)
$logFile = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_volume_monitor.md"
$previousTotalLines = 0
if (Test-Path $logFile) {
    $content = Get-Content $logFile -Raw
    if ($content -match "总代码行数：\[(\d+)\]") {
        $previousTotalLines = [int]$matches[1]
    }
}

# Count each file type
foreach ($pattern in $filePatterns) {
    $files = Get-ChildItem -Path $basePath -Recurse -Include $pattern -ErrorAction SilentlyContinue
    foreach ($file in $files) {
        $lineCount = 0
        try {
            $lineCount = (Get-Content $file.FullName | Measure-Object -Line).Lines
        } catch {
            $lineCount = 0
        }
        
        # Categorize by file type
        $type = ""
        switch ($file.Extension.ToLower()) {
            ".java" { $type = "Java" }
            ".js" { $type = "JavaScript" }
            ".ts" { $type = "JavaScript" }
            ".tsx" { $type = "JavaScript" }
            ".jsx" { $type = "JavaScript" }
            ".dart" { $type = "Dart" }
            ".rs" { $type = "Rust" }
            ".html" { $type = "HTML" }
            ".css" { $type = "CSS" }
        }
        
        if ($type -and $typeStats.ContainsKey($type)) {
            $typeStats[$type].Count++
            $typeStats[$type].Lines += $lineCount
            $totalFiles++
            $totalLines += $lineCount
        }
    }
}

# Calculate line change
$lineChange = $totalLines - $previousTotalLines
$changeText = if ($lineChange -gt 0) { "increase $lineChange lines" } elseif ($lineChange -lt 0) { "decrease $(-$lineChange) lines" } else { "no change" }

# Output statistics
Write-Host "=== Code Statistics ==="
Write-Host "Total Files: $totalFiles"
Write-Host "Total Lines: $totalLines"
Write-Host "Line Change: $changeText"
Write-Host ""

Write-Host "=== Type Distribution ==="
foreach ($type in $typeStats.Keys | Sort-Object) {
    $typeCount = $typeStats[$type].Count
    $typeLines = $typeStats[$type].Lines
    Write-Host "$type: $typeCount files, $typeLines lines"
}

# Return result as JSON
$result = [PSCustomObject]@{
    TotalFiles = $totalFiles
    TotalLines = $totalLines
    JavaFiles = $typeStats["Java"].Count
    JavaLines = $typeStats["Java"].Lines
    JavaScriptFiles = $typeStats["JavaScript"].Count
    JavaScriptLines = $typeStats["JavaScript"].Lines
    DartFiles = $typeStats["Dart"].Count
    DartLines = $typeStats["Dart"].Lines
    RustFiles = $typeStats["Rust"].Count
    RustLines = $typeStats["Rust"].Lines
    HTMLFiles = $typeStats["HTML"].Count
    HTMLLines = $typeStats["HTML"].Lines
    CSSFiles = $typeStats["CSS"].Count
    CSSLines = $typeStats["CSS"].Lines
    LineChange = $lineChange
}

$result | ConvertTo-Json