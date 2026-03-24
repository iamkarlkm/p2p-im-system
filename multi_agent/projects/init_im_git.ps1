# 创建干净IM项目Git仓库脚本
Write-Host "=== IM项目Git仓库初始化 ==="
Write-Host "位置: C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
Write-Host "包含项目: im-backend, im-desktop, im-mobile"
Write-Host ""

# 1. 进入目录
Set-Location "C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
Write-Host "当前目录: $(Get-Location)"
Write-Host ""

# 2. 初始化Git仓库
Write-Host "初始化Git仓库..."
git init
Write-Host ""

# 3. 添加三个项目文件夹
Write-Host "添加项目文件..."
git add im-backend/
git add im-desktop/
git add im-mobile/
Write-Host ""

# 4. 创建.gitignore
Write-Host "创建.gitignore..."
@'
# IDE文件
.vscode/
.idea/
*.swp
*.swo

# 编译输出
target/
build/
bin/
obj/
*.class
*.jar
*.war
*.ear

# 依赖文件
node_modules/
.vendor/
vendor/

# 本地配置
*.local
.env
.env.local

# 日志文件
*.log
logs/

# 系统文件
.DS_Store
Thumbs.db

# 临时文件
tmp/
temp/
'@ | Out-File -FilePath .gitignore -Encoding UTF8
git add .gitignore
Write-Host ""

# 5. 创建README.md
Write-Host "创建README.md..."
@'
# IM即时通讯系统

## 项目概述
完整的即时通讯系统，包含后端、桌面端和移动端。

## 项目结构
```
im-java/
├── im-backend/           # Java后端项目 (Spring Boot)
├── im-desktop/          # 桌面端项目  
├── im-mobile/           # 移动端项目
├── README.md            # 项目说明
└── .gitignore           # Git忽略文件
```

## 技术栈
- **后端**: Java 17, Spring Boot, WebSocket, JWT
- **桌面端**: [技术栈]
- **移动端**: [技术栈]

## 快速开始
1. 后端启动: `cd im-backend && mvn spring-boot:run`
2. 构建: `mvn clean package`
3. 文档: 查看各项目目录下的README

## 功能特性
- 实时消息推送
- 好友关系管理
- 群组聊天
- 消息历史存储
- 多设备同步

## 开发状态
🚧 开发中...

## 许可证
[许可证信息]
'@ | Out-File -FilePath README.md -Encoding UTF8
git add README.md
Write-Host ""

# 6. 提交
Write-Host "提交初始版本..."
git commit -m "初始提交: IM即时通讯系统完整项目

- Java后端项目 (Spring Boot + WebSocket)
- 桌面端项目
- 移动端项目
- 项目文档和.gitignore配置"
Write-Host ""

# 7. 检查提交
Write-Host "提交信息:"
git log --oneline
Write-Host ""

Write-Host "✅ 本地Git仓库初始化完成!"
Write-Host "下一步: 设置远程仓库并推送"
Write-Host ""