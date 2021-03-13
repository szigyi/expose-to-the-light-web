#!/usr/bin/env bash

# -help
# run-ettl-web.sh 0.1.4 --rawDirectoryPath /home/pi/dev/expose-to-the-light/captured-images/ --logDirectoryPath /home/pi/dev/expose-to-the-light/logs

version=$1
artifact="expose-to-the-light-web_2.13-"$version".jar"

shift
args=$*

echo "Starting application..."
java -jar "$artifact" "$args"
