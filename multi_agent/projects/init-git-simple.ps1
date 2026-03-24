# 简化版IM项目Git初始化脚本

Write-Host "=== IM项目Git环境初始化 ==="

# 检查是否在项目目录中
$hasBackend = Test-Path "im-backend"
$hasDesktop = Test-Path "im-desktop" 
$hasMobile = Test-Path "im-mobile"

if (-not ($hasBackend -or $hasDesktop -or $hasMobile)) {
    Write-Host "错误: 请在IM项目目录中运行此脚本"
    exit 1
}

Write-Host "项目目录: $(Get-Location)"

# 1. 检查Git
try {
    git --version | Out-Null
    Write-Host "✅ Git已安装"
} catch {
    Write-Host "❌ Git未安装"
    exit 1
}

# 2. 配置用户
Write-Host ""
Write-Host "配置Git用户信息:"
git config user.name "暗精灵"
git config user.email "im-dev@example.com"
Write-Host "✅ 用户信息已配置"

# 3. 初始化Git仓库
if (-not (Test-Path ".git")) {
    Write-Host "初始化Git仓库..."
    git init
    Write-Host "✅ Git仓库已初始化"
}

# 4. 创建develop分支
git checkout -b develop 2>$null
Write-Host "✅ develop分支已创建/切换"

# 5. 创建基础文件
Write-Host "创建基础文件..."

# .gitignore
$gitignore = @"
# 构建产物
build/
dist/
target/
*.jar
*.war
*.ear

# 依赖
node_modules/
vendor/
.gradle/

# 开发环境
.env
.env.local

# 编辑器
.vscode/
.idea/

# 日志
logs/
*.log

# 系统文件
.DS_Store
Thumbs.db

# IM项目特定
*.keystore
*.p12
*.cert
*.key

# 测试报告
coverage/
test-results/
"@

Set-Content -Path ".gitignore" -Value $gitignore -Encoding UTF8
Write-Host "✅ .gitignore已创建"

# README.md
$readme = @"
# IM即时通讯系统

## 项目概述
基于多端架构的即时通讯系统。

## 技术栈
- 后端: Java Spring Boot
- 桌面端: Tauri + TypeScript  
- 移动端: Flutter + Dart

## 开发规范
请参考 workflows/git/im-project-git-workflow.md

## 许可证
版权所有 (c) 2026 IM开发团队
"@

Set-Content -Path "README.md" -Value $readme -Encoding UTF8
Write-Host "✅ README.md已创建"

# 6. 初始提交
Write-Host ""
Write-Host "执行初始提交..."

git add .
$commitMsg = @"
chore: 初始化IM项目Git环境

- 配置Git用户信息
- 创建基础分支结构
- 添加.gitignore文件
- 创建项目文档
"@

git commit -m $commitMsg

$commitHash = git rev-parse --short HEAD
Write-Host "✅ 初始提交完成"
Write-Host "提交哈希: $commitHash"

# 7. 显示总结
Write-Host ""
Write-Host "=== 初始化完成 ==="
Write-Host "✅ Git用户配置"
Write-Host "✅ develop分支"
Write-Host "✅ .gitignore文件"
Write-Host "✅ README.md文档"
Write-Host "✅ 初始提交"

Write-Host ""
Write-Host "下一步:"
Write-Host "1. 添加远程仓库: git remote add origin <url>"
Write-Host "2. 推送到远程: git push -u origin develop"
Write-Host "3. 开始开发: git checkout -b feature/功能名称"