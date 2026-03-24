import os
import sys

# 读取 .env 文件
env_file = r"C:\Users\Administrator\.openclaw\skills\official__baidu-search\.env"
print(f"Loading env from: {env_file}")

if os.path.exists(env_file):
    with open(env_file, 'r') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                os.environ[key] = value
                print(f"Set {key} = {value}")

# 确认 API Key
api_key = os.environ.get('BAIDU_API_KEY', 'NOT SET')
print(f"\nBAIDU_API_KEY value: [{api_key}]")
print(f"Length: {len(api_key)}")

# 检查是否有空格
if ' ' in api_key:
    print("WARNING: API key contains spaces!")
    parts = api_key.split(' ')
    print(f"Parts: {parts}")
