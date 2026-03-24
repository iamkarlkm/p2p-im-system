# 🌳 IM即时通讯项目 - Git工作流规范

## 📅 **创建时间**
2026-03-24 22:18

## 🎯 **目标项目**
- **项目名称**: 即时通讯系统 (IM System)
- **项目路径**: `C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\`
- **子项目**:
  1. `im-backend/` - Java Spring Boot 后端
  2. `im-desktop/` - Tauri + TypeScript 桌面端
  3. `im-mobile/` - Flutter + Dart 移动端

## 📊 **项目状态**
- **已完成功能**: 132个
- **总代码行数**: 277,581行
- **主要技术栈**: Java (49.0%), Dart (15.8%), TypeScript (13.2%)

## 🔧 **基础Git配置**

### 1. 用户配置
```bash
# 全局用户配置
git config --global user.name "IM Development Team"
git config --global user.email "im-dev@yourcompany.com"

# 项目特定配置（推荐）
cd C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects
git config user.name "暗精灵"
git config user.email "your-email@example.com"

# 开启彩色输出
git config --global color.ui auto

# 设置默认编辑器
git config --global core.editor "code --wait"
```

### 2. 别名配置（`~/.gitconfig` 或项目级）
```ini
[alias]
    # 状态相关
    st = status
    sts = status --short
    
    # 提交相关
    ci = commit
    cia = commit --amend
    cim = commit -m
    
    # 分支相关
    br = branch
    brv = branch -v
    bra = branch -a
    
    # 日志相关
    lg = log --oneline --graph --all
    lga = log --oneline --graph --all --decorate
    hist = log --pretty=format:'%h %ad | %s%d [%an]' --graph --date=short
    
    # 差异相关
    diffc = diff --cached
    
    # 清理相关
    cleanf = clean -fd
    cleann = clean -n
    
    # IM项目特定
    im-status = "!f() { echo '=== IM项目状态 ==='; git status; echo '=== 最近提交 ==='; git log -3 --oneline; }; f"
    im-backup = "!f() { git add . && git commit -m '自动备份: $(date)' && echo '✅ 备份完成'; }; f"
```

## 🌿 **分支策略**

### 1. 分支命名规范
```
main                    # 主分支 - 生产就绪代码
develop                 # 开发分支 - 集成测试环境
feature/<feature-id>    # 功能分支 - 新功能开发
hotfix/<issue-id>       # 热修复分支 - 紧急修复
release/v<version>      # 发布分支 - 版本发布
```

### 2. 分支说明
- **`main`**: 只接受来自`release/`分支的合并，保持生产稳定
- **`develop`**: 日常开发集成，所有功能分支合并到此
- **`feature/`**: 基于`develop`创建，完成后合并回`develop`
- **`hotfix/`**: 基于`main`创建，修复后合并到`main`和`develop`
- **`release/`**: 基于`develop`创建，用于版本发布准备

## 🔄 **标准工作流程**

### 1. 新功能开发流程
```bash
# 1. 切换到开发分支
git checkout develop
git pull origin develop

# 2. 创建功能分支（使用功能ID或名称）
git checkout -b feature/195-message-queue

# 3. 开发代码
# ... 编写代码 ...

# 4. 提交更改（使用规范的提交信息）
git add .
git commit -m "feat: 实现消息队列基础架构 (#195)

- 添加Kafka集成配置
- 实现消息生产者服务
- 添加消费者组管理
- 支持消息持久化存储"

# 5. 推送分支
git push -u origin feature/195-message-queue

# 6. 创建Pull Request（在GitHub/GitLab上）
# 7. 代码审查通过后合并到develop
```

### 2. 热修复流程
```bash
# 1. 从main创建热修复分支
git checkout main
git pull origin main
git checkout -b hotfix/urgent-fix-001

# 2. 修复问题并提交
git add .
git commit -m "fix: 修复消息发送超时问题 (#紧急修复)

- 调整WebSocket超时配置
- 增加连接重试机制
- 优化心跳包频率"

# 3. 测试通过后合并
git checkout main
git merge --no-ff hotfix/urgent-fix-001
git tag -a v1.0.1-hotfix -m "紧急修复版本 v1.0.1"

# 4. 同步到develop
git checkout develop
git merge main

# 5. 推送
git push origin main --tags
git push origin develop
git branch -d hotfix/urgent-fix-001
```

### 3. 版本发布流程
```bash
# 1. 从develop创建发布分支
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0

# 2. 版本准备
# - 更新版本号
# - 更新CHANGELOG.md
# - 运行完整测试套件

# 3. 提交版本准备
git add .
git commit -m "chore: 准备发布 v1.2.0

- 更新版本号到1.2.0
- 生成CHANGELOG
- 更新依赖版本"

# 4. 合并到main
git checkout main
git merge --no-ff release/v1.2.0
git tag -a v1.2.0 -m "正式发布 v1.2.0"

# 5. 同步回develop
git checkout develop
git merge main

# 6. 推送
git push origin main --tags
git push origin develop
git branch -d release/v1.2.0
```

## 📝 **提交信息规范**

### 1. 提交格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 2. 类型 (Type)
- **feat**: 新功能
- **fix**: 修复bug
- **docs**: 文档更新
- **style**: 代码格式调整（不影响功能）
- **refactor**: 重构代码
- **test**: 测试相关
- **chore**: 构建过程或辅助工具的变动
- **perf**: 性能优化
- **ci**: CI配置相关

### 3. IM项目特定示例
```bash
# 功能开发
git commit -m "feat(backend): 实现端到端加密消息传输 (#123)

- 集成Signal Protocol加密库
- 添加密钥交换机制
- 实现消息加密/解密服务
- 添加单元测试覆盖"

# Bug修复
git commit -m "fix(desktop): 修复消息列表滚动问题 (#156)

- 修正虚拟滚动计算错误
- 优化滚动性能
- 添加边界情况处理"

# 重构
git commit -m "refactor(mobile): 重构消息缓存层 (#189)

- 提取缓存接口抽象
- 实现多级缓存策略
- 优化内存使用效率"

# 文档
git commit -m "docs(api): 更新REST API文档 (#201)

- 添加新端点文档
- 更新请求/响应示例
- 补充错误代码说明"
```

## 🗂️ **仓库结构建议**

### 1. 单仓库 vs 多仓库
**推荐: 单仓库 (Monorepo)**
```
im-system/
├── .git/
├── backend/          # Java后端
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── desktop/          # TypeScript桌面端
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── mobile/           # Dart移动端
│   ├── lib/
│   ├── pubspec.yaml
│   └── README.md
├── docs/             # 项目文档
├── scripts/          # 构建和部署脚本
├── .gitignore        # Git忽略文件
├── README.md         # 项目总览
└── CHANGELOG.md      # 变更日志
```

### 2. Git忽略配置 (`.gitignore`)
```gitignore
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
```

## 🤖 **自动化脚本**

### 1. 自动化提交脚本 (`scripts/git/auto-commit.sh`)
```bash
#!/bin/bash
# 自动提交IM项目代码

TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S")
BRANCH=$(git rev-parse --abbrev-ref HEAD)
FILES_CHANGED=$(git status --porcelain | wc -l)

if [ $FILES_CHANGED -eq 0 ]; then
    echo "📭 没有需要提交的更改"
    exit 0
fi

echo "📊 检测到 $FILES_CHANGED 个文件更改"
echo "🌿 当前分支: $BRANCH"

# 添加所有更改
git add .

# 生成提交信息
if [[ $BRANCH == feature/* ]]; then
    FEATURE_ID=$(echo $BRANCH | sed 's/feature\///')
    COMMIT_MSG="feat: 自动提交 - $TIMESTAMP (#$FEATURE_ID)"
elif [[ $BRANCH == hotfix/* ]]; then
    COMMIT_MSG="fix: 热修复自动提交 - $TIMESTAMP"
else
    COMMIT_MSG="chore: 自动提交 - $TIMESTAMP"
fi

# 提交
git commit -m "$COMMIT_MSG"

echo "✅ 已提交: $COMMIT_MSG"
```

### 2. 代码统计脚本 (`scripts/git/code-stats.sh`)
```bash
#!/bin/bash
# 生成IM项目代码统计报告

echo "📊 IM项目代码统计报告"
echo "生成时间: $(date)"
echo "=========================="

# 后端统计
echo "🔧 后端 (Java):"
cd backend
find . -name "*.java" | wc -l | xargs echo "  文件数:"
find . -name "*.java" -exec cat {} \; | wc -l | xargs echo "  代码行数:"
cd ..

# 桌面端统计
echo "💻 桌面端 (TypeScript):"
cd desktop
find . -name "*.ts" -o -name "*.tsx" | wc -l | xargs echo "  文件数:"
find . -name "*.ts" -o -name "*.tsx" -exec cat {} \; | wc -l | xargs echo "  代码行数:"
cd ..

# 移动端统计
echo "📱 移动端 (Dart):"
cd mobile
find . -name "*.dart" | wc -l | xargs echo "  文件数:"
find . -name "*.dart" -exec cat {} \; | wc -l | xargs echo "  代码行数:"
cd ..

echo "=========================="
echo "📈 总计:"
find . \( -name "*.java" -o -name "*.ts" -o -name "*.tsx" -o -name "*.dart" \) | wc -l | xargs echo "总文件数:"
find . \( -name "*.java" -o -name "*.ts" -o -name "*.tsx" -o -name "*.dart" \) -exec cat {} \; | wc -l | xargs echo "总代码行数:"
```

## 🔍 **代码审查流程**

### 1. Pull Request模板 (`.github/PULL_REQUEST_TEMPLATE.md`)
```markdown
## 🎯 变更说明
<!-- 描述这个PR的主要目的 -->

## 🔧 修改内容
<!-- 详细描述修改的文件和功能 -->

- [ ] 后端修改
- [ ] 桌面端修改  
- [ ] 移动端修改
- [ ] 文档更新
- [ ] 配置变更

## 📋 测试清单
<!-- PR应该通过的测试 -->

- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] UI测试通过
- [ ] 性能测试通过
- [ ] 安全测试通过

## 📸 屏幕截图
<!-- 如果是UI变更，请提供截图 -->

## 🔗 相关Issue
<!-- 关联的Issue编号 -->
Closes #<issue-number>

## ✅ 审查要点
- [ ] 代码符合项目规范
- [ ] 有适当的测试覆盖
- [ ] 文档已更新
- [ ] 没有引入安全问题
- [ ] 性能影响已评估
```

### 2. 代码审查清单
```bash
# 审查前准备
git fetch origin
git checkout feature/branch-name
git rebase origin/develop

# 运行代码检查
./scripts/lint-check.sh
./scripts/test-runner.sh

# 审查代码
git log --oneline -10  # 查看最近提交
git diff origin/develop --stat  # 查看变更统计
git diff origin/develop --name-only  # 查看变更文件
```

## 🚀 **CI/CD集成**

### 1. GitHub Actions 配置 (`.github/workflows/im-ci.yml`)
```yaml
name: IM项目CI

on:
  push:
    branches: [ develop, feature/*, hotfix/* ]
  pull_request:
    branches: [ develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: 设置Java
      uses: actions/setup-java@v2
      with:
        distribution: 'temurin'
        java-version: '17'
    
    - name: 设置Node.js
      uses: actions/setup-node@v2
      with:
        node-version: '18'
    
    - name: 后端测试
      run: |
        cd backend
        ./mvnw test
    
    - name: 桌面端测试
      run: |
        cd desktop
        npm ci
        npm test
    
    - name: 移动端测试
      run: |
        cd mobile
        flutter test
    
  build:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: 构建所有组件
      run: |
        ./scripts/build-all.sh
    
    - name: 上传构建产物
      uses: actions/upload-artifact@v2
      with:
        name: im-build-artifacts
        path: |
          backend/target/*.jar
          desktop/dist/
          mobile/build/
```

## 📚 **文档要求**

### 1. 必须包含的文档
- `README.md` - 项目总览
- `CHANGELOG.md` - 变更日志
- `CONTRIBUTING.md` - 贡献指南
- `SECURITY.md` - 安全策略
- `API_DOCS.md` - API文档

### 2. 提交关联文档
```bash
# 每次功能提交都应更新文档
git add docs/API_DOCS.md
git commit -m "docs: 更新API文档 (#feature-id)"
```

## ⚠️ **禁止事项**

### 1. 绝对禁止
- ❌ 直接推送到`main`分支
- ❌ 使用`git push --force`（除非使用`--force-with-lease`）
- ❌ 提交敏感信息（密码、密钥、令牌）
- ❌ 提交二进制文件（除非必要）
- ❌ 提交自动生成的大文件

### 2. 需要审批
- ✅ 合并到`main`分支
- ✅ 删除已发布的分支
- ✅ 重写公共分支的历史
- ✅ 修改`.gitignore`规则

## 🆘 **常见问题解决**

### 1. 撤销错误的提交
```bash
# 撤销上次提交，保留更改
git reset --soft HEAD~1

# 撤销上次提交，丢弃更改
git reset --hard HEAD~1

# 撤销特定提交（创建新提交）
git revert <commit-hash>
```

### 2. 恢复删除的分支
```bash
# 查看操作历史
git reflog

# 恢复分支
git checkout -b feature/restored <commit-hash>
```

### 3. 解决合并冲突
```bash
# 查看冲突文件
git status

# 手动解决冲突后
git add resolved-file.txt
git commit -m "解决合并冲突"
# 或
git merge --continue
```

## 📈 **工作流优势**

### 1. 对IM项目的价值
- **一致性**: 多端开发保持统一的版本控制
- **可追溯**: 每个功能都有完整的开发历史
- **自动化**: 减少手动操作，提高效率
- **协作性**: 清晰的团队协作流程
- **质量保障**: 通过审查和测试保证代码质量

### 2. 与现有工作流集成
- ✅ 兼容现有的开发代理系统
- ✅ 与Python统计脚本协同工作
- ✅ 支持自动化代码生成
- ✅ 集成到监控和报告系统

## 🔄 **持续改进**

### 1. 定期审查
- 每月审查Git工作流效果
- 根据团队反馈优化流程
- 更新工具和脚本

### 2. 培训和文档
- 新成员Git工作流培训
- 定期更新最佳实践
- 分享成功案例和教训

---

**最后更新**: 2026-03-24 22:20  
**适用版本**: IM项目 v1.0+  
**负责人**: 开发团队  
**参考**: Git Essentials技能包 + 行业最佳实践