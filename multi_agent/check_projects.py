import os

def count_project_code(project_path):
    file_extensions = {
        'Java': ['.java'],
        'JavaScript': ['.js', '.ts', '.tsx', '.jsx'],
        'Dart': ['.dart'],
        'Rust': ['.rs'],
        'HTML/CSS': ['.html', '.css']
    }
    
    results = {'total_files': 0, 'total_lines': 0}
    
    for root, dirs, files in os.walk(project_path):
        for file in files:
            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1].lower()
            
            # 检查文件类型
            found = False
            for category, exts in file_extensions.items():
                if ext in exts:
                    found = True
                    break
            
            if found:
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = f.readlines()
                        line_count = len(lines)
                        
                        results['total_files'] += 1
                        results['total_lines'] += line_count
                except:
                    pass
    
    return results

projects_dir = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
projects = ['im-backend', 'im-desktop', 'im-mobile']

print('项目代码统计:')
print('=' * 50)

total_files = 0
total_lines = 0

for project in projects:
    project_path = os.path.join(projects_dir, project)
    if os.path.exists(project_path):
        stats = count_project_code(project_path)
        print(f'{project}: {stats["total_files"]}个文件, {stats["total_lines"]}行代码')
        total_files += stats['total_files']
        total_lines += stats['total_lines']
    else:
        print(f'{project}: 目录不存在')

print('=' * 50)
print(f'总计: {total_files}个文件, {total_lines}行代码')
print(f'差异: 总统计1068个文件, 项目统计{total_files}个文件, 差{1068-total_files}个文件')