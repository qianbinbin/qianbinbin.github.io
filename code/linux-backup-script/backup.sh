#!/usr/bin/env bash

set -o pipefail

EXCLUDE="/dev /lost+found /media /mnt /proc /run /sys /tmp /var/cache /var/tmp /root/.cache"

FILE_NAME_PREFIX="debian"
FILE_NAME="${FILE_NAME_PREFIX}_$(date +%Y%m%d-%H%M)"

TEMP_PATH="/tmp"
BACKUP_PATH="/mnt/hdd/Backups"
BACKUP_MAX=5

COPY_TO_REMOTE=false
REMOTE_PATH="/mnt/onedrive/debian-backup"

log() {
  while read -r; do
    echo "[$(date +%F-%T)] $REPLY"
  done
}

# CHECK

[ "$(whoami)" != root ] && echo "Please run as root" | log && exit 1

[ -d $TEMP_PATH ] || (rm -f $TEMP_PATH && mkdir -p $TEMP_PATH)

[ -d $BACKUP_PATH ] || (rm -f $BACKUP_PATH && mkdir -p $BACKUP_PATH)

check_space() {
  local avail
  avail=$(df -k "$1" | sed -n "2p" | awk '{ print $4 }')
  # reserve 512MiB
  [ $((avail - $2)) -gt $((1024 * 512)) ]
}

EXCLUDE_OPT=""
for f in $EXCLUDE; do EXCLUDE_OPT="$EXCLUDE_OPT--exclude='$f' "; done

_du="du -sh $EXCLUDE_OPT /"
size=$(eval "$_du" | awk '{ print $1 }')
echo "Total size: $size" | log
_du="du -sk $EXCLUDE_OPT /"
size=$(eval "$_du" | awk '{ print $1 }')

tmp_path=$TEMP_PATH
if ! check_space $tmp_path "$size"; then
  echo "No enough space: $tmp_path" | log
  echo "Using: $BACKUP_PATH" | log
  tmp_path=$BACKUP_PATH
  if ! check_space $tmp_path "$size"; then
    echo "No enough space: $tmp_path" | log
    df -h $tmp_path | log
    exit 1
  fi
fi

# ARCHIVE

TARBALL="$tmp_path/$FILE_NAME.tar"
_tar="tar -cpf '$TARBALL' $EXCLUDE_OPT /"
echo "+ $_tar" | log
if (eval "$_tar" 2>&1) | log; then
  echo "Successfully saved as:" | log
  ls -lh "$TARBALL" | log
else
  echo "Failed to archive" | log
  rm -f "$TARBALL"
  exit 1
fi

# COMPRESS

BACKUP="$BACKUP_PATH/$FILE_NAME.tar.xz"
_xz="xz '$TARBALL' -T 0 -M 80% -c >'$BACKUP'"
echo "+ $_xz" | log
if eval "$_xz"; then
  echo "Successfully saved as:" | log
  ls -lh "$BACKUP" | log
  echo "Removing: $TARBALL" | log
  rm -f "$TARBALL"
else
  echo "Failed to compress" | log
  echo "Removing: $TARBALL" | log
  rm -f "$TARBALL"
  echo "Removing: $BACKUP" | log
  rm -f "$BACKUP"
  exit 1
fi

# REMOVE OUTDATED

count=$(find "$BACKUP_PATH" -maxdepth 1 -name "$FILE_NAME_PREFIX*.tar.xz" | wc -l)
for f in "$BACKUP_PATH/$FILE_NAME_PREFIX"*.tar.xz; do
  [ "$count" -le $BACKUP_MAX ] && break
  echo "Removing outdated: $f" | log
  rm -f "$f"
  ((count -= 1))
done

# COPY

[ $COPY_TO_REMOTE != true ] && exit 0

[ -d $REMOTE_PATH ] || (rm -f $REMOTE_PATH && mkdir -p $REMOTE_PATH)
size=$(du -sk "$BACKUP" | awk '{ print $1 }')
if ! check_space $REMOTE_PATH "$size"; then
  echo "No enough space:" | log
  df -h $REMOTE_PATH | log
  exit 1
fi

BACKUP_REMOTE="$REMOTE_PATH/$FILE_NAME.tar.xz"
echo "Copying to remote: $BACKUP_REMOTE" | log
if cp "$BACKUP" "$BACKUP_REMOTE" 2>&1 | log; then
  echo "Successfully saved as:" | log
  ls -lh "$BACKUP_REMOTE" | log
else
  echo "Failed to copy" | log
  rm -f "$BACKUP_REMOTE"
  exit 1
fi
