$baseDir = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
$projects = @("im-backend","im-desktop","im-mobile")
$totalFiles = 0
$totalLines = 0
$javaFiles = 0; $javaLines = 0
$jsFiles = 0; $jsLines = 0
$dartFiles = 0; $dartLines = 0
$rustFiles = 0; $rustLines = 0
$htmlFiles = 0; $htmlLines = 0

foreach ($proj in $projects) {
    $projPath = Join-Path $baseDir $proj
    if (Test-Path $projPath) {
        # Java
        $jFiles = Get-ChildItem $projPath -Recurse -Include "*.java" -File -EA SilentlyContinue
        $jCnt = @($jFiles).Count
        $jLn = 0
        foreach ($f in $jFiles) {
            $lines = (Get-Content $f.FullName -EA SilentlyContinue | Measure-Object -Line).Lines
            if ($lines) { $jLn += $lines }
        }
        $javaFiles += $jCnt; $javaLines += $jLn

        # JS/TS
        $jsF = Get-ChildItem $projPath -Recurse -Include "*.js","*.ts","*.tsx","*.jsx" -File -EA SilentlyContinue
        $jsCnt = @($jsF).Count
        $jsLn = 0
        foreach ($f in $jsF) {
            $lines = (Get-Content $f.FullName -EA SilentlyContinue | Measure-Object -Line).Lines
            if ($lines) { $jsLn += $lines }
        }
        $jsFiles += $jsCnt; $jsLines += $jsLn

        # Dart
        $dFiles = Get-ChildItem $projPath -Recurse -Include "*.dart" -File -EA SilentlyContinue
        $dCnt = @($dFiles).Count
        $dLn = 0
        foreach ($f in $dFiles) {
            $lines = (Get-Content $f.FullName -EA SilentlyContinue | Measure-Object -Line).Lines
            if ($lines) { $dLn += $lines }
        }
        $dartFiles += $dCnt; $dartLines += $dLn

        # Rust
        $rFiles = Get-ChildItem $projPath -Recurse -Include "*.rs" -File -EA SilentlyContinue
        $rCnt = @($rFiles).Count
        $rLn = 0
        foreach ($f in $rFiles) {
            $lines = (Get-Content $f.FullName -EA SilentlyContinue | Measure-Object -Line).Lines
            if ($lines) { $rLn += $lines }
        }
        $rustFiles += $rCnt; $rustLines += $rLn

        # HTML/CSS
        $hFiles = Get-ChildItem $projPath -Recurse -Include "*.html","*.css" -File -EA SilentlyContinue
        $hCnt = @($hFiles).Count
        $hLn = 0
        foreach ($f in $hFiles) {
            $lines = (Get-Content $f.FullName -EA SilentlyContinue | Measure-Object -Line).Lines
            if ($lines) { $hLn += $lines }
        }
        $htmlFiles += $hCnt; $htmlLines += $hLn

        $projTotal = $jCnt + $jsCnt + $dCnt + $rCnt + $hCnt
        $projLines = $jLn + $jsLn + $dLn + $rLn + $hLn
        $totalFiles += $projTotal
        $totalLines += $projLines
        Write-Host "$proj : $projTotal files, $projLines lines"
    } else {
        Write-Host "$proj : NOT FOUND"
    }
}

Write-Host "---"
Write-Host "TOTAL: $totalFiles files, $totalLines lines"
Write-Host "Java: $javaFiles files, $javaLines lines"
Write-Host "JS/TS: $jsFiles files, $jsLines lines"
Write-Host "Dart: $dartFiles files, $dartLines lines"
Write-Host "Rust: $rustFiles files, $rustLines lines"
Write-Host "HTML/CSS: $htmlFiles files, $htmlLines lines"
