import requests
import json

# Test search
r = requests.post(
    'http://127.0.0.1:8005/search',
    json={'query': 'Tauri Flutter Netty 即时通讯', 'top_k': 3}
)

print("=== STATUS CODE ===")
print(r.status_code)
print()

print("=== RESPONSE ===")
print(r.text[:2000])
print()

# Try to parse and show all fields
try:
    data = r.json()
    print("=== ALL FIELDS ===")
    for key in data.keys():
        val = data[key]
        if isinstance(val, list):
            print(f"{key}: [list with {len(val)} items]")
            if len(val) > 0:
                print(f"  First item: {val[0]}")
        else:
            print(f"{key}: {str(val)[:200]}")
except Exception as e:
    print(f"Error parsing JSON: {e}")
