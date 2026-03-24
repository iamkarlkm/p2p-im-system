# IM项目Git工作流初始化脚本 (PowerShell版本)

Write-Host "🌳 IM项目Git工作流初始化" -ForegroundColor Cyan
Write-Host "==========================" -ForegroundColor Cyan

# 检查是否在项目目录中
$hasProjectDirs = (Test-Path "backend") -or (Test-Path "desktop") -or (Test-Path "mobile")
if (-not $hasProjectDirs) {
    Write-Host "❌ 错误: 请在IM项目根目录运行此脚本" -ForegroundColor Red
    exit 1
}

Write-Host "📁 项目目录: $($pwd.Path)" -ForegroundColor Yellow

# 1. 检查Git是否安装
try {
    $gitVersion = git --version
    Write-Host "✅ Git已安装: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Git未安装，请先安装Git" -ForegroundColor Red
    exit 1
}

# 2. 配置用户信息
Write-Host ""
Write-Host "📝 配置Git用户信息" -ForegroundColor Cyan

$username = Read-Host "请输入用户名 [默认: IM开发团队]"
if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "IM开发团队"
}

$email = Read-Host "请输入邮箱 [默认: im-dev@example.com]"
if ([string]::IsNullOrWhiteSpace($email)) {
    $email = "im-dev@example.com"
}

git config user.name "$username"
git config user.email "$email"

Write-Host "✅ 用户信息已配置: $username <$email>" -ForegroundColor Green

# 3. 创建基础分支结构
Write-Host ""
Write-Host "🌿 初始化分支结构" -ForegroundColor Cyan

# 检查是否已初始化Git仓库
if (-not (Test-Path ".git")) {
    Write-Host "📦 初始化Git仓库" -ForegroundColor Yellow
    git init
}

# 创建开发分支（如果不存在）
$hasDevelop = git branch --list develop
if ([string]::IsNullOrWhiteSpace($hasDevelop)) {
    git checkout -b develop
    Write-Host "✅ 创建develop分支" -ForegroundColor Green
} else {
    git checkout develop
    Write-Host "✅ 切换到develop分支" -ForegroundColor Green
}

# 4. 创建.gitignore文件
Write-Host ""
Write-Host "📄 创建.gitignore文件" -ForegroundColor Cyan

$gitignoreContent = @"
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
.env.development.local
.env.test.local
.env.production.local

# 编辑器
.vscode/
.idea/
*.swp
*.swo

# 日志
logs/
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*

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

# 临时文件
*.tmp
*.temp

# IDE设置
.settings/
.project
.classpath
"@

Set-Content -Path ".gitignore" -Value $gitignoreContent -Encoding UTF8
Write-Host "✅ .gitignore文件已创建" -ForegroundColor Green

# 5. 创建基础文档
Write-Host ""
Write-Host "📚 创建基础文档" -ForegroundColor Cyan

# README.md
if (-not (Test-Path "README.md")) {
    $readmeContent = @"
# IM即时通讯系统

## 项目概述
基于多端架构的即时通讯系统，支持文本、语音、视频消息传输。

## 技术栈
- **后端**: Java Spring Boot
- **桌面端**: Tauri + TypeScript
- **移动端**: Flutter + Dart

## 快速开始
```bash
# 克隆项目
git clone <repository-url>

# 启动后端
cd backend && ./mvnw spring-boot:run

# 启动桌面端
cd desktop && npm run dev

# 启动移动端
cd mobile && flutter run
```

## 开发规范
请参考 `workflows/git/im-project-git-workflow.md` 中的Git工作流规范。

## 许可证
版权所有 © 2026 IM开发团队
"@
    Set-Content -Path "README.md" -Value $readmeContent -Encoding UTF8
    Write-Host "✅ README.md已创建" -ForegroundColor Green
}

# CHANGELOG.md
if (-not (Test-Path "CHANGELOG.md")) {
    $changelogContent = @"
# 变更日志

本项目遵循[语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-03-24
### 新增
- 项目初始化
- 基础Git工作流配置
- 多端架构搭建
"@
    Set-Content -Path "CHANGELOG.md" -Value $changelogContent -Encoding UTF8
    Write-Host "✅ CHANGELOG.md已创建" -ForegroundColor Green
}

# 6. 创建脚本目录
Write-Host ""
Write-Host "🛠️ 创建工具脚本" -ForegroundColor Cyan

New-Item -ItemType Directory -Path "scripts\git" -Force | Out-Null
New-Item -ItemType Directory -Path "scripts\build" -Force | Out-Null
New-Item -ItemType Directory -Path "scripts\test" -Force | Out-Null

# 创建自动化提交脚本
$autoCommitScript = @"
# IM项目自动提交脚本

`$TIMESTAMP = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
`$BRANCH = git rev-parse --abbrev-ref HEAD
`$FILES_CHANGED = (git status --porcelain).Count

if (`$FILES_CHANGED -eq 0) {
    Write-Host "📭 没有需要提交的更改" -ForegroundColor Yellow
    exit 0
}

Write-Host "📊 IM项目自动提交" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan
Write-Host "时间: `$TIMESTAMP" -ForegroundColor White
Write-Host "分支: `$BRANCH" -ForegroundColor White
Write-Host "更改文件数: `$FILES_CHANGED" -ForegroundColor White

Write-Host ""
Write-Host "📋 更改摘要:" -ForegroundColor Cyan
git status --short

# 确认提交
`$confirm = Read-Host "是否提交这些更改? (y/N)"
if (`$confirm -notmatch "^[Yy]") {
    Write-Host "❌ 提交已取消" -ForegroundColor Red
    exit 0
}

# 添加所有更改
git add .

# 生成提交信息
if (`$BRANCH -like "feature/*") {
    `$FEATURE_ID = `$BRANCH -replace "feature/", ""
    `$COMMIT_MSG = "feat: 自动提交 - `$TIMESTAMP (#`$FEATURE_ID)"
} elseif (`$BRANCH -like "hotfix/*") {
    `$COMMIT_MSG = "fix: 热修复自动提交 - `$TIMESTAMP"
} elseif (`$BRANCH -like "release/*") {
    `$COMMIT_MSG = "chore: 发布准备 - `$TIMESTAMP"
} else {
    `$custom_msg = Read-Host "请输入提交信息"
    if ([string]::IsNullOrWhiteSpace(`$custom_msg)) {
        `$COMMIT_MSG = "chore: 自动提交 - `$TIMESTAMP"
    } else {
        `$COMMIT_MSG = `$custom_msg
    }
}

# 提交
git commit -m "`$COMMIT_MSG"

Write-Host ""
Write-Host "✅ 提交完成" -ForegroundColor Green
Write-Host "提交信息: `$COMMIT_MSG" -ForegroundColor White
`$COMMIT_HASH = git rev-parse --short HEAD
Write-Host "提交哈希: `$COMMIT_HASH" -ForegroundColor White
"@

Set-Content -Path "scripts\git\Auto-Commit.ps1" -Value $autoCommitScript -Encoding UTF8
Write-Host "✅ 自动提交脚本已创建: scripts\git\Auto-Commit.ps1" -ForegroundColor Green

# 7. 初始提交
Write-Host ""
Write-Host "🚀 执行初始提交" -ForegroundColor Cyan

git add .
git commit -m "chore: 初始化IM项目Git工作流

- 配置Git用户信息
- 创建基础分支结构
- 添加.gitignore文件
- 创建项目文档
- 添加实用脚本工具"

$commitHash = git rev-parse --short HEAD
Write-Host "✅ 初始提交完成" -ForegroundColor Green
Write-Host "提交哈希: $commitHash" -ForegroundColor White

# 8. 显示总结
Write-Host ""
Write-Host "🎉 IM项目Git工作流初始化完成" -ForegroundColor Green
Write-Host "==============================" -ForegroundColor Green
Write-Host "📋 已创建的内容:" -ForegroundColor Cyan
Write-Host "1. ✅ Git用户配置" -ForegroundColor Green
Write-Host "2. ✅ develop分支" -ForegroundColor Green
Write-Host "3. ✅ .gitignore文件" -ForegroundColor Green
Write-Host "4. ✅ README.md文档" -ForegroundColor Green
Write-Host "5. ✅ CHANGELOG.md文档" -ForegroundColor Green
Write-Host "6. ✅ 自动提交脚本" -ForegroundColor Green
Write-Host "7. ✅ 初始提交" -ForegroundColor Green

Write-Host ""
Write-Host "🚀 下一步:" -ForegroundColor Cyan
Write-Host "1. 添加远程仓库: git remote add origin <url>" -ForegroundColor White
Write-Host "2. 推送到远程: git push -u origin develop" -ForegroundColor White
Write-Host "3. 开始新功能开发: git checkout -b feature/功能名称" -ForegroundColor White

Write-Host ""
Write-Host "📚 详细工作流请参考: workflows\git\im-project-git-workflow.md" -ForegroundColor Yellow