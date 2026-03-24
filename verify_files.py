import os

workspace = 'C:/Users/Administrator/.openclaw/workspace-clawd3'
backend_dir = os.path.join(workspace, 'projects', 'im-backend', 'src', 'main', 'java', 'com', 'im', 'backend')

files_to_check = [
    'controller/FriendController.java',
    'service/FriendService.java', 
    'dto/SendFriendRequest.java'
]

print('好友关系管理模块文件检查：')
print('=' * 50)

for file_path in files_to_check:
    full_path = os.path.join(backend_dir, file_path)
    if os.path.exists(full_path):
        size = os.path.getsize(full_path)
        print(f'[OK] {file_path:40} {size:6} bytes')
    else:
        print(f'[FAIL] {file_path:40} File not found')

print()
print('API文档文件：')
api_doc_path = os.path.join(workspace, 'projects', 'im-backend', 'friend_api_docs.md')
if os.path.exists(api_doc_path):
    size = os.path.getsize(api_doc_path)
    print(f'[OK] friend_api_docs.md{28} {size:6} bytes')

print()
print('开发日志文件：')
log_path = os.path.join(workspace, 'projects', 'im-backend', 'feature_changelog_friend.md')
if os.path.exists(log_path):
    size = os.path.getsize(log_path)
    print(f'[OK] feature_changelog_friend.md{23} {size:6} bytes')

print()
print('开发计划更新检查：')
plan_path = os.path.join(workspace, 'development_plan.md')
if os.path.exists(plan_path):
    with open(plan_path, 'r', encoding='utf-8') as f:
        content = f.read()
        if '好友关系管理 | backend | 已完成' in content:
            print('[OK] 开发计划已更新（状态：已完成）')
        else:
            print('[FAIL] 开发计划未更新')
else:
    print('[FAIL] 开发计划文件不存在')