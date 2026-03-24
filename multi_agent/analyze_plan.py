import re
from pathlib import Path

# 读取开发计划文件
plan_path = Path("C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/development_plan.md")
content = plan_path.read_text(encoding='utf-8')

# 统计功能状态
completed_matches = re.findall(r'状态:\s*\*?\*?\*?已完成', content)
developing_matches = re.findall(r'状态:\s*\*?\*?\*?开发中', content)
todo_matches = re.findall(r'状态:\s*\*?\*?\*?待开发', content)
manual_matches = re.findall(r'状态:\s*\*?\*?\*?待人工解决', content)

print(f"✅ 已完成: {len(completed_matches)}")
print(f"🔄 开发中: {len(developing_matches)}")
print(f"📋 待开发: {len(todo_matches)}")
print(f"⚠️ 待人工解决: {len(manual_matches)}")

# 提取前几个功能作为示例
features = []
feature_pattern = re.compile(r'### \d+\. ([^\n]+)')
for match in feature_pattern.finditer(content):
    feature_name = match.group(1).strip()
    
    # 查找该功能的状态
    start_pos = match.start()
    next_feature = content.find('### ', start_pos + 1)
    if next_feature == -1:
        next_feature = len(content)
    
    feature_block = content[start_pos:next_feature]
    
    status = "📋 待开发"
    if '状态: 已完成' in feature_block:
        status = "✅ 已完成"
    elif '状态: 开发中' in feature_block:
        status = "🔄 开发中"
    elif '状态: 待人工解决' in feature_block:
        status = "⚠️ 待人工解决"
    
    features.append((feature_name, status))
    if len(features) >= 10:  # 只取前10个
        break

print("\n功能列表示例:")
for name, status in features:
    print(f"- {name}: {status}")