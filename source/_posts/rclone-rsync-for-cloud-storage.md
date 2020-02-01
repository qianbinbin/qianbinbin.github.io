---
title: 使用 Rclone 挂载网盘
date: 2020-02-01 16:08:53
tags:
- Linux
---

在没有图形界面的服务器上，或不想使用客户端软件时，如何方便地使用网盘是个问题。

Rclone 就是为此而生的，它可以方便在命令行下挂载网盘，目前已经支持包括 Google Drive、OneDrive 在内的几十个网盘，而且除了速度慢一点外，可以模拟本地磁盘，对于容量有限的 VPS 来说就是免费扩容，配合 aria2、Transmission 等工具，就可以打造离线下载服务器。

<!-- more -->

# 安装

Debian 的官方源中 Rclone 版本过于陈旧，建议到官网下载：<https://rclone.org/downloads/>

或者脚本一键安装：

```shell
$ curl https://rclone.org/install.sh | sudo bash
```

# 配置

Rclone 提供了简单的交互式配置方式，运行 `rclone config` 即可，一般就是打开浏览器获取一个 token，网上有很多教程，在此就不赘述了。

以 OneDrive for Business 为例，主要说明一下如何在没有图形界面的情况下获取 token。

一种是先用其他带图形界面的系统配置一次，直接把 token 复制过去。

另一种就是在命令行下执行到这一步时：

```shell
Use auto config?
 * Say Y if not sure
 * Say N if you are working on a remote or headless machine
y) Yes
n) No
y/n> y
If your browser doesn't open automatically go to the following link: http://127.0.0.1:53682/auth?state=xxxxxxxxxxxxxxxxxxxxxx
Log in and authorize rclone for access
Waiting for code...
```

选择 `y` 的话理论上会打开浏览器访问那个地址，当然由于是服务器不可能打开浏览器，这时我们重开一个 SSH 连接，并用 `curl` 访问：

```shell
$ curl http://127.0.0.1:53682/auth?state=xxxxxxxxxxxxxxxxxxxxxx
<a href="https://login.microsoftonline.com/common/oauth2/v2.0/authorize?access_type=offline&amp;client_id=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&amp;redirect_uri=http%3A%2F%2Flocalhost%3A53682%2F&amp;response_type=code&amp;scope=Files.Read+Files.ReadWrite+Files.Read.All+Files.ReadWrite.All+offline_access&amp;state=xxxxxxxxxxxxxxxxxxxxxx">Temporary Redirect</a>.
```

复制 `href` 中的链接，对其 URL 解码，可以使用 Python 或其它工具，如：<https://tool.chinaz.com/tools/urlencode.aspx>

接着（客户端）使用浏览器访问解码后的 URL 并登陆账号，然后会重定向到一个本地 URL，在服务器上用 `curl` 访问此 URL。

这时 Rclone 的配置应该会自动进行到下一步，其它照常配置即可。如果不行就多试几次。

# 挂载

安装 FUSE：

```shell
$ sudo apt install fuse
```

将名为 `onedrive` 的配置挂载到 `/mnt/onedrive` 下：

```shell
$ rclone mount onedrive:/ /mnt/onedrive --vfs-cache-mode full --vfs-cache-max-age 24h --vfs-cache-max-size 12G
```

其中 `vfs-cache-mode` 可选参数为 `off|minimal|writes|full`，默认为 `off`，写入文件时无法随机寻址，因此无法用于 aria2、Transmission 等分块下载软件。`writes` 或 `full` 则可以很好地模拟本地文件系统，`writes` 模式下只读打开的文件无缓存，`full` 模式下读写均有缓存。

`vfs-cache-max-age` 设置缓存保存时间，`vfs-cache-max-size` 设置缓存大小。

参数 `-vv` 可查看 log 以排查错误，`--daemon` 以守护进程运行等。更多可参考：<https://rclone.org/commands/rclone_mount/>

查看一下是否已经挂载：

```shell
$ df -h
Filesystem      Size  Used Avail Use% Mounted on
onedrive:       5.0T   18G  5.0T   1% /mnt/onedrive
```

# 开机自启

systemd 脚本：

```shell
$ cat /lib/systemd/system/rclone.service
[Unit]
Description=Rclone Service
After=network.target

[Service]
Type=simple
User=username
ExecStart=/usr/bin/rclone mount onedrive:/ /mnt/onedrive --vfs-cache-mode full --vfs-cache-max-age 24h --vfs-cache-max-size 12G
ExecStopPost=/bin/fusermount -qzu /mnt/onedrive

[Install]
WantedBy=multi-user.target
```

其中 `User` 设置为所运行的用户。

我实际使用时，停止进程后无法自动取消挂载，文件夹异常，因此用 `fusermount -qzu /mnt/onedrive` 取消挂载。

另外，开机时 Transmission 可能会在 Rclone 之前运行，此时目录未挂载，Transmission 就会出错，于是修改其 systemd 脚本：

```shell
$ vim /lib/systemd/system/transmission-daemon.service
```

将 `After=network.target` 修改为 `After=network.target rclone.service`，这样 Transmission 就在 Rclone 后启动。
