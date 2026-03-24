import re
import os
import sys

dev_plan_path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md'

with open(dev_plan_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 统计状态
completed = len(re.findall(r'状态.*已完成|\[x\]|\[✅\]', content))
inprogress = len(re.findall(r'状态.*开发中|\[🔄\]', content))
planned = len(re.findall(r'状态.*待开发|\[📋\]', content))
manual = len(re.findall(r'状态.*待人工解决|\[⚠️\]', content))

# 从开头获取初始状态
first_lines = content[:2000]
initial_stats = re.search(r'已完成功能:\s*(\d+).*?待开发功能:\s*(\d+)', first_lines)

if initial_stats:
    initial_completed = int(initial_stats.group(1))
    initial_planned = int(initial_stats.group(2))
else:
    initial_completed = 0
    initial_planned = 0

# 使用安全的输出方式
sys.stdout.reconfigure(encoding='utf-8')
print("=== 开发计划统计 ===")
print(f"状态统计 (基于正则匹配):")
print(f"- ✅ 已完成: {completed} 个")
print(f"- 🔄 开发中: {inprogress} 个")
print(f"- 📋 待开发: {planned} 个")
print(f"- ⚠️ 待人工解决: {manual} 个")
print()
print("文件头统计:")
print(f"- 已完成功能: {initial_completed} 个")
print(f"- 待开发功能: {initial_planned} 个")