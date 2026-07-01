#!/usr/bin/env sh

BRIEF=false
VIDEO_EXT="3gp avi flv m4v mkv mov mp4 mpeg mpg ts vob webm wmv"
NUM=10

SCRIPT=$(basename "$0")

USAGE=$(
  cat <<-END
Usage: $SCRIPT [<options>] <path>...
List top videos by size.

  -b            do not prepend size to output lines
  -e <ext>      video file extensions, separated by spaces, case-insensitive
                defaults to '$VIDEO_EXT'
  -n <num>      list top num video files
                defaults to '$NUM'
  -h            display this help and exit

Home page: <https://binac.org/posts/video-related-commands-and-scripts/>
END
)

error() { printf "%s\n" "$@" >&2; }

_exit() {
  error "$USAGE"
  exit 2
}

while getopts "bhe:n:" c; do
  case $c in
  b) BRIEF=true ;;
  e) VIDEO_EXT=$OPTARG ;;
  n) NUM=$OPTARG ;;
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

TMP_FILE=$(mktemp) || exit 1
trap 'rm -f "$TMP_FILE"' EXIT

grep_expression="$(echo " $VIDEO_EXT" | sed 's/ /\$|\\./g' | cut -c3-)$"
find "$@" -type f -exec du -h {} + | grep -i -E "$grep_expression" | sort -h -r >"$TMP_FILE"
count=$(wc -l <"$TMP_FILE" | xargs)

if [ "$count" -eq 0 ]; then
  error "No videos found"
  exit
else
  error "Detected $count videos"
fi

error "Top $(min "$NUM" "$count") videos by size:"
[ "$BRIEF" = true ] || printf "%-16s%s\n" "Size" "File"
head -n "$NUM" "$TMP_FILE" | while IFS= read -r line; do
  sz=$(echo "${line%%	*}" | xargs)
  f=${line#*	}
  [ "$BRIEF" = true ] && echo "$f" || printf "%-16s%s\n" "$sz" "$f"
done
