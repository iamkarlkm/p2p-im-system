# 统计development_plan.md中的功能状态

file_path = "C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/development_plan.md"

completed = 0
developing = 0
todo = 0
manual = 0

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()
    
completed = content.count("状态: 已完成")
developing = content.count("状态: 开发中")
todo = content.count("状态: 待开发")
manual = content.count("状态: 待人工解决")

print(f"已完成: {completed} 个")
print(f"开发中: {developing} 个")
print(f"待开发: {todo} 个")
print(f"待人工解决: {manual} 个")
print(f"总功能数: {completed + developing + todo + manual} 个")