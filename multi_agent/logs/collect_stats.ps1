$projectsDir = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
$knowledgeDir = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge'
$logsDir = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs'

Write-Host '=== Directory Check ==='
Write-Host "projects exists: $(Test-Path $projectsDir)"
Write-Host "knowledge exists: $(Test-Path $knowledgeDir)"
Write-Host "logs exists: $(Test-Path $logsDir)"

Write-Host '=== File Counts ==='
$java = (Get-ChildItem -Path $projectsDir -Recurse -Include *.java -File -ErrorAction SilentlyContinue).Count
$js = (Get-ChildItem -Path $projectsDir -Recurse -Include *.js,*.ts,*.tsx,*.jsx -File -ErrorAction SilentlyContinue).Count
$dart = (Get-ChildItem -Path $projectsDir -Recurse -Include *.dart -File -ErrorAction SilentlyContinue).Count
$rs = (Get-ChildItem -Path $projectsDir -Recurse -Include *.rs -File -ErrorAction SilentlyContinue).Count
$htmlcss = (Get-ChildItem -Path $projectsDir -Recurse -Include *.html,*.css -File -ErrorAction SilentlyContinue).Count
Write-Host "Java: $java"
Write-Host "JS/TS: $js"
Write-Host "Dart: $dart"
Write-Host "Rust: $rs"
Write-Host "HTML/CSS: $htmlcss"

Write-Host '=== Line Counts ==='
$jFiles = Get-ChildItem -Path $projectsDir -Recurse -Include *.java -File -ErrorAction SilentlyContinue
$jsFiles = Get-ChildItem -Path $projectsDir -Recurse -Include *.js,*.ts,*.tsx,*.jsx -File -ErrorAction SilentlyContinue
$dFiles = Get-ChildItem -Path $projectsDir -Recurse -Include *.dart -File -ErrorAction SilentlyContinue
$rsFiles = Get-ChildItem -Path $projectsDir -Recurse -Include *.rs -File -ErrorAction SilentlyContinue
$htmlFiles = Get-ChildItem -Path $projectsDir -Recurse -Include *.html,*.css -File -ErrorAction SilentlyContinue

$javaLines = 0
if ($jFiles) { foreach($f in $jFiles) { $javaLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines } }
$jsLines = 0
if ($jsFiles) { foreach($f in $jsFiles) { $jsLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines } }
$dartLines = 0
if ($dFiles) { foreach($f in $dFiles) { $dartLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines } }
$rsLines = 0
if ($rsFiles) { foreach($f in $rsFiles) { $rsLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines } }
$htmlLines = 0
if ($htmlFiles) { foreach($f in $htmlFiles) { $htmlLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines } }

Write-Host "Java lines: $javaLines"
Write-Host "JS/TS lines: $jsLines"
Write-Host "Dart lines: $dartLines"
Write-Host "Rust lines: $rsLines"
Write-Host "HTML/CSS lines: $htmlLines"

$totalFiles = $java + $js + $dart + $rs + $htmlcss
$totalLines = [int]$javaLines + [int]$jsLines + [int]$dartLines + [int]$rsLines + [int]$htmlLines
Write-Host "Total files: $totalFiles"
Write-Host "Total lines: $totalLines"

# List knowledge files
Write-Host '=== Knowledge Files ==='
if (Test-Path $knowledgeDir) {
    Get-ChildItem -Path $knowledgeDir -File -ErrorAction SilentlyContinue | ForEach-Object {
        Write-Host "$($_.Name) - $($_.LastWriteTime.ToString('yyyy-MM-dd HH:mm'))"
    }
} else {
    Write-Host "Knowledge directory not found"
}

# List projects subdirs
Write-Host '=== Projects Structure ==='
if (Test-Path $projectsDir) {
    Get-ChildItem -Path $projectsDir -Directory -ErrorAction SilentlyContinue | ForEach-Object {
        Write-Host "Project: $($_.Name)"
        Get-ChildItem -Path $_.FullName -File -ErrorAction SilentlyContinue | ForEach-Object {
            Write-Host "  File: $($_.Name) - $($_.LastWriteTime.ToString('yyyy-MM-dd HH:mm'))"
        }
    }
}
