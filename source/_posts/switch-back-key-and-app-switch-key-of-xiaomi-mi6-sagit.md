---
title: 交换小米 Xiaomi Mi 6 (Sagit) 的返回键和多任务键/应用切换键
date: 2018-03-12 18:17:12
tags:
- Android
- Xiaomi
---

Android 原生设计是返回键在左侧，多任务键在右侧，然而很多厂商喜欢反过来，理由是返回键放到右侧更便于右手操作。

小米 6 就是如此，这让作为原生党的我很不习惯。本文介绍如何交换小米 6 的返回键和多任务键。

<!-- more -->

我的 ROM 是 Pixel Experience，官方 ROM 应该也一样。

一般修改按键是修改`/system/usr/keylayout/Generic.kl`文件，但是我修改了没有效果，同目录下的`qwerty.kl`也无效。

摸索一番后发现，实际上小米 6 要修改的是`/system/vendor/usr/keylayout/synaptics_dsx.kl`：

```
# Copyright (c) 2015, The Linux Foundation. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above
#       copyright notice, this list of conditions and the following
#       disclaimer in the documentation and/or other materials provided
#       with the distribution.
#     * Neither the name of The Linux Foundation nor the names of its
#       contributors may be used to endorse or promote products derived
#       from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
# ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
# BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
# BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
# OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
# IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

key 139    APP_SWITCH    VIRTUAL
key 158    BACK          VIRTUAL
key 143    WAKEUP
```

代码为 139 的是多任务键，158 的是返回键，直接把两者交换即可，像这样：

```
key 139    BACK          VIRTUAL
key 158    APP_SWITCH    VIRTUAL
```

你也可以在原来的前面添加`#`注释掉，以便随时改回来。

可能有人不知道如何修改这个文件，

1. 如果你刷了 TWRP，可以直接进入 recovery，挂载 system 分区，连接 USB 后`adb shell`进去，用 vi 编辑器即可直接修改，虽然这个 vi 是半残，但是简单的修改没问题
2. 如果你已经 root，下载一个支持 root 模式的文件管理器，挂载 system 分区为读写，然后就可以编辑了，我推荐使用 X-plore，Symbian 时代就流行的文件管理器，树形管理界面，功能强大，免费无广告，把什么 ES 之流吊着打，那玩意已经变味了
3. 同样如果你已经 root，连接 USB 后`adb pull`把文件拖出来，在电脑上改完后再`adb push`进去

改完重启系统。
