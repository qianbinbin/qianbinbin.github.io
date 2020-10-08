---
title: 交叉编译 Go 语言项目 frp for iOS
date: 2019-09-03 18:52:33
tags:
- iOS
- Go
- 交叉编译
---

首先安装 Go 语言环境，然后下载 frp 源码：

```shell
git clone git@github.com:fatedier/frp.git
cd frp
```

Go 的交叉编译比 GCC、Clang 方便很多，只需要设置几个参数：

```shell
CC=$GOPATH/misc/ios/clangwrap.sh CGO_ENABLED=1 GOOS=darwin GOARCH=arm64 go build -o ./frpc-darwin-arm64 ./cmd/frpc
CC=$GOPATH/misc/ios/clangwrap.sh CGO_ENABLED=1 GOOS=darwin GOARCH=arm64 go build -o ./frps-darwin-arm64 ./cmd/frps
```

`GOOS` 参数没什么疑问，`GOARCH` 不是太古老的设备一般都是 `arm64`。

`CC` 一定要设置正确，`CGO_ENABLED` 不能像其他平台那样设置为 `0`，否则会有类似这样的错误：

```shell
# github.com/fatedier/frp/cmd/frps
$GOPATH/pkg/tool/darwin_amd64/link: running /misc/ios/clangwrap.sh failed: fork/exec /misc/ios/clangwrap.sh: no such file or directory
```

如果遇到网络问题：

```shell
go: golang.org/x/net@v0.0.0-20190724013045-ca1201d0de80: unrecognized import path "golang.org/x/net" (https fetch: Get https://golang.org/x/net?go-get=1: dial tcp 216.239.37.1:443: i/o timeout)
go: golang.org/x/text@v0.3.2: unrecognized import path "golang.org/x/text" (https fetch: Get https://golang.org/x/text?go-get=1: dial tcp 216.239.37.1:443: i/o timeout)
go: error loading module requirements
```

说明是被墙了。代理方法没用，VPN 应该有效。

建议直接下载 Github 上的镜像源码放到对应目录，如：

```shell
git clone git@github.com:golang/text.git $GOPATH/src/golang.org/x/text
```

然后重新编译即可。别忘了将编译生成的二进制文件进行签名。

## 参考资料

1. [一键解决 go get golang.org/x 包失败 - 格物](https://shockerli.net/post/go-get-golang-org-x-solution/)
2. [Golang教程：编译可在苹果iOS设备上运行的Go语言程序 | Konica 的自留地](http://www.iikira.com/2017/08/09/golang-compile-jc-1/)
