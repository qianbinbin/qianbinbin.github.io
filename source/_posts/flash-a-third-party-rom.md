---
title: Android 手机刷第三方 ROM
date: 2018-04-11 00:30:57
tags:
- Android
---

# 解锁 Bootloader

参考各机型官网。

# 刷第三方 Recovery

[TeamWin - TWRP](https://twrp.me/)

已 Root 的设备可以安装官方 APP。
已安装旧版本 TWRP 的设备可直接进入 Recovery 刷`.img`文件；
已解锁的设备可通过`fastboot`刷`.img`文件。

```sh
$ adb reboot bootloader
$ fastboot flash recovery twrp.img
$ fastboot reboot
```

许多机型第一次刷 TWRP 后需要先手动重启到 Recovery 一次，否则启动系统后会被替换为官方 Recovery。

<!-- more -->

# 退出 Google 账号，去除锁屏

先退出 Google 账号是为了在刷机后开机向导跳过登录 Google 账号，除非翻墙。如果不是在墙外，各种奇技淫巧均不推荐。

将锁屏改为无或者滑动，否则一些 TWRP 版本可能无法解密 data 分区。

# 备份（可选）

重启进入 TWRP，自带的 backup 功能进行备份，但注意无法备份默认目录下的媒体文件等，如照片、音乐。

# 下载 ROM

[LineageOS – LineageOS Android Distribution](https://lineageos.org/)
[Download center | Pixel Experience](https://download.pixelexperience.org/)
[Android Forum for Mobile Phones, Tablets, Watches & Android App Development - XDA Forums](https://forum.xda-developers.com/)

# 下载 SuperSU

[SuperSU HomePage](http://www.supersu.com/)

# 刷机

1. 将 ROM 和 SuperSU 复制到`/sdcard/`下
2. 清除 system 分区，如果之前不是同一个 ROM，还需要清除 data 分区
3. 刷 ROM、SuperSU
4. 清除 cache、dalvik cache

# 交换左右按键

[交换小米 Xiaomi Mi 6 (Sagit) 的返回键和多任务键/应用切换键 | Binac](https://binac.io/2018/03/12/switch-back-key-and-app-switch-key-of-xiaomi-mi6-sagit/)

# 修改网络检测 URL

```sh
$ adb shell "settings put global captive_portal_https_url https://connect.rom.miui.com/generate_204"
```

URL 可自选：
[Generate 204 - Linfinite](https://cli.ee/generate-204)
