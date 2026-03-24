import re

path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# 查找所有功能部分
pattern = r'###\s*(\d+\.\s+.*?)\n.*?状态\s*:\s*([✅🔄📋⚠️]+\s*[^\n]+)'
matches = re.findall(pattern, content, re.DOTALL)

completed = 0
in_progress = 0
to_develop = 0
needs_manual = 0

feature_list = []

for title, status in matches:
    # 提取状态
    if '✅ 已完成' in status:
        completed += 1
        status_symbol = '✅ 已完成'
    elif '🔄 开发中' in status or '🔄 待开发' in status:
        in_progress += 1
        status_symbol = '🔄 开发中'
    elif '📋 待开发' in status:
        to_develop += 1
        status_symbol = '📋 待开发'
    elif '⚠️ 待人工解决' in status:
        needs_manual += 1
        status_symbol = '⚠️ 待人工解决'
    else:
        status_symbol = status.strip()
    
    # 提取功能名称
    name_match = re.search(r'###\s*\d+\.\s+([^\n]+)', title)
    if name_match:
        feature_name = name_match.group(1).strip()
        feature_list.append((feature_name, status_symbol))

print(f'统计结果:')
print(f'✅ 已完成: {completed}个')
print(f'🔄 开发中: {in_progress}个')
print(f'📋 待开发: {to_develop}个')
print(f'⚠️ 待人工解决: {needs_manual}个')
print('\n功能列表 (前20个):')
for i, (name, status) in enumerate(feature_list[:20], 1):
    print(f'{i}. {name[:60]} [{status}]')