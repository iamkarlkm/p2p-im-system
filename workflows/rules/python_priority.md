# 📜 Python优先执行规则

## 🎯 **核心原则**
**所有复杂操作必须优先使用Python脚本，避免使用PowerShell或其他易出错的命令行工具**

## ⚠️ **禁止使用的命令**
以下PowerShell命令因编码和兼容性问题已禁用：

### ❌ 代码统计相关
```powershell
# 编码问题导致中文乱码
dir /B
Get-ChildItem -Path . -Name
dir *.java /s

# 错误处理困难
findstr .java
```

### ❌ 文件操作相关
```powershell
# 复杂且易错
Get-Content -Encoding UTF8
Out-File -Encoding UTF8
```

### ❌ 数据处理相关
```powershell
# 功能有限且不稳定
Select-String -Pattern
Measure-Object -Sum
```

## ✅ **必须使用的Python脚本**

### 1. **代码统计脚本**
```bash
# 完整工作空间分析
python workflows\scripts\statistics\workspace_analysis.py

# 项目代码统计
python workflows\scripts\statistics\project_code_stat.py

# 通用目录统计
python workflows\scripts\statistics\code_stat.py [目录路径]
```

### 2. **文件操作脚本**
```bash
# 文件查找和过滤
python workflows\scripts\file_ops\file_finder.py

# 文件内容分析
python workflows\scripts\file_ops\content_analyzer.py
```

### 3. **数据处理脚本**
```bash
# 数据清洗和转换
python workflows\scripts\data_processing\data_cleaner.py

# 统计和分析
python workflows\scripts\data_processing\data_analyzer.py
```

## 🎯 **执行流程**

### 步骤1：需求分析
```python
# 确定需要完成什么操作
# 如果是统计、处理、分析等复杂任务 → 选择Python
```

### 步骤2：脚本选择
```python
# 1. 检查是否有现成脚本
# 2. 如果没有，创建新的Python脚本
# 3. 绝对不要使用PowerShell替代
```

### 步骤3：脚本执行
```python
# 使用明确的参数调用Python脚本
# 确保错误处理完善
# 输出结果应该是结构化的
```

### 步骤4：结果记录
```python
# 将成功的工作流程记录到指南中
# 更新相应的脚本和文档
```

## 📊 **优势对比**

| 方面 | Python ✅ | PowerShell ❌ |
|------|----------|--------------|
| 编码处理 | 自动UTF-8，完美支持中文 | 编码问题频繁，中文乱码 |
| 跨平台 | Windows/Linux/macOS通用 | 主要限于Windows |
| 错误处理 | try-except机制可靠 | 错误处理复杂，容易中断 |
| 功能扩展 | 丰富的库支持复杂操作 | 功能有限，依赖外部命令 |
| 稳定性 | 脚本运行稳定可靠 | 依赖系统环境，容易出错 |
| 可维护性 | 代码结构清晰，易于修改 | 命令复杂，难以维护 |

## 🚨 **特殊情况处理**

### 情况1：Python环境不可用
```bash
# 1. 优先修复Python环境
# 2. 临时使用最简单的替代方案
# 3. 记录问题并后续修复
# 4. 绝对不要回退到PowerShell
```

### 情况2：需要新功能
```bash
# 1. 优先扩展现有Python脚本
# 2. 创建新的Python脚本
# 3. 更新操作指南
# 4. 记录到工作流目录
```

### 情况3：性能要求极高
```bash
# 1. 优化Python脚本性能
# 2. 考虑使用PyPy、Cython等
# 3. 在Python框架内解决问题
# 4. 不切换工具链
```

## 📋 **检查清单**
每次执行复杂操作前，检查：
- [ ] 是否可以使用Python脚本？
- [ ] 是否有现成的脚本可用？
- [ ] 是否需要创建新的Python脚本？
- [ ] 是否避免了PowerShell命令？
- [ ] 是否考虑了跨平台兼容性？
- [ ] 是否完善了错误处理？

## 📝 **创建新脚本的模板**
当需要创建新Python脚本时：
1. 使用 `workflows\examples\script_template.py`
2. 遵循PEP 8编码规范
3. 添加完善的文档字符串
4. 包含错误处理和日志记录
5. 输出结构化结果（JSON优先）

## 🔄 **更新记录**
- **2026-03-24**：基于用户建议创建此规则
- **来源**：用户明确指出代码统计应使用Python脚本
- **永久生效**：此规则将长期指导所有复杂操作

---
**创建时间**：2026-03-24  
**最后更新**：2026-03-24  
**强制级别**：必须遵守  
**例外情况**：无（特殊情况需记录并批准）