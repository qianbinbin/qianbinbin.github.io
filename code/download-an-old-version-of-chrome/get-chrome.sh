#!/usr/bin/env sh

set -e

UUID=$(uuidgen | tr '[:upper:]' '[:lower:]')
PLATFORM=mac
OS_VERSION=
TARGET_VERSION_PREFIX=

error() { printf "%s\n" "$@" >&2; }

if [ "$(uname)" = Darwin ]; then
  OS_VERSION=$(sw_vers -productVersion | grep -o '[[:digit:]]\+\.[[:digit:]]\+')
fi

PRINT_ALL_LINKS=false
PRINT_SHA256=false
SHA_256=

SCRIPT=$(basename "$0")

USAGE=$(
  cat <<-END
Usage: $SCRIPT [<options>]
Get official download link of Chrome for macOS.

  -o <os_version>             set OS version, e.g. '10.15'
  -t <target_version_prefix>  set target version prefix of Chrome, e.g. '126'
  -a                          print all available links (by default, print first HTTPS link only)
  -s                          print sha256 checksum
  -h                          display this help and exit

Home page: <https://binac.org/posts/download-an-old-version-of-chrome/>
END
)

_exit() {
  error "$USAGE"
  exit 2
}

while getopts "asho:t:" c; do
  case $c in
  o) OS_VERSION=$OPTARG ;;
  t) TARGET_VERSION_PREFIX=$OPTARG ;;
  a) PRINT_ALL_LINKS=true ;;
  s) PRINT_SHA256=true ;;
  h) error "$USAGE" && exit ;;
  *) _exit ;;
  esac
done

[ -z "$OS_VERSION" ] && error "OS version not set" && exit 2

URL="https://tools.google.com/service/update2"

DATA="<request protocol=\"3.0\" requestid=\"$UUID\">
    <os platform=\"$PLATFORM\" version=\"$OS_VERSION\" />
    <app appid=\"com.google.Chrome\" lang=\"en-us\">
        <updatecheck targetversionprefix=\"$TARGET_VERSION_PREFIX\"/>
    </app>
</request>"

CONTENT=$(curl -fsSL -X POST "$URL" -d "$DATA")

LINKS=$(echo "$CONTENT" | grep -o 'https\?://[^"]*')
NAME=$(echo "$CONTENT" | grep -o ' name="[^"]*' | cut -c 8-)

if [ "$PRINT_ALL_LINKS" = false ]; then
  LINKS=$(echo "$LINKS" | grep -o 'https://[^"]*' | head -1)
fi
for link in $LINKS; do
  echo "$link$NAME"
done

SHA_256=$(echo "$CONTENT" | grep -o 'hash_sha256="[^"]*' | cut -c 14-)
if [ "$PRINT_SHA256" = true ]; then
  error "sha256 $SHA_256"
fi
