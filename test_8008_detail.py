import requests
import json
r = requests.post('http://127.0.0.1:8008/search', json={'query': '即时通讯 IM 系统架构', 'top_k': 3})
print(f"Status: {r.status_code}")
data = r.json()
print("Fields:", list(data.keys()))
print("Results count:", len(data.get('results', [])))
if data.get('results'):
    print("First result:", json.dumps(data['results'][0], ensure_ascii=False, indent=2))
