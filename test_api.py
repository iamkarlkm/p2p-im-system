import requests
r = requests.post('http://127.0.0.1:8011/search', json={'query': 'Tauri Flutter', 'top_k': 3})
print(f"Status: {r.status_code}")
print(r.text[:1000])
