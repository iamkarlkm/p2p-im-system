import csv

input_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\merged_5min.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\filtered_5min.csv'

# 读取数据
with open(input_file, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    data = list(reader)

# 过滤掉小于0.01元的记录
filtered = []
for row in data:
    try:
        amount = float(row['合并金额(元)'])
        if amount >= 0.01:
            filtered.append(row)
    except:
        continue

print(f'原始记录数: {len(data)}')
print(f'过滤后记录数: {len(filtered)}')
print(f'删除记录数: {len(data) - len(filtered)}')
print('=' * 50)

# 显示前20条
print('消费时间              | 合并金额(元)')
print('-' * 50)
for row in filtered[:20]:
    print(f"{row['5分钟窗口起始时间']} | {row['合并金额(元)']}")

if len(filtered) > 20:
    print(f'... 还有 {len(filtered)-20} 条 ...')

# 保存到文件
with open(output_file, 'w', encoding='utf-8-sig', newline='') as f:
    writer = csv.DictWriter(f, fieldnames=['5分钟窗口起始时间', '合并金额(元)'])
    writer.writeheader()
    writer.writerows(filtered)

print('=' * 50)
print(f'过滤后的数据已保存到: {output_file}')
