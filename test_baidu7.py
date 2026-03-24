import requests
r = requests.post('http://127.0.0.1:8007/search', json={'query': 'Tauri Flutter Netty', 'top_k': 3})
print(f"Status: {r.status_code}")
print(r.text[:2000])
