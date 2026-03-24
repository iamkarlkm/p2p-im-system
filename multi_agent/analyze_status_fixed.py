import re
import sys

# 设置输出编码
sys.stdout.reconfigure(encoding='utf-8')

path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# 统计状态（使用文本匹配）
completed = len(re.findall(r'状态\s*:\s*✅\s*已完成', content))
in_progress = len(re.findall(r'状态\s*:\s*🔄\s*开发中', content))
to_develop = len(re.findall(r'状态\s*:\s*📋\s*待开发', content))
needs_manual = len(re.findall(r'状态\s*:\s*⚠️\s*待人工解决', content))

# 查找功能标题和状态
features = []
current_feature = None

lines = content.split('\n')
for line in lines:
    # 查找功能标题
    title_match = re.match(r'###\s*(\d+)\.\s+(.*)', line)
    if title_match:
        current_feature = title_match.group(2).strip()
        continue
    
    # 查找状态行
    if current_feature:
        status_match = re.match(r'.*状态\s*:\s*([✅🔄📋⚠️]+\s*[^\n]*)', line)
        if status_match:
            status = status_match.group(1).strip()
            features.append((current_feature, status))
            current_feature = None

print('统计结果:')
print(f'已完成: {completed}个')
print(f'开发中: {in_progress}个')
print(f'待开发: {to_develop}个')
print(f'待人工解决: {needs_manual}个')
print('\n功能列表 (前15个):')
for i, (name, status) in enumerate(features[:15], 1):
    print(f'{i}. {name[:50]}... [{status}]')