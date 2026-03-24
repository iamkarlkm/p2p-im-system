$projectDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

$javaFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.java -ErrorAction SilentlyContinue
$jsFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.js, *.ts, *.tsx, *.jsx -ErrorAction SilentlyContinue
$dartFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.dart -ErrorAction SilentlyContinue
$rustFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.rs -ErrorAction SilentlyContinue
$htmlcssFiles = Get-ChildItem -Path $projectDir -Recurse -Include *.html, *.css -ErrorAction SilentlyContinue

Write-Output "File counts:"
Write-Output "Java: $($javaFiles.Count) files"
Write-Output "JavaScript: $($jsFiles.Count) files"
Write-Output "Dart: $($dartFiles.Count) files"
Write-Output "Rust: $($rustFiles.Count) files"
Write-Output "HTML/CSS: $($htmlcssFiles.Count) files"

$totalFiles = $javaFiles.Count + $jsFiles.Count + $dartFiles.Count + $rustFiles.Count + $htmlcssFiles.Count
Write-Output "Total: $totalFiles files"