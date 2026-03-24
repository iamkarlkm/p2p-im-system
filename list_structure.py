import os
import json

workspace_dir = "C:/Users/Administrator/.openclaw/workspace-clawd3"
projects_dir = os.path.join(workspace_dir, "projects")

print("工作空间目录结构:")
print("=" * 50)

def list_dirs(path, prefix=""):
    try:
        items = os.listdir(path)
        dirs = [d for d in items if os.path.isdir(os.path.join(path, d))]
        files = [f for f in items if os.path.isfile(os.path.join(path, f))]
        
        if dirs:
            for i, d in enumerate(dirs):
                is_last = (i == len(dirs)-1) and (not files)
                print(f"{prefix}{'└── ' if is_last else '├── '}{d}/")
                next_prefix = prefix + ("    " if is_last else "│   ")
                list_dirs(os.path.join(path, d), next_prefix)
        
        if files:
            for i, f in enumerate(files):
                is_last = (i == len(files)-1)
                print(f"{prefix}{'└── ' if is_last else '├── '}{f}")
    except Exception as e:
        print(f"{prefix}错误: {e}")

# 检查projects目录是否存在
if os.path.exists(projects_dir):
    print(f"projects/")
    list_dirs(projects_dir, "")
else:
    print("projects目录不存在")
    # 列出根目录内容
    items = os.listdir(workspace_dir)
    for item in items:
        item_path = os.path.join(workspace_dir, item)
        if os.path.isdir(item_path):
            print(f"{item}/")
        else:
            print(f"{item}")