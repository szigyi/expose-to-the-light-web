#!/usr/bin/env bash

replace_string() {
  placeholder=$1
  new_string=$2
  file_name=$3
  ESCAPED_REPLACE=$(printf '%s\n' "$new_string" | sed -e 's/[\/&]/\\&/g')

  sed -i '' -e "s/$placeholder/$ESCAPED_REPLACE/" "$file_name"
}

artifact_link="https://www.dropbox.com/s/bg6wl2hkgjqg2gw/expose-to-the-light-web_2.13-0.1.11.jar?dl=1"
ettl_web_link="https://www.dropbox.com/s/ze6zrtwipzx6xee/ettl-web?dl=1"
artifact="expose-to-the-light-web_2.13-0.1.11.jar"

echo "Creating app folder at /usr/local/opt/ettl"
sudo mkdir -p /usr/local/opt/ettl
sudo chown -R pi /usr/local/opt/ettl
cd /usr/local/opt/ettl

echo "Downloading artifact to /usr/local/opt/ettl..."
curl -L -o "$artifact" "$artifact_link"

echo "Downloading ettl script to /usr/local/opt/ettl..."
curl -L -o "ettl-web" "$ettl_web_link"

sudo chmod u+x ettl-web

echo "Artifact's Manifest file:"
jar xf "$artifact" META-INF/MANIFEST.MF && cat META-INF/MANIFEST.MF

echo "Installing and adding to PATH..."

# make the artifact path absolute
replace_string "artif=.*" "artif=\"/usr/local/opt/ettl/$artifact\"" "ettl-web"

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  echo "Detected OS is Linux"
  echo "Installing ettl command to /usr/local/bin"
  cd /usr/local/bin
  sudo ln -fs /usr/local/opt/ettl/ettl-web ettl-web
  sudo chown -R pi /usr/local/bin/ettl-web

  echo "Installing dependencies"
  sudo apt install imagemagick
  sudo apt install exiftool
  source ~/.bashrc

elif [[ "$OSTYPE" == "darwin"* ]]; then
  echo "Detected OS is macOS"
  echo "Installing ettl command to /usr/local/bin"
  cd /usr/local/bin
  sudo ln -fs /usr/local/opt/ettl/ettl-web ettl-web

  echo "Installing dependencies"
  brew install imagemagick
  brew install ufraw
  brew install exiftool

  source "$ZSH"/oh-my-zsh.sh

else
  echo "big problemo! No OSTYPE"
fi

echo "Install is finished"
