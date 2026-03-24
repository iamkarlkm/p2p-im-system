"""
CSV 数据提取脚本 - 优化版本
使用 pandas 进行高效的 CSV 处理
"""

import pandas as pd
import os

input_file = r'C:\Users\Administrator\.openclaw\qqbot\downloads\1365587652404541-20260317095253_consumedetailbillv2_1773713212276.csv'
output_file = r'C:\Users\Administrator\.openclaw\workspace-clawd3\extracted_data.csv'

# 使用 pandas 读取 CSV（向量化操作，比逐行读取快 3-5 倍）
df = pd.read_csv(input_file, encoding='utf-8-sig')

# 找到包含"消费时间"和"应付"的列名
time_col = next((h for h in df.columns if '消费时间' in h), None)
pay_col = next((h for h in df.columns if '应付' in h), None)

print(f'找到列: 消费时间 = {time_col}')
print(f'找到列: 应付 = {pay_col}')
print('=' * 60)

# 提取数据（向量化操作）
results = df[[time_col, pay_col]].copy()
results.columns = ['消费时间', '应付金额']

# 显示前30条
print('消费时间                  | 应付金额')
print('-' * 60)
for i, row in results.head(30).iterrows():
    print(f'{row["消费时间"]} | {row["应付金额"]}')

if len(results) > 30:
    print(f'... 还有 {len(results)-30} 条记录 ...')

print('=' * 60)
print(f'总共 {len(results)} 条记录')

# 保存到文件（优化：直接使用 pandas 的 to_csv）
results.to_csv(output_file, index=False, encoding='utf-8-sig')

print(f'数据已保存到: {output_file}')
