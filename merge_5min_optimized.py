"""
5分钟窗口合并脚本 - 优化版本
优化时间处理逻辑，减少内存占用
"""

import csv
from datetime import datetime
from collections import defaultdict

input_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\extracted_data.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\merged_5min.csv'

# 预定义时间格式（避免重复解析）
TIME_FORMAT = '%Y-%m-%d %H:%M:%S'

# 使用流式读取 + defaultdict 进行分组
groups = defaultdict(float)

with open(input_file, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    for row in reader:
        time_str = row.get('消费时间', '').strip()
        if not time_str:
            continue
        
        try:
            # 解析时间
            dt = datetime.strptime(time_str, TIME_FORMAT)
            
            # 计算5分钟窗口（使用整数运算，比除法更快）
            # minute // 5 * 5 等价于 (minute // 5) * 5
            minute = (dt.minute // 5) * 5
            # 使用时间戳作为键，避免字符串格式化
            window_key = f'{dt.year}-{dt.month:02d}-{dt.day:02d} {dt.hour:02d}:{minute:02d}:00'
            
            # 累加金额
            amount = float(row.get('应付金额', 0) or 0)
            groups[window_key] += amount
        except (ValueError, TypeError):
            continue

# 排序输出
sorted_groups = sorted(groups.items(), key=lambda x: x[0])

print('5分钟窗口起始时间      | 合并金额(元)')
print('-' * 50)
for time_key, total in sorted_groups:
    print(f'{time_key} | {total:.6f}')

print('=' * 50)
print(f'总共 {len(sorted_groups)} 个5分钟窗口')

# 保存到文件
with open(output_file, 'w', encoding='utf-8-sig', newline='') as f:
    writer = csv.writer(f)
    writer.writerow(['5分钟窗口起始时间', '合并金额(元)'])
    for time_key, total in sorted_groups:
        writer.writerow([time_key, f'{total:.6f}'])

print(f'结果已保存到: {output_file}')
