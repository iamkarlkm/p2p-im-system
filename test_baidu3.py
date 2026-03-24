import requests
import json

# Check all fields in response
r = requests.post(
    'http://127.0.0.1:8001/search',
    json={'query': 'test', 'top_k': 1}
)

print("=== STATUS CODE ===")
print(r.status_code)
print()

print("=== ALL RESPONSE TEXT ===")
print(r.text)
print()

print("=== RESPONSE JSON (if any) ===")
try:
    data = r.json()
    print(json.dumps(data, indent=2, ensure_ascii=False))
except:
    print("Not JSON")
print()

print("=== RESPONSE HEADERS ===")
for k, v in r.headers.items():
    print(f"{k}: {v}")
