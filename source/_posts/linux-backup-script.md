---
title: Linux 自动备份脚本
date: 2020-04-18 21:40:05
tags:
- Linux
- Bash
- Shell
---

写了个用于 Linux 备份的脚本。

<!-- more -->

## Bash 脚本

其中几个用于配置的常量：

- EXCLUDE
  所有不备份的绝对路径，以空格分隔，路径中不能包含空格等特殊字符

- FILE_NAME_PREFIX
  备份的文件名前缀

- FILE_NAME
  备份的文件名，默认是文件名前缀+时间

- TEMP_PATH
  临时文件存放路径

- BACKUP_PATH
  备份文件存放路径

- BACKUP_MAX
  保留备份文件的最大数目，超过时将清理旧备份

- COPY_TO_REMOTE
  是否复制到远程路径

- REMOTE_PATH
  需要保存的远程路径，例如使用 Rclone 挂载的路径

{% include_code lang:sh linux-backup-script/backup.sh %}

将脚本放到 `/root/bin/` 下并添加执行权限。

总体过程是，先用 `tar` 打包存放到 `TEMP_PATH`，再使用 `xz` 压缩为最终备份文件并存放到 `BACKUP_PATH`，如果超出最大数则清理旧备份，最后复制到远程目录。

## systemd 定时任务

将以下两个文件存放到 `/lib/systemd/system/` 下：

{% include_code linux-backup-script/backup.service %}

{% include_code linux-backup-script/backup.timer %}

这里设置为每周一 03:00 开始备份，`AccuracySec=1h` 表示可以有一小时的容错，这是为了减少唤醒 CPU。

运行并使其开机启动：

```sh
$ sudo systemctl daemon-reload
$ sudo systemctl start backup.timer
$ sudo systemctl enable backup.timer
```

`systemctl list-timers` 可以查看所有定时任务。
