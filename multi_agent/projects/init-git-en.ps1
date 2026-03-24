# IM Project Git Environment Initialization (English)

Write-Host "=== IM Project Git Initialization ==="

# Check if in project directory
$hasBackend = Test-Path "im-backend"
$hasDesktop = Test-Path "im-desktop" 
$hasMobile = Test-Path "im-mobile"

if (-not ($hasBackend -or $hasDesktop -or $hasMobile)) {
    Write-Host "Error: Please run this script in IM project directory"
    exit 1
}

Write-Host "Project directory: $(Get-Location)"

# 1. Check Git
try {
    git --version | Out-Null
    Write-Host "✅ Git is installed"
} catch {
    Write-Host "❌ Git is not installed"
    exit 1
}

# 2. Configure user
Write-Host ""
Write-Host "Configuring Git user info..."
git config user.name "IM Developer"
git config user.email "im-dev@example.com"
Write-Host "✅ User info configured"

# 3. Initialize Git repository
if (-not (Test-Path ".git")) {
    Write-Host "Initializing Git repository..."
    git init
    Write-Host "✅ Git repository initialized"
}

# 4. Create develop branch
Write-Host "Creating develop branch..."
git checkout -b develop 2>$null
Write-Host "✅ develop branch created/switched"

# 5. Create basic files
Write-Host "Creating basic files..."

# .gitignore
$gitignore = @"
# Build artifacts
build/
dist/
target/
*.jar
*.war
*.ear

# Dependencies
node_modules/
vendor/
.gradle/

# Environment
.env
.env.local

# Editors
.vscode/
.idea/

# Logs
logs/
*.log

# System
.DS_Store
Thumbs.db

# IM Project specific
*.keystore
*.p12
*.cert
*.key

# Test reports
coverage/
test-results/
"@

Set-Content -Path ".gitignore" -Value $gitignore -Encoding ASCII
Write-Host "✅ .gitignore created"

# README.md
$readme = @"
# IM Instant Messaging System

## Project Overview
Multi-platform instant messaging system.

## Tech Stack
- Backend: Java Spring Boot
- Desktop: Tauri + TypeScript  
- Mobile: Flutter + Dart

## Development Guidelines
See workflows/git/im-project-git-workflow.md

## License
Copyright (c) 2026 IM Development Team
"@

Set-Content -Path "README.md" -Value $readme -Encoding ASCII
Write-Host "✅ README.md created"

# 6. Initial commit
Write-Host ""
Write-Host "Performing initial commit..."

git add .
$commitMsg = @"
chore: Initialize IM project Git environment

- Configure Git user info
- Create basic branch structure
- Add .gitignore file
- Create project documentation
"@

git commit -m $commitMsg

$commitHash = git rev-parse --short HEAD
Write-Host "✅ Initial commit completed"
Write-Host "Commit hash: $commitHash"

# 7. Show summary
Write-Host ""
Write-Host "=== Initialization Complete ==="
Write-Host "✅ Git user configuration"
Write-Host "✅ develop branch"
Write-Host "✅ .gitignore file"
Write-Host "✅ README.md documentation"
Write-Host "✅ Initial commit"

Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Add remote repository: git remote add origin <url>"
Write-Host "2. Push to remote: git push -u origin develop"
Write-Host "3. Start development: git checkout -b feature/feature-name"