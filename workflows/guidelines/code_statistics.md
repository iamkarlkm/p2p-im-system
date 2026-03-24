# 📊 代码统计工作流程指南

## 🎯 **概述**
本指南描述了如何使用Python脚本进行代码统计和分析，避免使用PowerShell等易出错工具。

## 🔧 **可用脚本**

### 1. `workspace_analysis.py` - 工作空间完整分析
**用途**: 分析整个工作空间的目录结构、文件分布和代码统计

**使用方法**:
```bash
# 分析当前目录
python workflows\scripts\statistics\workspace_analysis.py

# 分析指定目录
python workflows\scripts\statistics\workspace_analysis.py [目录路径]
```

**输出文件**:
- `workspace_analysis.json` - 结构化分析数据
- `workspace_analysis.txt` - 简洁文本报告

**典型输出摘要**:
```
📊 代码统计摘要
总目录数: 155个
总文件数: 1,321个
代码文件数: 1,288个 (97.5%)
总代码行数: 256,195行
```

### 2. `project_code_stat.py` - 项目代码统计
**用途**: 专门统计projects目录下的代码，按项目分组分析

**使用方法**:
```bash
# 统计projects目录
python workflows\scripts\statistics\project_code_stat.py
```

**输出文件**:
- `project_statistics.json` - 项目详细统计
- `project_statistics.txt` - 项目简洁报告

### 3. `code_stat.py` - 通用代码统计
**用途**: 统计任意目录的代码文件

**使用方法**:
```bash
# 统计当前目录
python workflows\scripts\statistics\code_stat.py .

# 统计指定目录
python workflows\scripts\statistics\code_stat.py [目录路径]
```

**输出文件**:
- `code_statistics.json` - 通用统计结果
- 控制台输出格式化报告

## 📋 **执行流程**

### 步骤1：确定统计范围
```python
# 根据需求选择脚本：
# - 完整工作空间分析 → workspace_analysis.py
# - 项目特定分析 → project_code_stat.py
# - 任意目录统计 → code_stat.py
```

### 步骤2：执行统计脚本
```bash
# 使用对应脚本
python workflows\scripts\statistics\[脚本名称].py [参数]
```

### 步骤3：分析结果
```python
# 查看JSON格式完整数据
# 查看文本摘要
# 提取关键指标
```

### 步骤4：分享结果
```text
# 按照文件分享规则，使用文本摘要：
📊 代码统计结果
总代码行数: 256,195行
主要技术栈: Java (51.5%), Dart (16.5%), TypeScript (14.1%)
```

## 🚫 **禁止的做法**

### ❌ 使用PowerShell统计
```powershell
# 编码问题，已禁用
dir *.java /s
Get-ChildItem -Path . -Name | findstr .java
```

### ❌ 手动计数
```text
# 易错且效率低
手动查看每个文件
使用记事本打开统计
```

### ❌ 发送完整文件
```text
# 不遵守文件分享规则
<qqfile>workspace_analysis.txt</qqfile>
```

## ✅ **推荐的做法**

### 1. **定期统计项目进度**
```bash
# 每周运行一次完整分析
python workflows\scripts\statistics\workspace_analysis.py
```

### 2. **分析特定项目变化**
```bash
# 查看项目代码增长
python workflows\scripts\statistics\project_code_stat.py
```

### 3. **生成报告摘要**
```bash
# 自动生成文本摘要
python workflows\scripts\statistics\workspace_analysis.py
# 然后查看summary.txt文件
```

## 📊 **关键指标解释**

### 1. **代码行数 (Lines of Code)**
- **总代码行数**: 所有代码文件的行数总和
- **代码文件占比**: 代码文件数 / 总文件数
- **技术栈分布**: 各编程语言的代码行数占比

### 2. **项目规模指标**
- **文件数**: 代码文件数量
- **目录数**: 项目结构复杂度
- **平均文件大小**: 总代码行数 / 代码文件数

### 3. **技术栈分析**
- **主要语言**: 占比超过5%的编程语言
- **辅助语言**: 占比1-5%的语言
- **文档文件**: Markdown、文档等非代码文件

## 🔍 **常见使用场景**

### 场景1：项目进度汇报
```bash
# 运行完整分析
python workflows\scripts\statistics\workspace_analysis.py

# 输出摘要
📊 本周项目进度
代码行数: 256,195 → 265,430 (+9,235行)
Java占比: 51.5% → 52.1% (+0.6%)
新文件: 15个
```

### 场景2：技术栈评估
```bash
# 分析技术栈分布
python workflows\scripts\statistics\project_code_stat.py

# 输出摘要
💻 技术栈现状
Java: 132,059行 (51.5%)
Dart: 42,180行 (16.5%)
TypeScript: 36,088行 (14.1%)
```

### 场景3：代码质量检查
```bash
# 查看代码密度
python workflows\scripts\statistics\code_stat.py .

# 输出摘要
📈 代码质量指标
代码文件占比: 97.5%
平均文件大小: 198.9行
最大文件: 1,077行 (建议拆分)
```

## 🛠️ **脚本维护**

### 1. **添加新功能**
```python
# 优先扩展现有脚本
# 遵循脚本模板结构
# 更新本指南文档
```

### 2. **处理错误**
```python
# 检查Python环境
# 验证文件权限
# 查看错误日志
```

### 3. **性能优化**
```python
# 对于大型项目，考虑分块处理
# 添加进度指示
# 优化文件遍历逻辑
```

## 📝 **记录和记忆**

### 1. **成功的工作流程**
每次成功使用脚本后，记录：
- 使用的脚本和参数
- 统计的范围和目的
- 关键发现和结论
- 用户反馈和改进建议

### 2. **更新的规则**
基于经验更新：
- `workflows/rules/python_priority.md`
- `workflows/rules/file_sharing.md`

### 3. **用户的偏好**
记录到：
- `USER.md` - 用户技术偏好
- `TOOLS.md` - 工具使用习惯

## 🔄 **更新历史**
- **2026-03-24**: 基于用户建议创建此指南
- **背景**: 从PowerShell迁移到Python统计脚本
- **目标**: 标准化代码统计工作流程
- **状态**: 已完全实施并验证

---
**创建时间**: 2026-03-24  
**最后更新**: 2026-03-24  
**维护状态**: 活跃  
**参考规则**: `python_priority.md`, `file_sharing.md`