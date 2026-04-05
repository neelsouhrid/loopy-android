import urllib.request
import json
import os

url = "https://api.github.com/repos/fuhton/piano-mp3/git/trees/master?recursive=1"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode())
        
    for item in data.get('tree', []):
        path = item['path']
        if path.endswith('.mp3'):
            filename = os.path.basename(path).lower().replace('#', 's')
            # The repository has files like A4.mp3, Db4.mp3, etc. We can normalize them.
            # Actually just downloading them sequentially
            safe_name = filename.replace("-", "").replace(" ", "")
            download_url = f"https://raw.githubusercontent.com/fuhton/piano-mp3/master/{path.replace(' ', '%20')}"
            
            output_file = f"app/src/main/res/raw/{safe_name}"
            if not os.path.exists(output_file):
                print(f"Downloading {safe_name}...")
                with urllib.request.urlopen(download_url) as res, open(output_file, 'wb') as out_file:
                    out_file.write(res.read())
except Exception as e:
    print(f"Error: {e}")
