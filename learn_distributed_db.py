import requests
r = requests.post('http://127.0.0.1:8008/search', json={'query': '分布式数据库 IM 即时通讯 系统架构', 'top_k': 5})
print(f"Status: {r.status_code}")
data = r.json()
print(f"Total results: {data.get('total', 0)}")
print("\n=== Results ===")
for i, item in enumerate(data.get('results', []), 1):
    print(f"\n{i}. {item.get('title', 'N/A')}")
    print(f"   URL: {item.get('url', 'N/A')}")
    print(f"   Snippet: {item.get('snippet', 'N/A')[:200]}...")
