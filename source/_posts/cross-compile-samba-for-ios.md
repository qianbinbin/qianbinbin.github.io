---
title: 移植 Samba 到 iOS 平台
date: 2019-09-08 18:48:08
tags:
- iOS
- 交叉编译
---

# 前言

iOS 越狱后可以像树莓派那样作为迷你服务器，在上面搭建 aria2、Transmission 等。但是 Cydia 上的 Samba 非常古老，而且只能在 32 位 CPU 上运行。

于是我开始寻找替代方案：

1. 用其他协议，例如 ftp、nfs 等
2. 用 web 文件管理器
3. 从源码交叉编译 Samba
4. 在 iOS 端用 Cydia 上的 LLVM 工具链从源码编译 Samba

其中 ftp 似乎对流媒体支持并不好，nfs 有个开源项目 NFS-Ganesha，但是交叉编译依赖难以处理。经过一番挣扎后，这种方案被放弃了。

Web 服务器的话，Cydia 上也没有 Nginx，PHP 倒是有，主要担心的是即使服务器搭好了，搭建前端又要处理一堆依赖。而且用浏览器进行文件管理终究不爽。

后来尝试了一个用 Go 语言编写的 web 文件管理器 [File Browser](https://filebrowser.xyz/)，交叉编译后发现可以运行，但浏览器打开就是不能使用。给他们提了 issue，一直没有回复，这个项目在招募维护者，看样子不会很快处理了。

Samba 如果能跑当然是最好的方案了，但是用官方的最新源码按照[官方文档](https://wiki.samba.org/index.php/Waf)中的交叉编译方法来编译的话，是无法编译到 iOS 平台的。如果想尝试建议不要浪费这个时间。iOS 端直接编译也尝试过，就算忽略孱弱的性能，configure 过程死活要尝试运行测试程序，而 iOS 端都必须签名才能运行。

[iOS开发之macOS下kxsmb编译smb类库-天狐博客](http://www.skyfox.org/ios-macos-kxsmb-smb-build.html) 中的方法是编译出几个特定的类库，我将编译脚本中的编译命令改为`make`后编译出错，报什么错已经忘了，后来没有再尝试，感觉上应该可行。

最后我找了一个比较老的 4.0.8 版本，磕磕绊绊后终于成功了，虽然`nmbd`还是无法运行，但`smbd`可以跑了，已经可以满足我的需求。

# 编译过程

下载源码：

```sh
wget https://download.samba.org/pub/samba/stable/samba-4.0.8.tar.gz
tar zxf samba-4.0.8.tar.gz
```

进入 source3 目录，而不是源码根目录：

```sh
cd samba-4.0.8/source3
```

之所以用老版本，是因为比较新的几个版本 source3 目录下是没有 configure 或 autogen.sh 的，而根目录下的 configure 文件无法正确配置 iOS 的交叉编译环境。

新建一个文件 cache-file.cache 用于保存配置的变量：

```
samba_cv_big_endian=no
samba_cv_little_endian=yes
samba_cv_CC_NEGATIVE_ENUM_VALUES=yes
libreplace_cv_HAVE_GETADDRINFO=no
samba_cv_HAVE_WRFILE_KEYTAB=no
smb_krb5_cv_enctype_to_string_takes_krb5_context_arg=no
smb_krb5_cv_enctype_to_string_takes_size_t_arg=yes
ac_cv_file__proc_sys_kernel_core_pattern=yes
```

这个文件 configure 后会被脚本改写。

运行配置脚本：

```sh
./configure CC=$(xcrun -sdk iphoneos -find clang) LD=$(xcrun -sdk iphoneos -find ld) AR=$(xcrun -sdk iphoneos -find ar) CFLAGS="-isysroot $(xcrun --sdk iphoneos --show-sdk-path) -target arm64-apple-darwin -arch arm64" LDFLAGS="-isysroot $(xcrun --sdk iphoneos --show-sdk-path) -arch arm64" --host=arm-apple-darwin --cache-file=cache-file.cache
```

其他编译参数可以 `./configure --help` 查看。例如不设置 `prefix` 参数的话，默认安装路径就是 `/usr/local/samba`。

但是生成的配置还有一些不正确，可以手动修改 `include/autoconf/config.h`：

- `#define HAVE_YP_GET_DEFAULT_DOMAIN 1`注释掉，否则编译出错
- `SIZEOF_BLKCNT_T_8`宏取消注释，改为`#define SIZEOF_BLKCNT_T_8`，否则编译出错
- `USE_SETREUID`宏取消注释，改为`#define USE_SETREUID 1`，否则运行出错，需要查看 log 才能发现

这个时候就可以 `make -j8` 了。但是编译仍然会出错，主要是 iOS 修改了 API。一个是缺少 `crt_externs.h` 头文件，还有一个 `system(cmd)` 改为 `popen(cmd, "r")`，具体解决方法可以看编译 kxsmb 那篇文章。

编译很快，完成后新建一个目录：

```shell
mkdir -p build-ios/usr/local/samba
```

然后安装

```shell
make install prefix=/path/to/build-ios/usr/local/samba
```

DESTDIR 变量是不起作用的，这个要注意，一不小心安装到本机上就难受了。

其中 `bin`、`include`、`lib`、`sbin` 等文件夹均不在常见的系统默认路径中，安装到 iOS 的话还要手动添加，我索性把这几个目录直接放到 `usr/local` 下了。

不要忘了给二进制文件签名，包括 `bin`、`sbin` 下的可执行文件和 `lib` 下的库文件：

```shell
codesign --force --sign xxx --entitlements /path/to/ent.xml file-to-sign
```

`sign` 参数可以通过 `security find-identity -v -p codesigning` 来获取。

然后再用 `dpkg` 打个包，方便 iOS 端安装卸载。在 `build-ios` 目录下新建 `DEBIAN` 文件夹，并在其中新建 `control` 文件，内容如下：

```
Package: samba
Version: 4.0.8
Priority: optional
Section: Networking
Maintainer: MaintainerName
Author: AuthorName
Description: Windows (SMB/CIFS) filesharing and networking
Architecture:iphoneos-arm
```

然后：

```shell
dpkg -b build-ios samba_4.0.8-0_arm64.deb
```

然后 `scp` 到设备上安装。

仍然无法运行，查看 log 发现有文件权限问题、缺少配置文件等，这些都容易解决，就不赘述了。

再说说 `USE_SETREUID` 这个宏，如果不设置的话，`smbd` 就会出错：

```
[2019/09/08 02:22:50.992604,  0] lib/util_sec.c:121(assert_gid)
  Failed to set gid privileges to (-1,501) now set to (0,0) uid=(0,0)
[2019/09/08 02:22:50.994642,  0] lib/util.c:810(smb_panic_s3)
  PANIC (pid 1150): failed to set gid
```

查看 `lib/util_sec.c` 文件 `gain_root_group_privilege` 函数，没时间看整个模块的逻辑（而且代码写得没有让人看的欲望……），从这个函数来看是在设置 gid 时出错，定位函数中的几个宏发现是没有找到合适的系统函数，但在 iOS API 中是存在的，在 `include/autoconf/config.h` 中改一下，重新编译就可以了。其他几个类似的宏如果系统也有对应函数接口的话应该也可行，懒得尝试了。

最重要的是 `smbpasswd` 和 `smbd` 都可以运行了。

`nmbd` 无法使用，查看其 log 发现是在 `../lib/socket/interfaces.c` 中 `get_interfaces` 函数调用 `TYPESAFE_QSORT` 产生的，这个宏包装了 `qsort`，然后对前两个元素大小进行 `assert`，逻辑上没什么问题，怀疑是 `compare` 函数有问题，但是实在不想看了。`get_interfaces` 这个函数主要逻辑就是将一个数组排序，然后去重，但是去重算法写成这样真让人头大：

```c
/* this wrapper is used to remove duplicates from the interface list generated
   above */
int get_interfaces(TALLOC_CTX *mem_ctx, struct iface_struct **pifaces)
{
        struct iface_struct *ifaces;
        int total, i, j;

        total = _get_interfaces(mem_ctx, &ifaces);
        if (total <= 0) return total;

        /* now we need to remove duplicates */
        TYPESAFE_QSORT(ifaces, total, iface_comp);

        for (i=1;i<total;) {
                if (iface_comp(&ifaces[i-1], &ifaces[i]) == 0) {
                        for (j=i-1;j<total-1;j++) {
                                ifaces[j] = ifaces[j+1];
                        }
                        total--;
                } else {
                        i++;
                }
        }

        *pifaces = ifaces;
        return total;
}
```

还好 `nmbd` 对我来说没什么卵用，忽略了。

# 参考资料

1. [iOS开发之macOS下kxsmb编译smb类库-天狐博客](http://www.skyfox.org/ios-macos-kxsmb-smb-build.html)
2. [MINI2440 samba移植笔记 - 且行且珍惜 - CSDN博客](https://blog.csdn.net/kingdragonfly120/article/details/10044605)
