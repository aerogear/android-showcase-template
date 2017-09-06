#!/bin/bash

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 API_KEY" >&2
  exit 1
fi

KRYPTOWIRE_API_KEY=$1
URL="https://appsrv2.kryptowire.com/api/analyze"
PLATFORM="android"
FILEPATH="./app/build/outputs/apk/app-release.apk"

curl -X POST $URL -F file=@$FILEPATH -F key=$KRYPTOWIRE_API_KEY -F platform=$PLATFORM



