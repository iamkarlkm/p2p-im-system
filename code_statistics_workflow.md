# 🔧 代码统计工作流程参考卡片

## 📌 **核心原则**
**Python优先，PowerShell禁止**

## 🚀 **快速开始**

### 1. 基本统计
```bash
# 分析整个工作空间
python workspace_analysis.py

# 统计项目目录
python project_code_stat.py

# 统计任意目录
python code_stat.py [目录路径]
```

### 2. 查看结果
```bash
# 查看JSON格式结果
type workspace_analysis.json

# 查看文本报告
type workspace_analysis.txt
```

## 📊 **脚本功能对比**

| 脚本名称 | 主要用途 | 输出文件 | 最佳场景 |
|---------|---------|---------|---------|
| `workspace_analysis.py` | 完整工作空间分析 | `.json`, `.txt` | 全面了解工作空间状态 |
| `project_code_stat.py` | 项目代码统计 | `.json`, `.txt` | 分析特定项目进度 |
| `code_stat.py` | 通用目录统计 | `.json`, `.txt` | 快速统计任意目录 |

## ❌ **禁止使用的命令**
```powershell
# 以下命令因编码问题已禁用
dir /B
Get-ChildItem -Path . -Name | findstr .java
# 任何依赖PowerShell的代码统计方案
```

## ✅ **Python脚本优势**
1. **编码完美** - 自动UTF-8，中文无乱码
2. **跨平台** - Windows/Linux/macOS通用
3. **功能强大** - 支持复杂统计逻辑
4. **稳定可靠** - 完善的错误处理

## 🔄 **更新历史**
- **2026-03-24**：基于用户建议，从PowerShell迁移到Python
- **用户原话**："统计代码的工作，你完全可以写成Python脚本呀，用Python来完成啊"

## 🎯 **承诺**
我承诺：
1. 永远优先使用Python进行代码统计
2. 避免使用PowerShell处理中文和复杂统计
3. 将此工作流程固化到长期记忆中

## 📁 **相关文件**
- `TOOLS.md` - 详细工作流程说明
- `USER.md` - 用户偏好记录
- `memory/2026-03-24.md` - 本次改进的详细记录

---
**创建时间**：2026-03-24  
**最后更新**：2026-03-24  
**永久有效**：✅ 是