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
----

"""

mid, video = sys.argv[1:]
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
tail = f"\r\n--{boundary}--\r\n".encode()
file_size = os.path.getsize(video)
connection = (http.client.HTTPSConnection if url.scheme == "https" else http.client.HTTPConnection)(url.hostname, url.port)
connection.putrequest("POST", url.path)
connection.putheader("Authorization", f"Bearer {token}")
connection.putheader("Content-Type", f"multipart/form-data; boundary={boundary}")
connection.putheader("Content-Length", str(len(head) + file_size + len(tail)))
connection.endheaders()
connection.send(head)
uploaded = 0
with open(video, "rb") as source:
    for chunk in iter(lambda: source.read(1024 * 1024), b""):
        connection.send(chunk)
        uploaded += len(chunk)
        print(f"\rUploaded {uploaded / 1048576:.1f}/{file_size / 1048576:.1f} MiB ({uploaded / file_size:.0%})", end="", flush=True)
print()
connection.send(tail)
response = connection.getresponse()
print(response.status, response.reason, response.read().decode(errors="replace"))
sys.exit(not 200 <= response.status < 300)
