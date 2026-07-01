#!/usr/bin/env sh

BRIEF=false
VIDEO_EXT="3gp avi flv m4v mkv mov mp4 mpeg mpg ts vob webm wmv"
NUM=10
VIDEO_BITRATE=false

SCRIPT=$(basename "$0")

USAGE=$(
  cat <<-END
Usage: $SCRIPT [<options>] <path>...
List top videos by bitrate.

  -b            do not prepend bitrate to output lines
  -e <ext>      video file extensions, separated by spaces, case-insensitive
                defaults to '$VIDEO_EXT'
  -n <num>      list top num video files
                defaults to '$NUM'
  -v            list by the first video stream bitrate instead of the overall bitrate
                not applicable for containers like MKV, TS, WebM, etc
  -h            display this help and exit

Home page: <https://binac.org/posts/video-related-commands-and-scripts/>
END
)

error() { printf "%s\n" "$@" >&2; }

_exit() {
  error "$USAGE"
  exit 2
}

while getopts "bhve:n:" c; do
  case $c in
  b) BRIEF=true ;;
  e) VIDEO_EXT=$OPTARG ;;
  n) NUM=$OPTARG ;;
  v) VIDEO_BITRATE=true ;;
  h) error "$USAGE" && exit ;;
  *) _exit ;;
  esac
done

is_integer() {
  [ -n "$1" ] && [ "$1" -eq "$1" ] 2>/dev/null
}

is_integer "$NUM" || _exit

shift $((OPTIND - 1))
[ $# -eq 0 ] && _exit

min() {
  [ "$1" -le "$2" ] && echo "$1" || echo "$2"
}

get_bitrate() {
  if [ "$VIDEO_BITRATE" = false ]; then
    ffprobe -v error -show_entries format=bit_rate -of default=noprint_wrappers=1:nokey=1 "$1"
  else
    ffprobe -v error -select_streams v:0 -show_entries stream=bit_rate -of default=noprint_wrappers=1:nokey=1 "$1" | head -n 1
  fi
}

TMP_FILE=$(mktemp) || exit 1
trap 'rm -f "$TMP_FILE"' EXIT

count=0
grep_expression="$(echo " $VIDEO_EXT" | sed 's/ /\$|\\./g' | cut -c3-)$"
find "$@" -type f | grep -i -E "$grep_expression" | while IFS= read -r f; do
  : $((count += 1))
  printf "Detecting videos: %s\r" "$count" >&2
  br=$(get_bitrate "$f")
  if is_integer "$br"; then
    echo "$(numfmt --to iec "$br")|$f" >>"$TMP_FILE"
  else
    printf "\n%s\n" "Cannot get bitrate: $f" >&2
  fi
done
count=$(wc -l <"$TMP_FILE" | xargs)
printf "\n" >&2

if [ "$count" -eq 0 ]; then
  error "No videos found"
  exit
else
  error "Detected $count videos"
fi

error "Top $(min "$NUM" "$count") videos by bitrate:"
[ "$BRIEF" = true ] || printf "%-16s%s\n" "Bitrate" "File"
sort -t '|' -k1 -hr "$TMP_FILE" | head -n "$NUM" | while IFS= read -r line; do
  br=$(echo "${line%%|*}" | xargs)
  f=${line#*|}
  [ "$BRIEF" = true ] && echo "$f" || printf "%-16s%s\n" "$br" "$f"
done
