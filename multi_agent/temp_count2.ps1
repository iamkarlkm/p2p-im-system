$projectDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

$javaFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.java -ErrorAction SilentlyContinue
$jsFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.js, *.ts, *.tsx, *.jsx -ErrorAction SilentlyContinue
$dartFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.dart -ErrorAction SilentlyContinue
$rustFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.rs -ErrorAction SilentlyContinue
$htmlcssFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.html, *.css -ErrorAction SilentlyContinue

$javaCount = $javaFiles.Count
$jsCount = $jsFiles.Count
$dartCount = $dartFiles.Count
$rustCount = $rustFiles.Count
$htmlcssCount = $htmlcssFiles.Count

$javaLines = 0
foreach ($file in $javaFiles) {
    try {
        $javaLines += (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    } catch {}
}

$jsLines = 0
foreach ($file in $jsFiles) {
    try {
        $jsLines += (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    } catch {}
}

$dartLines = 0
foreach ($file in $dartFiles) {
    try {
        $dartLines += (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    } catch {}
}

$rustLines = 0
foreach ($file in $rustFiles) {
    try {
        $rustLines += (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    } catch {}
}

$htmlcssLines = 0
foreach ($file in $htmlcssFiles) {
    try {
        $htmlcssLines += (Get-Content $file.FullName -ErrorAction SilentlyContinue | Measure-Object -Line).Lines
    } catch {}
}

$totalFiles = $javaCount + $jsCount + $dartCount + $rustCount + $htmlcssCount
$totalLines = $javaLines + $jsLines + $dartLines + $rustLines + $htmlcssLines

Write-Output "Total files: $totalFiles"
Write-Output "Total lines: $totalLines"
Write-Output "Java: $javaCount files, $javaLines lines"
Write-Output "JavaScript: $jsCount files, $jsLines lines"
Write-Output "Dart: $dartCount files, $dartLines lines"
Write-Output "Rust: $rustCount files, $rustLines lines"
Write-Output "HTML/CSS: $htmlcssCount files, $htmlcssLines lines"