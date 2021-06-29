---
title: "Y 站、K 站下载器 moebooru-crawler"
date: 2021-06-29T12:53:33+08:00
tags:
- Linux
- Shell
---

Bash 脚本，用来下载基于 moebooru 的网站上的图片，如 yande.re、konachan.com 等。

GNU/Linux 下可用，不支持 BSD 环境（如 macOS）：<https://github.com/qianbinbin/moebooru-crawler>

## 下载

```sh
$ curl -O "https://github.com/qianbinbin/moebooru-crawler/raw/master/moebooru-crawler.sh"
$ # git clone git@github.com:qianbinbin/moebooru-crawler.git && cd moebooru-crawler
$ chmod +x ./moebooru-crawler.sh
```

## 使用

```sh
Usage: moebooru-crawler URL [ -d | --dir DIR ]
                            [ -n | --num NUM ]
                            [ -u | --urls-only ]
                            [ -p | --max-procs PROCS ]
```

### 示例

#### 下载指定页面上的图片

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?tags=coffee-kizoku+order%3Ascore"
```

#### 下载到指定位置

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?tags=coffee-kizoku+order%3Ascore" -d ~/Downloads
```

#### 只获取图片链接，但不下载

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?tags=coffee-kizoku+order%3Ascore" -u >>downloads.txt
```

然后可以用 aria2c 之类的工具批量下载。

#### 下载指定数量的图片（当页面多于一页时）

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?page=2&tags=coffee-kizoku" -n 100  # "page=2" 会被忽略
```

要下载所有页面上的图片，指定一个很大的数即可：

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?tags=coffee-kizoku" -n 10000
```

#### curl 并行下载最大进程数（默认为 8）

```sh
$ ./moebooru-crawler.sh "https://yande.re/post?tags=coffee-kizoku+order%3Ascore" -p 16
```
