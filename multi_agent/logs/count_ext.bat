@echo off
setlocal enabledelayedexpansion

set "BASE=C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

for %%e in (java,js,ts,tsx,jsx,dart,rs,html,css) do (
    set "ext=%%e"
    set "count=0"
    for /f "delims=" %%f in ('dir /s /b "%BASE%\*.%%e" 2^>nul ^| find /c /v ""') do set "count=%%f"
    echo EXT:%%e:!count!
)
echo DONE
