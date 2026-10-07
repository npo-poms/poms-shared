#!/usr/bin/env python3
import http.client
import os
import sys
import uuid
from urllib.parse import quote, urlsplit
"""
Ddependency-free reproduction of an MP4 upload It reads
`sourcingservice.video.baseUrl` and `sourcingservice.video.token` from
`~/conf/sourcingservice.properties` and streams the file without loading it into memory:

[source, shell]
----
python3 upload_mp4.py WO_VPRO_20363164 /path/to/video.mp4
python3 upload_mp4.py WO_VPRO_20363164 /path/to/video.mp4 profile-name
----

"""

mid, video = sys.argv[1:3]
profile = sys.argv[3] if len(sys.argv) > 3 else None
properties = {}
with open(os.path.expanduser("~/conf/sourcingservice.properties"), encoding="utf-8") as source:
    for line in source:
        if "=" in line and not line.lstrip().startswith(("#", "!")):
            key, value = line.split("=", 1)
            properties[key.strip()] = value.strip()
try:
    base_url = properties["sourcingservice.video.baseUrl"].rstrip("/")
    token = properties["sourcingservice.video.token"]
except KeyError as error:
    sys.exit(f"Missing {error.args[0]} in ~/conf/sourcingservice.properties")
url, boundary = urlsplit(f"{base_url}/api/ingest/{quote(mid, safe='')}/upload"), uuid.uuid4().hex
head = (f"--{boundary}\r\nContent-Disposition: form-data; name=\"file\"; filename=\"{os.path.basename(video)}\"\r\nContent-Type: video/mp4\r\n\r\n").encode()
profile_part = (
    f"\r\n--{boundary}\r\nContent-Disposition: form-data; name=\"profile\"\r\n\r\n{profile}"
).encode() if profile is not None else b""
tail = f"\r\n--{boundary}--\r\n".encode()
file_size = os.path.getsize(video)
connection = (http.client.HTTPSConnection if url.scheme == "https" else http.client.HTTPConnection)(url.hostname, url.port)
connection.putrequest("POST", url.path)
connection.putheader("Authorization", "Bearer " + token)
connection.putheader("Content-Type", f"multipart/form-data; boundary={boundary}")
connection.putheader("Content-Length", str(len(head) + file_size + len(profile_part) + len(tail)))
connection.endheaders()
connection.send(head)
uploaded = 0
with open(video, "rb") as source:
    for chunk in iter(lambda: source.read(1024 * 1024), b""):
        connection.send(chunk)
        uploaded += len(chunk)
        print(f"\rUploaded {uploaded / 1048576:.1f}/{file_size / 1048576:.1f} MiB ({uploaded / file_size:.0%})", end="", flush=True)
print()
connection.send(profile_part)
connection.send(tail)
response = connection.getresponse()
body = response.read().decode(errors="replace")
if 400 <= response.status < 600:
    print("Response headers:")
    for name, value in response.getheaders():
        print(f"{name}: {value}")
print(response.status, response.reason, body)
sys.exit(not 200 <= response.status < 300)
