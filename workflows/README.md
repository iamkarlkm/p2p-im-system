# 📋 工作流操作指南总纲

## 🎯 **设计理念**
1. **Python优先** - 复杂操作优先用Python脚本实现
2. **目录分层** - 清晰的结构便于查找和重用
3. **记忆固化** - 成功的工作流程记录到记忆系统
4. **规则明确** - 避免重复决策，提高效率

## 📁 **目录结构**
```
workflows/
├── README.md                # 本文件 - 总纲
├── scripts/                 # Python脚本库
├── guidelines/              # 操作指南文档
├── rules/                   # 执行规则和约束
└── examples/               # 示例和模板
```

## 🔧 **脚本目录 (scripts/)**
存放可重用的Python脚本，按功能分类：
- `statistics/` - 统计类脚本
- `file_ops/` - 文件操作脚本
- `data_processing/` - 数据处理脚本
- `system_utils/` - 系统工具脚本

## 📘 **指南目录 (guidelines/)**
存放操作指南文档，按场景分类：
- `code_statistics.md` - 代码统计工作流程
- `file_management.md` - 文件管理指南
- `project_analysis.md` - 项目分析指南
- `system_operations.md` - 系统操作指南

## 📜 **规则目录 (rules/)**
存放执行规则和约束文档：
- `python_priority.md` - Python优先规则
- `file_sharing.md` - 文件分享规则
- `security_constraints.md` - 安全约束
- `execution_standards.md` - 执行标准

## 🎨 **示例目录 (examples/)**
存放示例文件和模板：
- `script_template.py` - Python脚本模板
- `guideline_template.md` - 指南文档模板
- `rule_template.md` - 规则文档模板

## 🔄 **工作流程**
1. **识别需求** → 确定需要什么操作
2. **查找指南** → 检查是否有现成指南
3. **执行脚本** → 使用或创建Python脚本
4. **记录结果** → 将成功流程记入记忆
5. **更新指南** → 完善或创建新的指南

## 📊 **当前已实现的改进**

### ✅ **2026-03-24 实现的功能**
1. **代码统计系统**
   - `scripts/statistics/code_stat.py`
   - `scripts/statistics/project_code_stat.py`
   - `scripts/statistics/workspace_analysis.py`

2. **执行规则**
   - `rules/python_priority.md` - Python优先原则
   - `rules/file_sharing.md` - 文件分享约束

3. **操作指南**
   - `guidelines/code_statistics.md` - 代码统计工作流程

## 🚀 **使用方式**

### 1. 查找现有解决方案
```bash
# 查看所有可用脚本
dir workflows\scripts

# 查看操作指南
dir workflows\guidelines
```

### 2. 执行Python脚本
```bash
# 使用现有脚本
python workflows\scripts\statistics\code_stat.py [目录]

# 创建新脚本（按模板）
copy workflows\examples\script_template.py new_script.py
```

### 3. 查阅操作指南
```bash
# 查看特定指南
type workflows\guidelines\code_statistics.md
```

### 4. 遵守执行规则
```bash
# 查看规则
type workflows\rules\python_priority.md
```

## 📝 **扩展原则**
1. **DRY原则** - 不要重复实现，优先复用
2. **KISS原则** - 保持简单直接
3. **模块化** - 一个脚本一个明确功能
4. **文档化** - 每个脚本都有清晰指南

## 🔍 **快速查找表**

| 需求类型 | 查找位置 | 示例 |
|---------|---------|------|
| 统计代码 | `scripts/statistics/` | `code_stat.py` |
| 文件操作 | `scripts/file_ops/` | 待创建 |
| 数据处理 | `scripts/data_processing/` | 待创建 |
| 操作步骤 | `guidelines/` | `code_statistics.md` |
| 执行限制 | `rules/` | `python_priority.md` |

## 🎯 **承诺**
- 所有复杂操作优先使用Python脚本
- 新的工作流程都会被记录和规范化
- 这个目录体系会持续完善和扩展

---
**创建时间**：2026-03-24  
**最后更新**：2026-03-24  
**维护者**：系统自动化工作流