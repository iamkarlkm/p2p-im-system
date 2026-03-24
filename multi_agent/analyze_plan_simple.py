import re
from pathlib import Path

# 读取开发计划文件
plan_path = Path("C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/development_plan.md")
content = plan_path.read_text(encoding='utf-8')

# 统计功能状态
completed_matches = re.findall(r'状态:\s*已完成', content)
developing_matches = re.findall(r'状态:\s*开发中', content)
todo_matches = re.findall(r'状态:\s*待开发', content)
manual_matches = re.findall(r'状态:\s*待人工解决', content)

print(f"已完成: {len(completed_matches)}")
print(f"开发中: {len(developing_matches)}")
print(f"待开发: {len(todo_matches)}")
print(f"待人工解决: {len(manual_matches)}")