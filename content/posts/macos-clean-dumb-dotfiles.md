---
title: "macOS 清除 ._*、.DS_Store 隐藏垃圾文件"
date: 2021-05-11T23:25:01+08:00
tags:
- macOS
- Shell
---

众所周知，macOS 会自作聪明地生成一些隐藏的垃圾文件。

想出这个主意的人真是个鬼才，反正只要表面好看就行，全然不顾那些希望高度把控系统的强迫症患者，更别说泄露隐私的风险了。

<!-- more -->

垃圾文件主要是 `._*` 和 `.DS_Store`，对于前者，可以用 macOS 自带的 `dot_clean` 工具清除（看来苹果自己也知道这是垃圾文件啊），对于后者，只能用 `find` 命令查找删除了。

为此写了一个简单的脚本：

{{< code lang=sh file=macos-clean-dumb-dotfiles/clean >}}

只需在 `clean` 后加一个或多个路径，即可递归地清除这些路径下的垃圾文件，例如 `clean /Users /Volumes`。

要在 macOS 上设置定时任务，还得使用蛋疼的 launchd。假设脚本路径为 `/Users/username/.local/bin/clean`，

每周日 20:30 清除整个系统：

{{< code lang=xml file=macos-clean-dumb-dotfiles/clean-system.plist >}}

除周日外每天 20:30 清除用户目录和挂载目录：

{{< code lang=xml file=macos-clean-dumb-dotfiles/clean-users.plist >}}

把以上 plist 文件放在 `/Library/LaunchDaemons` 下，然后：

```sh
sudo launchctl load /Library/LaunchDaemons/clean-system.plist
sudo launchctl load /Library/LaunchDaemons/clean-users.plist
```
