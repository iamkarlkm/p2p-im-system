$projects = @{
    "im-backend" = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\im-backend"
    "im-desktop" = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\im-desktop"
    "im-mobile" = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\im-mobile"
}

foreach ($project in $projects.Keys) {
    $path = $projects[$project]
    $files = Get-ChildItem -Path $path -Recurse -Include *.java,*.js,*.ts,*.dart,*.rs,*.json -File
    $total = 0
    foreach ($f in $files) {
        $lines = (Get-Content $f.FullName | Measure-Object -Line).Lines
        $total += $lines
    }
    Write-Output "$project : $($files.Count) files, $total lines"
}
