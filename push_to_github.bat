@echo off
echo 正在推送IM项目到GitHub...
echo 仓库: https://github.com/iamkarlkm/im-system
echo 分支: main
echo 提交数量: 2个提交
echo.

git push origin main
if %errorlevel% equ 0 (
    echo.
    echo ✅ 推送成功!
    echo GitHub仓库地址: https://github.com/iamkarlkm/im-system
) else (
    echo.
    echo ❌ 推送失败，请检查GitHub token和网络连接
)

pause