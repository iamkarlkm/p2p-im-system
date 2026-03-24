"""
数据过滤脚本 - 优化版本
使用流式处理减少内存占用
"""

import csv

input_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\merged_5min.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\filtered_5min.csv'

# 使用流式处理：边读边写，减少内存占用
filtered_count = 0
total_count = 0

with open(input_file, 'r', encoding='utf-8-sig') as f_in, \
     open(output_file, 'w', encoding='utf-8-sig', newline='') as f_out:
    
    reader = csv.DictReader(f_in)
    writer = csv.DictWriter(f_out, fieldnames=['5分钟窗口起始时间', '合并金额(元)'])
    writer.writeheader()
    
    for row in reader:
        total_count += 1
        try:
            amount = float(row['合并金额(元)'])
            if amount >= 0.01:
                writer.writerow({
                    '5分钟窗口起始时间': row['5分钟窗口起始时间'],
                    '合并金额(元)': row['合并金额(元)']
                })
                filtered_count += 1
        except (ValueError, KeyError):
            continue

print(f'原始记录数: {total_count}')
print(f'过滤后记录数: {filtered_count}')
print(f'删除记录数: {total_count - filtered_count}')
print('=' * 50)

# 显示前20条（使用流式读取）
print('消费时间              | 合并金额(元)')
print('-' * 50)
with open(output_file, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    for i, row in enumerate(reader):
        if i >= 20:
            break
        print(f"{row['5分钟窗口起始时间']} | {row['合并金额(元)']}")

if filtered_count > 20:
    print(f'... 还有 {filtered_count-20} 条 ...')

print('=' * 50)
print(f'过滤后的数据已保存到: {output_file}')
