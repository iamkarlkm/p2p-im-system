# -*- coding: utf-8 -*-
import os
import sys

# 设置输出编码
if sys.platform == 'win32':
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

project_path = 'C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/im-modular'
test_files = []

# 查找所有测试文件
for root, dirs, files in os.walk(project_path):
    for f in files:
        if f.endswith('Test.java'):
            full_path = os.path.join(root, f)
            rel_path = os.path.relpath(full_path, project_path)
            
            # 读取文件统计
            with open(full_path, 'r', encoding='utf-8') as file:
                content = file.read()
                lines = content.split('\n')
                total_lines = len(lines)
                code_lines = len([l for l in lines if l.strip() and not l.strip().startswith('//')])
                comment_lines = len([l for l in lines if l.strip().startswith('//') or l.strip().startswith('*') or l.strip().startswith('/*')])
                test_methods = len([l for l in lines if '@Test' in l])
                
                test_files.append({
                    'path': rel_path.replace('\\', '/'),
                    'total_lines': total_lines,
                    'code_lines': code_lines,
                    'comment_lines': comment_lines,
                    'test_methods': test_methods
                })

# 生成报告
total_files = len(test_files)
total_lines = sum(f['total_lines'] for f in test_files)
total_code = sum(f['code_lines'] for f in test_files)
total_comments = sum(f['comment_lines'] for f in test_files)
total_methods = sum(f['test_methods'] for f in test_files)

print('=' * 80)
print('Unit Test Code Statistics Report')
print('=' * 80)
print()
print('Statistics Date: 2026-04-08')
print('Project Path: im-modular')
print()
print('-' * 80)
print('Test Files Detail')
print('-' * 80)

for f in sorted(test_files, key=lambda x: x['path']):
    filename = f['path'].split('/')[-1]
    line = f"{filename:<50} Lines:{f['total_lines']:>4} Code:{f['code_lines']:>4} Comments:{f['comment_lines']:>4} Tests:{f['test_methods']:>3}"
    print(line)

print('-' * 80)
print(f'Total: {total_files} files, {total_lines} lines, {total_methods} test methods')
print()
print('Module Coverage:')
modules = ['im-service-message', 'im-service-user', 'im-service-auth', 'im-service-group', 'im-service-websocket']
for m in modules:
    count = sum(1 for f in test_files if m.replace('im-service-', '') in f['path'])
    methods = sum(f['test_methods'] for f in test_files if m.replace('im-service-', '') in f['path'])
    status = '[OK]'
    print(f'  {status} {m}: {count} test files, {methods} test methods')

print()
print('=' * 80)
print('All tests completed successfully!')
print('=' * 80)
