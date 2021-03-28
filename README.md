# expose-to-the-light-web

expose-to-the-light-web client

## Install application

### Download installer

`curl -L -o install-web.sh https://www.dropbox.com/s/65d86fx4h3b5vdc/install-web.sh?dl=1`

### Install

* `chmod u+x install-web.sh`
* `./install-web.sh`

### Runtime dependencies if you are not using the installer

Installer should install these dependencies for you.

### On Mac Os

* `brew cask install java11` [https://medium.com/macoclock/using-homebrew-to-install-java-jdk11-on-macos-44b30f497b38]
* `brew install imagemagick`
* `brew install ufraw`
* `brew install exiftool`
* [ettl app](https://github.com/szigyi/expose-to-the-light) README should guide you to install it

#### On Raspberry Pi (unix)

* `sudo apt-get -y install openjdk-11-jdk`
* `sudo apt -y install imagemagick`
* `sudo apt -y install exiftool`
* [ettl app](https://github.com/szigyi/expose-to-the-light) README should guide you to install it

## Run the app

`ettl-web`

## Install on raspberry pi

* Installing raspbian on SD card
    * [Install Raspberry Pi OS using Raspberry Pi Imager](https://www.raspberrypi.org/software/)
    * Fully fledged version
* Install hotspot on the pi - so you can use it anywhere without wifi
    * [Autohotspot](https://www.raspberryconnect.com/projects/65-raspberrypi-hotspot-accesspoints/183-raspberry-pi-automatic-hotspot-and-static-hotspot-installer)
    * I have to modify the `nameserver` dns lookup list after this
        * open file `sudo nano /etc/resolv.conf` and add
            * `nameserver 8.8.8.8`
            * `nameserver 8.8.4.4`
* Install ettl app
    * Use the link from the beginning of ettl's README file to download the installer
* Install ettl web app
    * Use the link from the beginning of this README file to download the installer
