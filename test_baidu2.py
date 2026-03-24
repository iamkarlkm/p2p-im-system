import requests
import json

# Try different endpoints or fields
try:
    # Test ping
    r1 = requests.get('http://127.0.0.1:8001/ping')
    print("=== PING ===")
    print(r1.text)
    print()
    
    # Try search with different format
    r2 = requests.post(
        'http://127.0.0.1:8001/search',
        json={'query': 'hello', 'top_k': 3}
    )
    print("=== SEARCH ===")
    print(r2.status_code)
    print(r2.text)
except Exception as e:
    print(f"Error: {e}")
