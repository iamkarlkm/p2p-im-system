import os
from dotenv import load_dotenv

# 加载 .env 文件
env_path = r"C:\Users\Administrator\.openclaw\skills\official__baidu-search\.env"
load_dotenv(env_path)

# 检查 API Key
api_key = os.getenv("BAIDU_API_KEY")
print(f"Loaded API Key: {api_key[:20]}..." if api_key else "No API Key found")

# 启动服务
import uvicorn
from baidu_search.main import app

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8007)
