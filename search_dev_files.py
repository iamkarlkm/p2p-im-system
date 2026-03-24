import os
import sys

workspace_dir = "C:/Users/Administrator/.openclaw/workspace-clawd3"
search_terms = ["plan", "project", "开发", "todo", "task", "需求", "功能"]

files_found = []

for root, dirs, files in os.walk(workspace_dir):
    for file in files:
        if file.lower().endswith(('.md', '.txt', '.json', '.yaml', '.yml')):
            for term in search_terms:
                if term.lower() in file.lower():
                    files_found.append(os.path.join(root, file))
                    break
            # 也检查文件内容
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read(5000).lower()
                    for term in search_terms:
                        if term.lower() in content:
                            if filepath not in files_found:
                                files_found.append(filepath)
                            break
            except:
                continue

print("找到的开发相关文件:")
print("-" * 50)
for f in files_found[:20]:  # 只显示前20个
    rel_path = os.path.relpath(f, workspace_dir)
    try:
        print(f"* {rel_path}")
    except:
        print(f"* {rel_path.encode('ascii', 'replace').decode('ascii')}")

# 优先读取包含"开发计划"的文件
print("\n\n优先读取的内容:")
print("-" * 50)
for f in files_found:
    if "开发" in f.lower() or "plan" in f.lower():
        rel_path = os.path.relpath(f, workspace_dir)
        print(f"\n=== 文件: {rel_path} ===")
        try:
            with open(f, 'r', encoding='utf-8') as fobj:
                print(fobj.read()[:1000])  # 只显示前1000字符
        except:
            print("无法读取文件")