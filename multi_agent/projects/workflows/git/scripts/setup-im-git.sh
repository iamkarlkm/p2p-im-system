#!/bin/bash
# IM项目Git工作流初始化脚本

echo "🌳 IM项目Git工作流初始化"
echo "=========================="

# 检查是否在项目目录中
if [ ! -d "backend" ] && [ ! -d "desktop" ] && [ ! -d "mobile" ]; then
    echo "❌ 错误: 请在IM项目根目录运行此脚本"
    exit 1
fi

echo "📁 项目目录: $(pwd)"

# 1. 检查Git是否安装
if ! command -v git &> /dev/null; then
    echo "❌ Git未安装，请先安装Git"
    exit 1
fi
echo "✅ Git已安装: $(git --version)"

# 2. 配置用户信息
echo ""
echo "📝 配置Git用户信息"
read -p "请输入用户名 [默认: IM开发团队]: " username
username=${username:-"IM开发团队"}
read -p "请输入邮箱 [默认: im-dev@example.com]: " email
email=${email:-"im-dev@example.com"}

git config user.name "$username"
git config user.email "$email"

echo "✅ 用户信息已配置: $username <$email>"

# 3. 创建基础分支结构
echo ""
echo "🌿 初始化分支结构"

# 检查是否已初始化Git仓库
if [ ! -d ".git" ]; then
    echo "📦 初始化Git仓库"
    git init
fi

# 创建开发分支（如果不存在）
if ! git show-ref --verify --quiet refs/heads/develop; then
    git checkout -b develop
    echo "✅ 创建develop分支"
else
    git checkout develop
    echo "✅ 切换到develop分支"
fi

# 4. 创建.gitignore文件
echo ""
echo "📄 创建.gitignore文件"

cat > .gitignore << 'EOF'
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
EOF

echo "✅ .gitignore文件已创建"

# 5. 创建基础文档
echo ""
echo "📚 创建基础文档"

# README.md
if [ ! -f "README.md" ]; then
    cat > README.md << 'EOF'
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
EOF
    echo "✅ README.md已创建"
fi

# CHANGELOG.md
if [ ! -f "CHANGELOG.md" ]; then
    cat > CHANGELOG.md << 'EOF'
# 变更日志

本项目遵循[语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-03-24
### 新增
- 项目初始化
- 基础Git工作流配置
- 多端架构搭建
EOF
    echo "✅ CHANGELOG.md已创建"
fi

# 6. 创建脚本目录
echo ""
echo "🛠️ 创建工具脚本"

mkdir -p scripts/git
mkdir -p scripts/build
mkdir -p scripts/test

# 创建自动化提交脚本
cat > scripts/git/auto-commit.sh << 'EOF'
#!/bin/bash
# IM项目自动提交脚本

TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S")
BRANCH=$(git rev-parse --abbrev-ref HEAD)
FILES_CHANGED=$(git status --porcelain | wc -l)

if [ $FILES_CHANGED -eq 0 ]; then
    echo "📭 没有需要提交的更改"
    exit 0
fi

echo "📊 IM项目自动提交"
echo "=================="
echo "时间: $TIMESTAMP"
echo "分支: $BRANCH"
echo "更改文件数: $FILES_CHANGED"

# 显示更改摘要
echo ""
echo "📋 更改摘要:"
git status --short

# 确认提交
read -p "是否提交这些更改? (y/N): " confirm
if [[ ! $confirm =~ ^[Yy]$ ]]; then
    echo "❌ 提交已取消"
    exit 0
fi

# 添加所有更改
git add .

# 生成提交信息
if [[ $BRANCH == feature/* ]]; then
    FEATURE_ID=$(echo $BRANCH | sed 's/feature\///')
    COMMIT_MSG="feat: 自动提交 - $TIMESTAMP (#$FEATURE_ID)"
elif [[ $BRANCH == hotfix/* ]]; then
    COMMIT_MSG="fix: 热修复自动提交 - $TIMESTAMP"
elif [[ $BRANCH == release/* ]]; then
    COMMIT_MSG="chore: 发布准备 - $TIMESTAMP"
else
    read -p "请输入提交信息: " custom_msg
    if [ -z "$custom_msg" ]; then
        COMMIT_MSG="chore: 自动提交 - $TIMESTAMP"
    else
        COMMIT_MSG="$custom_msg"
    fi
fi

# 提交
git commit -m "$COMMIT_MSG"

echo ""
echo "✅ 提交完成"
echo "提交信息: $COMMIT_MSG"
echo "提交哈希: $(git rev-parse --short HEAD)"
EOF

chmod +x scripts/git/auto-commit.sh
echo "✅ 自动提交脚本已创建: scripts/git/auto-commit.sh"

# 创建代码统计脚本
cat > scripts/git/code-stats.sh << 'EOF'
#!/bin/bash
# IM项目代码统计脚本

echo "📊 IM项目代码统计报告"
echo "========================"
echo "生成时间: $(date)"
echo "项目目录: $(pwd)"
echo ""

# 统计函数
count_files_and_lines() {
    local dir=$1
    local pattern=$2
    local name=$3
    
    if [ -d "$dir" ]; then
        local files=$(find "$dir" -name "$pattern" | wc -l)
        local lines=$(find "$dir" -name "$pattern" -exec cat {} \; | wc -l)
        printf "%-20s %8d 文件 %12d 行\n" "$name:" $files $lines
    else
        printf "%-20s %8s 目录不存在\n" "$name:" ""
    fi
}

# 后端统计
count_files_and_lines "backend" "*.java" "后端 (Java)"

# 桌面端统计
count_files_and_lines "desktop" "*.ts" "桌面端 (TS)"
count_files_and_lines "desktop" "*.tsx" "桌面端 (TSX)"

# 移动端统计
count_files_and_lines "mobile" "*.dart" "移动端 (Dart)"

echo ""
echo "📈 总计:"

# 计算总计
total_files=0
total_lines=0

for dir in backend desktop mobile; do
    if [ -d "$dir" ]; then
        case $dir in
            backend)
                pattern="*.java"
                ;;
            desktop)
                pattern="*.ts"
                ;;
            mobile)
                pattern="*.dart"
                ;;
        esac
        files=$(find "$dir" -name "$pattern" | wc -l)
        lines=$(find "$dir" -name "$pattern" -exec cat {} \; | wc -l)
        total_files=$((total_files + files))
        total_lines=$((total_lines + lines))
    fi
done

printf "%-20s %8d 文件 %12d 行\n" "总代码量:" $total_files $total_lines

# Git统计
echo ""
echo "🌿 Git统计:"
echo "提交总数: $(git rev-list --count HEAD)"
echo "分支数量: $(git branch | wc -l)"
echo "最后提交: $(git log -1 --pretty=format:"%h - %s [%an]")"
EOF

chmod +x scripts/git/code-stats.sh
echo "✅ 代码统计脚本已创建: scripts/git/code-stats.sh"

# 7. 初始提交
echo ""
echo "🚀 执行初始提交"

git add .
git commit -m "chore: 初始化IM项目Git工作流

- 配置Git用户信息
- 创建基础分支结构
- 添加.gitignore文件
- 创建项目文档
- 添加实用脚本工具"

echo "✅ 初始提交完成"
echo "提交哈希: $(git rev-parse --short HEAD)"

# 8. 显示总结
echo ""
echo "🎉 IM项目Git工作流初始化完成"
echo "=============================="
echo "📋 已创建的内容:"
echo "1. ✅ Git用户配置"
echo "2. ✅ develop分支"
echo "3. ✅ .gitignore文件"
echo "4. ✅ README.md文档"
echo "5. ✅ CHANGELOG.md文档"
echo "6. ✅ 自动提交脚本"
echo "7. ✅ 代码统计脚本"
echo "8. ✅ 初始提交"
echo ""
echo "🚀 下一步:"
echo "1. 添加远程仓库: git remote add origin <url>"
echo "2. 推送到远程: git push -u origin develop"
echo "3. 开始新功能开发: git checkout -b feature/功能名称"
echo ""
echo "📚 详细工作流请参考: workflows/git/im-project-git-workflow.md"