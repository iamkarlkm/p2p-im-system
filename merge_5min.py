import csv
from datetime import datetime, timedelta
from collections import defaultdict

input_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\extracted_data.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\merged_5min.csv'

# 读取数据
data = []
with open(input_file, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    for row in reader:
        time_str = row['消费时间']
        amount = float(row['应付金额']) if row['应付金额'] else 0
        data.append({'time': time_str, 'amount': amount})

# 按5分钟分组
groups = defaultdict(float)

for item in data:
    if item['time']:
        try:
            dt = datetime.strptime(item['time'], '%Y-%m-%d %H:%M:%S')
            # 计算5分钟窗口的起始时间
            minute = (dt.minute // 5) * 5
            window_start = dt.replace(minute=minute, second=0, microsecond=0)
            window_key = window_start.strftime('%Y-%m-%d %H:%M:%S')
            groups[window_key] += item['amount']
        except:
            continue

# 排序并输出
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
