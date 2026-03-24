$planPath = "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
$content = Get-Content $planPath -Raw -ErrorAction SilentlyContinue

$completedCount = 0
$inProgressCount = 0
$pendingCount = 0
$needsHumanCount = 0

if ($content) {
    $completedCount = ($content | Select-String -Pattern '状态\s*:\s*已完成' -AllMatches).Matches.Count
    $inProgressCount = ($content | Select-String -Pattern '状态\s*:\s*开发中' -AllMatches).Matches.Count
    $pendingCount = ($content | Select-String -Pattern '状态\s*:\s*待开发' -AllMatches).Matches.Count
    $needsHumanCount = ($content | Select-String -Pattern '状态\s*:\s*待人工解决' -AllMatches).Matches.Count
}

Write-Output "Completed: $completedCount"
Write-Output "In progress: $inProgressCount"
Write-Output "Pending: $pendingCount"
Write-Output "Needs human: $needsHumanCount"
Write-Output "Total: $($completedCount + $inProgressCount + $pendingCount + $needsHumanCount)"