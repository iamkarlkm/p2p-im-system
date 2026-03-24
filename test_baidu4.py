import requests
import json

# Test search
r = requests.post(
    'http://127.0.0.1:8004/search',
    json={'query': 'Tauri Flutter Netty 即时通讯', 'top_k': 3}
)

print("=== STATUS CODE ===")
print(r.status_code)
print()

print("=== RESPONSE ===")
print(r.text)
print()

# Try to parse and show all fields
try:
    data = r.json()
    print("=== ALL FIELDS ===")
    for key in data.keys():
        print(f"{key}: {data[key]}")
except:
    print("Not JSON")
