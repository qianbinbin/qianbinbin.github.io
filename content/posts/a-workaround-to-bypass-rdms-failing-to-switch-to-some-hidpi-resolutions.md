---
title: RDM 无法切换 HiDPI 分辨率的一种临时解决办法
date: 2020-11-08 16:47:24
tags:
- macOS
---

MacBook Pro 外接某 3200x1800 分辨率显示器，默认不开启 HiDPI。使用 [one-key-hidpi](https://github.com/xzhih/one-key-hidpi) 开启 1600x900 的 HiDPI 后，RDM 却无法切换到该分辨率，而且基本上 16:9 的都不行，比如 1080p、1440x810、720p。

<!-- more -->

## 问题原因

查看日志发现有：

```
错误	15:12:51.192852+0800	WindowServer	[ERROR] - CGXCompleteDisplayConfiguration: Monitor 3d005587; Mode -2147471360 not available
```

根据 SwitchResX FAQ[^1] 的说法，这是 macOS 的问题：

> This is a bug within macOS itself. macOS reports this resolution as available when building the resolution list, but doesn't let you select it. There's nothing to do at this point, and SwitchResX cannot avoid showing this resolution in its list.

简而言之就是 macOS 能够识别却不允许选择，老苹果了，又一次无情地牺牲了第三方配件的体验。

## 解决方法

one-key-hidpi 在开启 HiDPI 后，会创建

`/System/Library/Displays/Contents/Resources/Overrides/DisplayVendorID-vid/DisplayProductID-pid`：

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>DisplayProductName</key>
	<string>pn</string>
	<key>DisplayProductID</key>
	<integer>pid</integer>
	<key>DisplayVendorID</key>
	<integer>vid</integer>
	<key>scale-resolutions</key>
	<array>
		<data>AAAO/gAACHAAAAABACAAAA==</data>
		<data>AAAMfgAABwgAAAABACAAAA==</data>
		<data>AAALPgAABlQAAAABACAAAA==</data>
		<data>AAAJ/gAABaAAAAABACAAAA==</data>
	</array>
</dict>
</plist>
```

其中 vid 和 pid 是厂商和型号，不同的显示器会有区别。

`scale-resolutions` 包括了一系列所添加分辨率等信息的 base64 编码。

解决办法就是，将 HiDPI 分辨率减少或增加一个像素，使其不同于 macOS 识别到的。例如，添加 3198x1800，在使用 HiDPI 后就缩放到 1599x900。类似地，如需 1919x1080 的 HiDPI，只需添加 3838x2160 即可，其它依此类推。

上面 `scale-resolutions` 中的四个是我手动添加的，从上到下依次为：3838x2160、3198x1800、2878x1620、2558x1440。

使用在线工具，更为直观：[Scaled Resolutions for your MacBooks external monitor | by Comsysto Reply](https://comsysto.github.io/Display-Override-PropertyList-File-Parser-and-Generator-with-HiDPI-Support-For-Scaled-Resolutions/)

添加完成后重启，RDM 就可以成功识别并切换到对应的 HiDPI 分辨率了。

另外，通过修改 EDID 来增加分辨率应该也是可行的，不过比较麻烦，这里就不介绍了。

## 参考资料

[^1]:  [I want to use a HiDPI resolution that is half the native size of the monitor, but this doesn't work](https://www.madrau.com/support/support/faq_files/ns_I_want_to_use_a_HiDPI_resolutio.html)
