import requests

r = requests.post('http://127.0.0.1:8006/search', json={'query': 'hello', 'top_k': 1})
print(f"Status: {r.status_code}")
print(r.text[:1000])
