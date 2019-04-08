---
title: macOS Mojave 上找不到 C 语言头文件
date: 2019-04-08 15:16:28
tags:
- C
---

从 Mojave 开始，macOS 就不再默认将 C 语言头文件安装到 `/usr/include/` 下了，因此编译时会找不到头文件。

临时解决方法：

```sh
open /Library/Developer/CommandLineTools/Packages/macOS_SDK_headers_for_macOS_10.14.pkg
```

这个兼容包可将头文件安装到 `/usr/include/` 下，但将来可能不再提供。


# 参考资料

[build fails with OSX Mojave · Issue #9050 · neovim/neovim](https://github.com/neovim/neovim/issues/9050#issuecomment-424417456)
