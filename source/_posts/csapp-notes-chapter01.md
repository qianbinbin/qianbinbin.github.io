---
title: CSAPP 计算机系统漫游
date: 2018-12-24 21:30:09
mathjax: true
tags:
- CSAPP
- 读书笔记
---

# 1.1 信息就是二进制位+上下文

例如 C 语言源码文件 [hello.c](https://github.com/qianbinbin/csapp/blob/master/ch01/hello.c)，可用 vim 以二进制方式打开：

```sh
$ vim -b hello.c
```

然后使用 `:%!xxd` 转换为 16 进制，直接查看 [ASCII 码](https://github.com/qianbinbin/csapp/blob/master/ch01/hello.ascii)：

```
00000000: 2369 6e63 6c75 6465 203c 7374 6469 6f2e  #include <stdio.
00000010: 683e 0a0a 696e 7420 6d61 696e 2829 0a7b  h>..int main().{
00000020: 0a20 2020 2070 7269 6e74 6628 2268 656c  .    printf("hel
00000030: 6c6f 2c20 776f 726c 645c 6e22 293b 0a20  lo, world\n");. 
00000040: 2020 2072 6574 7572 6e20 303b 0a7d 0a       return 0;.}.
```

# 1.2 程序被其他程序翻译成不同的格式

gcc 编译保留中间文件：

```sh
$ gcc -save-temps -o hello hello.c
```

翻译过程：

1. 预处理：原始 C 程序 `hello.c` 中以 `#` 开头的命令，告诉预处理器读取系统头文件，并将头文件直接插入程序文本，得到修改的源程序文本 [hello.i](https://github.com/qianbinbin/csapp/blob/master/ch01/hello.i)
2. 编译：将 `hello.i` 翻译成文本文件 [hello.s](https://github.com/qianbinbin/csapp/blob/master/ch01/hello.s)，它包含一个汇编语言程序，该程序包含函数 `main` 的定义
3. 汇编：汇编器将 `hello.s` 翻译成机器语言指令，并把指令打包成可重定位目标程序，结果保存到二进制文件 [hello.o](https://github.com/qianbinbin/csapp/blob/master/ch01/hello.o)
4. 链接：`hello` 程序调用了 `printf`函数，它存在于一个名为 `printf.o` 到单独的预编译好的目标文件中，链接器将它以某种方式合并到 `hello.o` 程序中，结果得到可执行文件 [hello](https://github.com/qianbinbin/csapp/blob/master/ch01/hello)

# 1.7 操作系统管理硬件

## 1.7.3 虚拟内存

Linux 中，虚拟地址空间由大量区构成，从低到高有：

- 程序代码和数据
- 堆
  调用 `malloc` 和 `free` 这样的 C 标准函数，堆可以在运行时动态扩展和收缩
- 共享库
  存放像 C 标准库和数学库这样的共享库的代码和数据的区域
- 栈
  编译器用它来实现函数调用
- 内核虚拟内存
  位于地址空间顶部区域

## 1.7.4 文件

文件就是字节序列，每个 I/O 设备包括磁盘、键盘、显示器甚至网络，均可看作文件，系统中所有输入输出都是通过称为 Unix I/O 的系统函数调用读写文件来实现的。

# 1.9 重要主题

## 1.9.1 Amdahl 定律

若某程序需要时间为 $T\_{old}$，某部分所需执行时间占比为 $\alpha$，该部分性能提升比例为 $k$，则总的执行时间变为

$$T\_{new} = (1 - \alpha)T\_{old} + (\alpha T\_{old}) / k = T\_{old}[(1 - \alpha) + \alpha / k]$$

加速比 $S = T\_{old} / T\_{new}$ 为

$$S = \frac{1}{(1 - \alpha) + \alpha / k}$$

Amdahl 定律说明，要想显著加速整个系统，必须提升全系统中相当大的部分。假设我们可以将系统某一部分加速到时间可以忽略不计，即 $k$ 趋向于 $\infty$，则

$$S\_\infty = \frac{1}{1 - \alpha}$$

我们获得的加速比仍然是很有限的。

## 1.9.2 并发和并行

1. 线程级并发

  多核处理器是将多个 CPU 集成到一个继承电路芯片上。

  超线程是允许一个 CPU 执行多个控制流的技术，涉及 CPU 某些硬件有多个备份，比如程序计数器和寄存器文件（寄存器堆），而其他硬件只有一份，比如执行复电算数运算的单元。

2. 指令级并行

  同时执行多条指令的属性称为指令级并行。流水线技术的使用。如果处理器执行速率可以达到比每个时钟周期一条指令更快，称为超标量处理器。

3. 单指令、多数据并行

  SIMD 并行，现代处理器拥有特殊的硬件，允许一条指令产生多个可以并行执行的操作，例如并行地对 8 对单精度浮点数（float）做加法。
