#!/usr/bin/env sh

ARGUMENTS=
CODEC_WHITELIST="hevc av1 vp9"
AVAILABLE_ENCODERS=$(ffmpeg -codecs 2>/dev/null | grep '^.\{8\}hevc' | sed -n 's/.*(encoders: \([^)]*\)).*/\1/p' | xargs)
ENCODER=libx265
OUTPUT_DIR=

SCRIPT=$(basename "$0")

USAGE=$(
  cat <<-END
Usage: $SCRIPT [<options>] <file>...
List top videos by size.

  -a <args>     pass extra arguments to ffmpeg
  -c <codecs>   videos with these codecs will NOT be encoded
                defaults to '$CODEC_WHITELIST'
  -e <encoder>  set HEVC encoder, this could accelerate transcoding but reduce the quality
                you may need to use '-a <args>'
                available encoders: $AVAILABLE_ENCODERS
                defaults to '$ENCODER'
  -o <dir>      set output directory
                by default, the output file will be placed in the same directory as the original video
  -h            display this help and exit

Home page: <https://binac.org/posts/video-related-commands-and-scripts/>
END
)

THIN_LINE=$(printf '%.s-' $(seq 1 80))
BOLD_LINE=$(printf '%.s=' $(seq 1 80))

error() { printf "%s\n" "$@" >&2; }

_exit() {
  error "$USAGE"
  exit 2
}

while getopts "ha:c:e:o:" c; do
  case $c in
  a) ARGUMENTS=$OPTARG ;;
  c) CODEC_WHITELIST=$OPTARG ;;
  e) ENCODER=$OPTARG ;;
  o) OUTPUT_DIR=$OPTARG ;;
  h) error "$USAGE" && exit ;;
  *) _exit ;;
  esac
done

contains() {
  echo "$1" | grep -qs "\<$2\>"
}

if ! contains "$AVAILABLE_ENCODERS" "$ENCODER"; then
  error "Invalid encoder: '$ENCODER'"
  _exit
fi

if [ -z "$ARGUMENTS" ] && [ "$ENCODER" = libx265 ]; then
  ARGUMENTS="-x265-params log-level=error"
fi

if [ -n "$OUTPUT_DIR" ]; then
  if [ ! -d "$OUTPUT_DIR" ] || [ ! -w "$OUTPUT_DIR" ]; then
    error "Cannot access: '$OUTPUT_DIR'"
    exit 1
  fi
  OUTPUT_DIR=$(realpath "$OUTPUT_DIR")
fi

shift $((OPTIND - 1))
[ $# -eq 0 ] && _exit

do_ffmpeg() {
  if [ -f "$2" ]; then
    error "File already exists: '$2'"
    return 0
  fi
  if ffmpeg -nostdin -v error -hide_banner -stats -i "$1" -map 0 -c copy -c:v:0 "$ENCODER" $ARGUMENTS -tag:v:0 hvc1 "$2"; then
    error "==> '$2'"
  else
    rm -f "$2"
    return 1
  fi
}

FAIL_LIST=$(mktemp) || exit 1
trap 'rm -f "$FAIL_LIST"' EXIT

count=1
total="$#"
for f in "$@"; do
  error "$THIN_LINE"
  error "==> $count/$total: '$f'"
  : $((count += 1))
  vc=$(ffprobe "$f" -v error -select_streams v:0 -show_entries stream=codec_name -of default=noprint_wrappers=1:nokey=1 | head -n 1)
  if [ -z "$vc" ]; then
    error "Unknown input: '$f'"
    echo "$f" >>"$FAIL_LIST"
    continue
  fi
  if contains "$CODEC_WHITELIST" "$vc"; then
    error "Skipping codec: '$vc'"
    continue
  fi

  if [ -n "$OUTPUT_DIR" ]; then
    output="$OUTPUT_DIR/$(basename "$f").HEVC.mp4"
  else
    output="$f.HEVC.mp4"
  fi
  do_ffmpeg "$f" "$output" && continue

  error "Unable to use MP4 container, retrying with MKV"
  output="${output%.*}.mkv"
  do_ffmpeg "$f" "$output" && continue

  error "Unable to use MKV container"
  error "Skipping file: '$f'"
  echo "$f" >>"$FAIL_LIST"
done

if [ -s "$FAIL_LIST" ]; then
  error "$BOLD_LINE"
  error "Unable to encode the following $(wc -l <"$FAIL_LIST" | xargs) files:"
  cat "$FAIL_LIST" >&2
  exit 1
fi
