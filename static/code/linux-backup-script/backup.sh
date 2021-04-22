#!/bin/bash

set -o pipefail

EXCLUDE="/boot /dev /lost+found /media /mnt /proc /run /sys /tmp /var/cache /var/tmp"

FILE_NAME_PREFIX="armbian-backup"
FILE_NAME="${FILE_NAME_PREFIX}_$(date +%Y%m%d-%H%M)"

TEMP_PATH="/var/tmp"
BACKUP_PATH="/mnt/sdcard/armbian-backup"
BACKUP_MAX=3
COPY_TO_REMOTE=true
REMOTE_PATH="/mnt/onedrive/armbian-backup"

log() {
  echo "$(date +%F-%T)" "$@"
}

[ "$(whoami)" != root ] && log "Please run as root" && exit 1

[ -d $TEMP_PATH ] || rm -f $TEMP_PATH && mkdir -p $TEMP_PATH && chmod 777 $TEMP_PATH
[ -d $BACKUP_PATH ] || rm -f $BACKUP_PATH && mkdir -p $BACKUP_PATH

check_space() {
  # KiB
  local avail
  avail=$(df -k "$1" | sed -n "2p" | awk '{print $4}')
  # reserve 512MiB
  [ $((avail - $2)) -gt $((1024 * 512)) ]
}

EXCLUDE_OPT=""
for f in $EXCLUDE; do EXCLUDE_OPT="$EXCLUDE_OPT--exclude=$f "; done
# shellcheck disable=SC2086
size=$(du -sh $EXCLUDE_OPT / | awk '{print $1}')
log "Total size is about: $size"
# shellcheck disable=SC2086
size=$(du -sk $EXCLUDE_OPT / | awk '{print $1}')

tmp_path=$TEMP_PATH
check_space $tmp_path "$size" || tmp_path=$BACKUP_PATH
if ! check_space $tmp_path "$size"; then
  log "Space not enough:"
  df -h $tmp_path
  exit 1
fi

TARBALL="$tmp_path/$FILE_NAME.tar"
log "Archiving to: $TARBALL"
# shellcheck disable=SC2086
tar -cpf - $EXCLUDE_OPT / | pv >"$TARBALL"
if [ $? -lt 2 ]; then
  log "Successfully saved to:" && ls -lh "$TARBALL"
else
  log "Error happened while archieving"
  rm -f "$TARBALL"
  exit 1
fi

echo
BACKUP="$BACKUP_PATH/$FILE_NAME.tar.xz"
log "Compressing to: $BACKUP"
size=$(du -sk "$TARBALL" | awk '{print $1}')
if dd if="$TARBALL" bs=8M status=none | pv -s "$size"K | xz -T0 >"$BACKUP"; then
  log "Successfully saved to:" && ls -lh "$BACKUP"
  log "Removing $TARBALL" && rm -f "$TARBALL"
else
  rm -f "$TARBALL"
  rm -f "$BACKUP"
  log "Failed to compress" && exit 1
fi

count=$(find $BACKUP_PATH -mindepth 1 -maxdepth 1 -type f -name "$FILE_NAME_PREFIX*.tar.xz" | wc -l)
for f in "$BACKUP_PATH"/"$FILE_NAME_PREFIX"*.tar.xz; do
  [ "$count" -le $BACKUP_MAX ] && break
  log "Removing old backup: $f" && rm -f "$f"
  ((count -= 1))
done

[ $COPY_TO_REMOTE != true ] && exit 0

echo
[ -d $REMOTE_PATH ] || rm -f $REMOTE_PATH && mkdir -p $REMOTE_PATH
size=$(du -sk "$BACKUP" | awk '{print $1}')
if ! check_space $REMOTE_PATH "$size"; then
  log "Space not enough:"
  df -h $REMOTE_PATH
  exit 1
fi

BACKUP_REMOTE="$REMOTE_PATH/$FILE_NAME.tar.xz"
log "Copying to remote: $BACKUP_REMOTE"
if pv "$BACKUP" >"$BACKUP_REMOTE"; then
  log "Successfully saved to:" && ls -lh "$BACKUP_REMOTE"
else
  log "Error happened while copying"
  rm -f "$BACKUP_REMOTE"
  exit 1
fi
