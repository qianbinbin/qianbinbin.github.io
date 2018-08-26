---
title: Linux 折腾手记
date: 2018-08-07 20:32:29
tags:
- Linux
- VPS
- Android
---

# 常用配置


## SSH 配置


### 修改 SSH 端口

在文件`/etc/ssh/sshd_config`中把`Port`值改为其他端口（如无此选项则手动添加），注意不要冲突：

```
Port 12345
```

修改后以`-p`参数指定端口号登录，例如：

```sh
ssh user@ip -p 12345
```

<!-- more -->


### 禁止 root 用户登录

添加其他用户并确保可以登录，在文件`/etc/ssh/sshd_config`中把`PermitRootLogin`值修改为`no`（如无此选项则手动添加）:

```
PermitRootLogin no
```

其他可选值可参考：[sshd\_config 中 PermitRootLogin 的探讨](https://blog.csdn.net/huigher/article/details/52972013)



### 公钥认证免密码登录

将客户端公钥（默认在`~/.ssh/id_rsa.pub`，如没有则需重新生成）复制到服务端`~/.ssh/authorized_keys`文件中（如没有则新建），`authorized_keys`文件权限应为`600`。


# 科学上网：Shadowsocks

Python 版本：<https://github.com/shadowsocks/shadowsocks>

C 语言版本：<https://github.com/shadowsocks/shadowsocks-libev>

我用的是 C 语言版本，如需支持多用户，则应使用`ss-manager`命令。

建议把默认端口改掉。

建议为 Shadowsocks 专门新建用户来运行。

自定义 Shadowsocks-libev 的 systemd 脚本可编辑：`/usr/lib/systemd/system/shadowsocks-libev.service`


# 网络加速：Google BBR 拥塞控制算法

Linux Kernel 4.9 已经自带算法，此内核及以上版本的无需安装。

KVM 架构的 VPS 可参考 [安装 Google BBR 加速VPS网络](http://blog.leanote.com/post/quincyhuang/google-bbr) 升级内核。

OpenVZ 架构的 VPS 无法升级内核，可参考 [OpenVZ 平台 Google BBR 一键安装脚本](https://blog.kuoruan.com/116.html)，建议为其专门新建用户运行。

安装后科学上网可跑满带宽。


# 打造离线下载服务器


## HTTP 下载：aria2

速度快，支持 RPC，客户端可以很方便地远程管理下载任务。

安装及配置略。

systemd 启动脚本可参考：
<https://github.com/qianbinbin/backup/blob/master/redhat/usr/lib/systemd/system/aria2.service>

SysV 启动脚本可参考：
<https://github.com/qianbinbin/backup/blob/master/debian/etc/init.d/aria2>

建议专门新建用户运行。

Web 管理工具建议使用：[YAAW by binux](http://binux.github.io/yaaw/)


## BT 下载：Transmission

支持 RPC，自带 Web 管理界面，默认端口为 9091，访问`http://ip:9091`即可访问。

安装及配置略。

安装后一般自带 SysV 或 systemd 脚本，无需自己编写。

建议专门新建用户运行。


## 电驴下载：MLDonkey

软件较为陈旧，自带的 Web 管理界面很不友好，且现在 BT 更为流行。不建议折腾。


# 文件共享：Samba

有时我们需要将目录共享，以便在客户端访问，Samba 支持 Linux、Windows、macOS 等平台。

安装及配置略。默认的 445 端口可能被屏蔽，如无法使用可修改为其他端口。


# Web 服务：Nginx

利用 Web 服务，可以将离线下载的文件通过 HTTP 下载到本地。


# 权限管理

1. 新建用户组，例如`download`
2. 统一设置下载目录，例如`/home/user/Downloads`，将其用户组改为`download`，权限改为`775`
3. 将 aria2、Transmission、MLDonkey、Nginx 的用户均加入`download`用户组


# 将 Android 手机打造成 Linux 服务器

通过 Linux Deploy 可以在 Android 上安装各种 Linux 发行版，如 Debian、Ubuntu、Arch Linux 等，它提供了一个 chroot 环境，Linux 与 Android 系统一起运行（共享内核），是我目前用过同类软件中最好的。
<https://play.google.com/store/apps/details?id=ru.meefik.linuxdeploy>
<https://github.com/meefik/linuxdeploy>

上面的程序都可以在 Android 手机上跑，完全可以当作树莓派或者小型 Linux 服务器来用。

需要注意的坑：

1. Android 对文件管理、网络等采用了特殊的权限管理，如果在 Linux 中新建用户需要访问这些资源，则需要将用户加入用户组，例如存储读写用户组`aid_sdcard_rw`，网络访问用户组`aid_net_raw`
2. 如果遇到奇怪的问题，建议将 Linux Deploy 详细的 log 打开，比如我遇到一个问题，Linux 安装后无法运行，默认 log 没有任何有意义的提示，而实际上是因为手机内核版本太低，此发行版不支持


## 内网穿透：frp

家用宽带可能没有固定的公网 IP，外网无法直接访问，此时需要内网穿透，Ngrok 虽好但比较繁琐，我使用的是 frp：
<https://github.com/fatedier/frp>

建议专门新建用户运行。

附：
SSH 默认端口：22
aria2 默认端口：6800
Transmission 默认端口：9091
Samba 默认端口：445
以上均使用 TCP 协议。
