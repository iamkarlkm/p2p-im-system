import re

plan_path = "C:\\Users\\Administrator\\.openclaw\\workspace-clawd3\\multi_agent\\projects\\development_plan.md"

try:
    with open(plan_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    completed = len(re.findall(r'状态\s*:\s*已完成', content))
    in_progress = len(re.findall(r'状态\s*:\s*开发中', content))
    pending = len(re.findall(r'状态\s*:\s*待开发', content))
    needs_human = len(re.findall(r'状态\s*:\s*待人工解决', content))
    
    print(f"Completed: {completed}")
    print(f"In progress: {in_progress}")
    print(f"Pending: {pending}")
    print(f"Needs human: {needs_human}")
    print(f"Total: {completed + in_progress + pending + needs_human}")
    
except Exception as e:
    print(f"Error: {e}")