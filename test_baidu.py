import requests

try:
    response = requests.post(
        'http://127.0.0.1:8002/search',
        json={'query': 'Tauri Flutter Netty 即时通讯', 'top_k': 5}
    )
    print(response.text)
except Exception as e:
    print(f"Error: {e}")
