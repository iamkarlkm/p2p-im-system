import re
import json
from pathlib import Path

# 读取 development_plan.md 文件
plan_file = Path(r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md")
content = plan_file.read_text(encoding='utf-8')

# 查找功能状态
status_patterns = {
    "已完成": r"状态\s*:\s*已完成",
    "开发中": r"状态\s*:\s*开发中",
    "待开发": r"状态\s*:\s*📋\s*待开发",
    "待人工解决": r"状态\s*:\s*⚠️\s*待人工解决"
}

# 查找功能定义
function_blocks = re.findall(r'### \d+\. .*?(?=###|\Z)', content, re.DOTALL)

completed_count = 0
in_progress_count = 0
pending_count = 0
manual_resolution_count = 0

functions_by_status = {
    "已完成": [],
    "开发中": [],
    "待开发": [],
    "待人工解决": []
}

for block in function_blocks:
    # 提取功能名称
    name_match = re.search(r'### (\d+)\. (.+)', block)
    if not name_match:
        continue
    
    func_num = name_match.group(1)
    func_name = name_match.group(2).strip()
    
    # 检查状态
    status = None
    for status_text, pattern in status_patterns.items():
        if re.search(pattern, block):
            status = status_text
            break
    
    if not status:
        # 如果没有找到状态标记，检查是否有 "状态：" 文本
        if "状态：" in block:
            # 尝试提取状态值
            status_match = re.search(r'状态\s*:\s*([^\n]+)', block)
            if status_match:
                status_text = status_match.group(1).strip()
                if "已完成" in status_text:
                    status = "已完成"
                elif "开发中" in status_text:
                    status = "开发中"
                elif "待开发" in status_text:
                    status = "待开发"
                elif "待人工解决" in status_text:
                    status = "待人工解决"
    
    if not status:
        # 默认设置为待开发
        status = "待开发"
    
    # 计数
    if status == "已完成":
        completed_count += 1
    elif status == "开发中":
        in_progress_count += 1
    elif status == "待开发":
        pending_count += 1
    elif status == "待人工解决":
        manual_resolution_count += 1
    
    functions_by_status[status].append(f"{func_num}. {func_name}")

# 直接返回结果，不打印中文字符
result = {
    "completed": completed_count,
    "in_progress": in_progress_count,
    "pending": pending_count,
    "manual_resolution": manual_resolution_count,
    "total": completed_count + in_progress_count + pending_count + manual_resolution_count
}

# 创建统计文本（使用简单的ASCII表示）
status_summary = f"Completed: {completed_count}\nIn Progress: {in_progress_count}\nPending: {pending_count}\nManual Resolution: {manual_resolution_count}"
print(status_summary)

# 返回JSON结果
print("\n=== JSON Result ===")
print(json.dumps(result, ensure_ascii=False, indent=2))