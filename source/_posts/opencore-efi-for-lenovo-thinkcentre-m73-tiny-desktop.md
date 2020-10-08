---
title: 自制 OpenCore EFI 让联想 M73 Tiny 吃上黑苹果
date: 2020-10-04 00:20:11
tags:
- Hackintosh
- macOS
- OpenCore
- 捡垃圾
---

看了[【图捡垃圾】90%人都够用的黑苹果小主机只要600块，附详细安装教程](https://www.bilibili.com/video/av499052441/)，忽然想到自己老本本里还有两条 DDR3 内存和祖传的 MLC 固态，而且视频里的 i3-4170 性能相当于 2020 年 MacBook Air 使用的 i3-1000NG4，上网办公完全够用，何不组一台家用上网机呢？

<!-- more -->

不过如果是全新装机的话，就不建议用这个平台了，8G 的 DDR3 比 DDR4 还贵，不值得。

实际去买的时候发现 JS 纷纷涨价。i3-4170 更夸张，视频发布时 180 的价格已经涨到了 250 以上，还不一定有货。

于是我从四代 CPU 中挑选了 i3-4330，两者性能相当，4330 频率略低、缓存略高，TDP 相同。但 4170 的 4400 核显需要进行额外的配置才能让 macOS 识别，否则可能会有奇怪的 bug，而 4330 的 4600 核显可以直接驱动，重要的是价格还没那么夸张。

选购 CPU 时，可以从同代里横向比较，综合考虑性能、价格和散热。我在实际使用时有风扇狂转的情况，小闷罐单热管还是压不住四代桌面 i3，打算以后有机会再加散热片。如果只是轻度上网办公，使用带 T 的 CPU 是个不错的主意。

然后就是自制 OpenCore EFI[^1] 了，理论上 4600 核显的四代 CPU 都能使用。过程很长，可以参考 GitHub 提交记录[^2]。

![](https://github.com/qianbinbin/hackintosh-m73-tiny/raw/macOS-Catalina-10.15/images/screenshot-20201002-22.29.51.png)

## 规格

之所以列出规格，是因为不同的型号和 BIOS 版本可能会不适用。EFI 不保证兼容性，使用者后果自负。

| | |
|-|-|
| 产品序列号 | PC07S4KD |
| 机器型号 | 10AXA2EJJP |
| 详细规格 | <https://support.lenovo.com/us/en/solutions/PD029621> |
| BIOS 版本 | [FHKT85A 23 Jun 2020](https://pcsupport.lenovo.com/us/en/products/desktops-and-all-in-ones/thinkcentre-m-series-desktops/thinkcentre-m73/10ax/10axa2ejjp/pc07s4kd/downloads/DS038325) |
| CPU | [Intel® Core™ i3-4330 Processor](https://ark.intel.com/content/www/us/en/ark/products/77769/intel-core-i3-4330-processor-4m-cache-3-50-ghz.html) |
| 显卡 | Intel® HD Graphics 4600 |
| OpenCore 版本 | 0.6.1 |
| macOS 版本 | Catalina 10.15 |

## 使用方法

1. [创建 USB](https://dortania.github.io/OpenCore-Install-Guide/installer-guide/)。

2. 挂载 USB 中的 EFI 分区（连接电脑时会自动挂载），删除其中所有文件，并把 [EFI 文件夹](https://github.com/qianbinbin/hackintosh-m73-tiny/tree/macOS-Catalina-10.15/BOOT/EFI) 复制进去。

   名为 `BOOT` 的 EFI 分区结构应该形如：

   ```
   BOOT
   └── EFI
       ├── BOOT
       │   └── BOOTx64.efi
       └── OC
           ├── ACPI
           ├── Bootstrap
           ├── Drivers
           ├── Kexts
           ├── OpenCore.efi
           ├── Resources
           ├── Tools
           └── config.plist
   ```

3. 以 `iMac14,1` [生成 SMBIOS 信息](https://dortania.github.io/OpenCore-Install-Guide/config-laptop.plist/haswell.html#platforminfo)，并在 `config.plist` 中设置 `SystemSerialNumber`、`MLB` 和 `SystemUUID` 的值。

4. 如有必要，更新 BIOS 版本。

5. [配置 BIOS](https://dortania.github.io/OpenCore-Install-Guide/config-laptop.plist/haswell.html#intel-bios-settings)。

6. 从 USB 启动，安装 macOS。

7. 可选地加入适当的 [Kexts 文件](https://dortania.github.io/OpenCore-Install-Guide/ktext.html#wifi-and-bluetooth) 来驱动 Intel 无线网卡或非苹果的博通无线网卡。

## 参考资料

[^1]: [OpenCore Install Guide](https://dortania.github.io/OpenCore-Install-Guide/)

[^2]: [qianbinbin/hackintosh-m73-tiny: OpenCore EFI for Lenovo ThinkCentre M73 Tiny Desktop](https://github.com/qianbinbin/hackintosh-m73-tiny)
