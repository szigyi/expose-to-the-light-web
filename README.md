# expose-to-the-light-web
expose-to-the-light-web client

## Install application
### Download installer
`curl -L -o install-web.sh https://www.dropbox.com/s/9s7ngvrukfkgtu5/install-web.sh?dl=1`

### Install
* `chmod u+x install-web.sh`
* `./install-web.sh`

### On Mac Os
* `brew install imagemagick`
* `brew install ufraw`
* `brew install exiftool`

#### On Raspberry Pi (unix)
TODO add apt install commands

## Run the app
`./ettl-web --rawDirectoryPath /home/pi/dev/expose-to-the-light/captured-images/ --logDirectoryPath /home/pi/dev/expose-to-the-light/logs`
