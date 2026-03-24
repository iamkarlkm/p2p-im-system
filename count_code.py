import os
import json

base_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"

# Define file patterns
file_patterns = {
    "Java": [".java"],
    "JavaScript": [".js", ".ts", ".tsx", ".jsx"],
    "Dart": [".dart"],
    "Rust": [".rs"],
    "HTML": [".html"],
    "CSS": [".css"]
}

# Initialize statistics
total_files = 0
total_lines = 0
type_stats = {type_name: {"count": 0, "lines": 0} for type_name in file_patterns.keys()}

# Get previous statistics
log_file = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_volume_monitor.md"
previous_total_lines = 0

try:
    if os.path.exists(log_file):
        with open(log_file, 'r', encoding='utf-8') as f:
            content = f.read()
            import re
            match = re.search(r"总代码行数：\[(\d+)\]", content)
            if match:
                previous_total_lines = int(match.group(1))
except:
    pass

# Count files recursively
for root, dirs, files in os.walk(base_path):
    for file in files:
        file_path = os.path.join(root, file)
        ext = os.path.splitext(file)[1].lower()
        
        # Determine file type
        file_type = None
        for type_name, extensions in file_patterns.items():
            if ext in extensions:
                file_type = type_name
                break
        
        if file_type:
            # Count lines
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    line_count = sum(1 for _ in f)
            except:
                line_count = 0
            
            type_stats[file_type]["count"] += 1
            type_stats[file_type]["lines"] += line_count
            total_files += 1
            total_lines += line_count

# Calculate change
line_change = total_lines - previous_total_lines
if line_change > 0:
    change_text = f"增加 {line_change} 行"
elif line_change < 0:
    change_text = f"减少 {-line_change} 行"
else:
    change_text = "无变化"

# Print results
print("=== 代码统计 ===")
print(f"总文件数: {total_files}")
print(f"总代码行数: {total_lines}")
print(f"代码量变化: {change_text}")
print()

print("=== 类型分布 ===")
for type_name in sorted(type_stats.keys()):
    count = type_stats[type_name]["count"]
    lines = type_stats[type_name]["lines"]
    print(f"{type_name}: {count} 个文件, {lines} 行")

# Create result dictionary
result = {
    "TotalFiles": total_files,
    "TotalLines": total_lines,
    "TypeStats": type_stats,
    "LineChange": line_change,
    "ChangeText": change_text
}

print("\n=== JSON 结果 ===")
print(json.dumps(result, ensure_ascii=False, indent=2))