# 更新 roadmap 中所有功能的状态
import re

with open(r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md', 'r', encoding='utf-8') as f:
    content = f.read()

# 替换 #129 到 #137 功能的状态
for i in range(129, 138):
    pattern = rf'(## 🆕 功能 #{i}:.*?\*\*状态\*\*: )待开发'
    replacement = rf'\1✅ 已转移到开发计划'
    content = re.sub(pattern, replacement, content, flags=re.DOTALL)

# 写入更新后的文件
with open(r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md', 'w', encoding='utf-8') as f:
    f.write(content)

print("已更新 roadmap 中 #129-#137 功能的状态")