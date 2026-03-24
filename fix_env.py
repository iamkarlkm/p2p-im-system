import os

# 读取原始 .env 文件
env_file = r"C:\Users\Administrator\.openclaw\skills\official__baidu-search\.env"
api_key = None

if os.path.exists(env_file):
    with open(env_file, 'r') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                if key == 'BAIDU_API_KEY':
                    api_key = value
                    break

print(f"Original API Key: [{api_key}]")

# 检查是否有空格，尝试合并
if api_key and ' ' in api_key:
    # 移除空格
    api_key_fixed = api_key.replace(' ', '')
    print(f"Fixed API Key (no space): [{api_key_fixed}]")
    
    # 写回 .env 文件
    with open(env_file, 'w') as f:
        f.write(f"BAIDU_API_KEY={api_key_fixed}\n")
    print("Updated .env file")
