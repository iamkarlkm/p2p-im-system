import csv

input_file = r'C:\Users\Administrator\.openclaw\qqbot\downloads\1365587652404541-20260317095253_consumedetailbillv2_1773713212276.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\extracted_data.csv'

with open(input_file, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    
    # 找到包含"消费时间"和"应付"的列名
    headers = reader.fieldnames
    time_col = None
    pay_col = None
    
    for h in headers:
        if '消费时间' in h:
            time_col = h
        if '应付' in h:
            pay_col = h
    
    print(f'找到列: 消费时间 = {time_col}')
    print(f'找到列: 应付 = {pay_col}')
    print('=' * 60)
    
    # 提取数据
    results = []
    for row in reader:
        results.append({
            '消费时间': row.get(time_col, ''),
            '应付金额': row.get(pay_col, '')
        })
    
    # 显示前30条
    print(f'消费时间                  | 应付金额')
    print('-' * 60)
    for i, r in enumerate(results[:30]):
        print(f'{r["消费时间"]} | {r["应付金额"]}')
    
    if len(results) > 30:
        print(f'... 还有 {len(results)-30} 条记录 ...')
    
    print('=' * 60)
    print(f'总共 {len(results)} 条记录')
    
    # 保存到文件
    with open(output_file, 'w', encoding='utf-8-sig', newline='') as out:
        writer = csv.DictWriter(out, fieldnames=['消费时间', '应付金额'])
        writer.writeheader()
        writer.writerows(results)
    
    print(f'数据已保存到: {output_file}')
