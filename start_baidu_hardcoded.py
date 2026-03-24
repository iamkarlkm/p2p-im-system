import os
import sys

# 直接设置 API Key
os.environ['BAIDU_API_KEY'] = 'APIKey-20260315132107bce-v3/ALTAK-7mdr4Jx47Nsed7bMNMZ7I/4595b871bf8f4255f7a36a9954224dc5d788a014'

print(f"Set BAIDU_API_KEY: {os.environ.get('BAIDU_API_KEY')[:20]}...")

# 设置 PYTHONPATH
sys.path.insert(0, r"C:\Users\Administrator\.openclaw\skills\official__baidu-search\src")

# 启动服务
import uvicorn
from baidu_search.main import app

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8011)
