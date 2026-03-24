import os
import sys

def check_directory(path):
    print(f"检查目录: {path}")
    if os.path.exists(path):
        print("目录存在")
        items = os.listdir(path)
        print(f"目录内容 ({len(items)}项):")
        for item in items:
            item_path = os.path.join(path, item)
            if os.path.isdir(item_path):
                print(f"  [DIR] {item}/")
            else:
                print(f"  [FILE] {item}")
    else:
        print("目录不存在")

# 检查主目录
multi_agent_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent"
projects_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
knowledge_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"

print("=== 目录结构检查 ===")
check_directory(multi_agent_path)
print("\n=== Projects 目录 ===")
check_directory(projects_path)
print("\n=== Knowledge 目录 ===")
check_directory(knowledge_path)