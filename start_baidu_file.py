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
                print(f"Set {key}")

# 确认 API Key
print(f"BAIDU_API_KEY: {os.environ.get('BAIDU_API_KEY', 'NOT SET')[:30]}...")

# 启动服务
sys.path.insert(0, r"C:\Users\Administrator\.openclaw\skills\official__baidu-search\src")

import uvicorn
from baidu_search.main import app

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8010)
