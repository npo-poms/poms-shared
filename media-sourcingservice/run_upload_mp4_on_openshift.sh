#!/usr/bin/env bash
set -euo pipefail

mid=${1:?Usage: $0 MID /path/to/video.mp4 [profile]}
video=${2:?Usage: $0 MID /path/to/video.mp4 [profile]}
pod="upload-mp4-$RANDOM"
image="${UPLOAD_IMAGE:-python:3.12-slim}"

cleanup() {
    oc delete pod "$pod" --ignore-not-found --wait=false
}
trap cleanup EXIT

oc run "$pod" --image="$image" --restart=Never --command -- sleep infinity
oc wait --for=condition=Ready "pod/$pod" --timeout=120s
oc exec "$pod" -- mkdir -p /tmp/conf
oc cp upload_mp4.py "$pod:/tmp/upload_mp4.py"
oc cp "$HOME/conf/sourcingservice.properties" "$pod:/tmp/conf/sourcingservice.properties"
oc cp "$video" "$pod:/tmp/video.mp4"
if (($# >= 3)); then
    oc exec "$pod" -- env HOME=/tmp python3 /tmp/upload_mp4.py "$mid" /tmp/video.mp4 "$3"
else
    oc exec "$pod" -- env HOME=/tmp python3 /tmp/upload_mp4.py "$mid" /tmp/video.mp4
fi
