#!/usr/bin/env bash

version=$1
artifact="expose-to-the-light-web_2.13-"$version".jar"

if [[ ! -n $(which java) ]]; then
  echo "Installing jdk as did not found it on this machine..."
  sudo apt-get update
  sudo apt-get install default-jdk
fi

declare -A version_url_map
##_new_version_url_map_here

echo "Downloading artifact..."
wget -O "$artifact" ${version_url_map["$version"]}

echo "Artifact's Manifest file:"
jar xf "$artifact" META-INF/MANIFEST.MF && cat META-INF/MANIFEST.MF