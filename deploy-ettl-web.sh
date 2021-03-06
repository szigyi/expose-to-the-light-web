#!/usr/bin/env bash

token=$1
buildNumber=$2
export BUILD_NUMBER="$buildNumber"
version=$(sbt -Dsbt.supershell=false -error "print version")

echo ""
echo "Assembling $version jar package..."
sbt clean assembly

echo ""
echo "Deploying $version jar package to Dropbox..."
asset_src="target/scala-2.13/expose-to-the-light-web_2.13-$version.jar"
asset_dst="/artifact/expose-to-the-light-web_2.13-$version.jar"
install_src="install-ettl-web.sh"
install_dst="/script/install-ettl-web.sh"

curl -X POST https://content.dropboxapi.com/2/files/upload \
    --header "Authorization: Bearer $token" \
    --header "Dropbox-API-Arg: {\"path\": \"$asset_dst\"}" \
    --header "Content-Type: application/octet-stream" \
    --data-binary @$asset_src

echo ""
echo "Making uploaded artifact public..."
shared_link=$(
          curl -X POST https://api.dropboxapi.com/2/sharing/create_shared_link_with_settings \
              --header "Authorization: Bearer $token" \
              --header "Content-Type: application/json" \
              --data "{\"path\": \"$asset_dst\",\"settings\": {\"requested_visibility\": \"public\"}}"  | jq -r '.url'
              )
echo "Shared Link:"
echo "$shared_link"

new_version_map="version_url_map[\"$version\"]=\"$shared_link\""

ESCAPED_REPLACE=$(printf '%s\n' "$new_version_map" | sed -e 's/[\/&]/\\&/g')
ESCAPED_REPLACE="$ESCAPED_REPLACE\\
##_new_version_url_map_here"

sed -i '' -e "s/##_new_version_url_map_here/$ESCAPED_REPLACE/" install-ettl-web.sh

echo ""
echo "Deploying $version install script..."
curl -X POST https://content.dropboxapi.com/2/files/upload \
    --header "Authorization: Bearer $token" \
    --header "Dropbox-API-Arg: {\"path\": \"$install_dst\", \"mode\": \"overwrite\"}" \
    --header "Content-Type: application/octet-stream" \
    --data-binary @$install_src