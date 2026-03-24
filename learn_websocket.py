import requests
r = requests.post('http://127.0.0.1:8008/search', json={'query': 'WebSocket IM 长连接 实时通讯 协议', 'top_k': 5})
print(f"Status: {r.status_code}")
data = r.json()
print(f"Total: {data.get('total', 0)}\n")
for i, item in enumerate(data.get('results', []), 1):
    print(f"{i}. {item.get('title', 'N/A')}")
    print(f"   {item.get('url', 'N/A')}")
    print()
