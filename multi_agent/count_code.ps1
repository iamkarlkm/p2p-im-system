$extensions = '*.java','*.js','*.ts','*.tsx','*.jsx','*.rs','*.go','*.py','*.dart','*.swift','*.kt','*.scala','*.c','*.cpp','*.h','*.hpp','*.cs'
$root = 'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'

# Get all matching files
$files = Get-ChildItem -Path $root -Recurse -Include $extensions -File -ErrorAction SilentlyContinue
$totalFiles = $files.Count
$totalLines = 0

foreach ($file in $files) {
    $lines = (Get-Content $file.FullName -ErrorAction SilentlyContinue).Count
    $totalLines += $lines
}

# Also get per-project stats
$projects = Get-ChildItem -Path $root -Directory
foreach ($proj in $projects) {
    $projFiles = Get-ChildItem -Path $proj.FullName -Recurse -Include $extensions -File -ErrorAction SilentlyContinue
    $projLines = 0
    foreach ($f in $projFiles) {
        $projLines += (Get-Content $f.FullName -ErrorAction SilentlyContinue).Count
    }
    Write-Output "PROJECT:$($proj.Name)|$($projFiles.Count)|$projLines"
}

Write-Output "TOTAL:$totalFiles|$totalLines"
