import os
import shutil

script_dir = r'C:\Users\Administrator\.openclaw\skills\desktop-control\scripts'

# 需要重命名的文件
files_to_rename = [
    'app-control.ps1.txt',
    'input-sim.ps1.txt',
    'process-manager.ps1.txt',
    'screen-info.ps1.txt',
    'vscode-control.ps1.txt'
]

print('开始重命名文件...')
for old_name in files_to_rename:
    old_path = os.path.join(script_dir, old_name)
    new_name = old_name.replace('.ps1.txt', '.ps1')
    new_path = os.path.join(script_dir, new_name)
    
    if os.path.exists(old_path):
        # 复制文件内容到新文件
        shutil.copy2(old_path, new_path)
        # 删除原文件
        os.remove(old_path)
        print(f'OK: {old_name} -> {new_name}')
    else:
        print(f'NOT FOUND: {old_name}')

print('\n重命名完成！')
print('验证文件列表:')
for f in os.listdir(script_dir):
    print(f'  - {f}')
