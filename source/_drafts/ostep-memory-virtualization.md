---
title: OSTEP 内存虚拟化
date: 2021-02-28 22:12:18
tags:
- OSTEP
- 操作系统
- 读书笔记
---

## 第 13 章 抽象：地址空间

### 13.1 早期系统

早期 OS 是一组例程，即库。

### 13.2 多道程序和时分共享

由于机器昂贵，为了有效共享机器，引入了多道程序，增加了 CPU 利用率。

由于批量计算的局限性，为了交互性，引入了时分共享。

早期时分共享在将进程暂停后，将其状态信息保持在磁盘上，但这样太慢了，于是将进程信息放在内存中。

多个程序同时驻留在内存中，使得保护成为重要问题，避免进程读写其他进程的内存。

### 13.3 地址空间

OS 将物理内存抽象为地址空间。

一个进程的地址空间包含其所有内存状态，包括：

- 代码：code，指令。
- 栈：stack，局部变量、函数参数、返回值等。
- 堆：heap，通过 `malloc()` 获得的内存。

另外还有用于存储全局和静态变量的数据区等。

![](/images/ostep-memory-virtualization/an-example-address-space.png)

在如图所示的例子中，代码位于地址空间顶部，堆和栈分别放在顶部和底部，这样堆向下增长，栈向上增长。

地址空间的安排有不同的实现。

这里的地址空间都是 OS 提供给进程的抽象（逻辑地址），而不是物理地址，这就是虚拟化内存。

隔离是建立可靠系统的关键原则。一个实体的失败不会影响其他实体。一些现代 OS 将 OS 中的某些部分分离，实现进一步的隔离，这就是微内核，它可以提供比宏内核更高的可靠性。

### 13.4 目标

虚拟化内存的主要目标：

1. 透明：OS 实现虚拟内存的方式对于用户程序是不可见的，程序就好像拥有自己的私有物理内存。
2. 效率：依靠 TLB 等硬件，实现时间和空间上的高效。
3. 保护：OS 通过隔离，确保进程受到保护，进程之间不会影响，OS 本身也不会受进程影响。

使用如下程序打印代码、堆和栈的虚拟地址：

{% include_code lang:c ostep/ostep-code/vm-intro/va.c %}

书中 Mac 的结果：

```
location of code : 0x1095afe50
location of heap : 0x1096008c0
location of stack: 0x7fff691aea64
```

代码和堆在顶部，栈在底部。

本地 macOS Catalina 的结果：

```
location of code : 0x10eb72ec0
location of heap : 0x7fa9a5000000
location of stack: 0x7ffee10908ac
```

看起来堆和栈都在底部。Debian 10 的结果与 macOS 类似。

## 第 14 章 内存操作 API

栈内存由编译器隐式管理，也称为自动内存，堆内存的申请和释放由程序员手动完成。

`malloc()` 接受一个 `size_t` 参数，表示需要分配的字节数，返回指向新分配空间的指针，否则返回 `NULL`。

`free()` 接受一个 `malloc()` 返回的指针。分配区域的大小由内存分配库记录，而不是用户记录。

常见错误：

1. 忘记分配内存。导致段错误。
2. 没有分配足够内存，例如忘记字符串最后的结束符。称为缓冲区溢出。
3. 忘记初始化分配的内存。
4. 忘记释放内存。造成内存泄漏。
5. 在用完之前释放内存。称为悬挂指针。
6. 重复释放内存。这样的结果是未定义的。
7. 错误调用 `free()`。如果传入的不是 `malloc()` 返回的指针，就导致非法释放。

系统中存在两级内存管理：

1. 由 OS 执行的内存管理，OS 在进程运行时将内存交给进程，在进程结束时将其回收。
2. 由进程执行的内存管理，如 `malloc()` 和 `free()`。

即使进程没有释放其申请分配的内存，OS 会在进程结束时清理为其分配的所有内存页面。因此对于短时间运行的程序，内存泄露通常不会导致问题。但如果是长期运行的程序，例如数据库管理系统、Web 服务器，甚至是 OS 本身，就可能会导致内存不足而崩溃。

`malloc()` 和 `free()` 并不是系统调用，而是库调用，它是建立在一些系统调用之上的，如 `brk()`、`sbrk()`、`mmap()` 等。

内存分配库还包括 `calloc()`、`realloc()` 等。

### 作业

1.

{% include_code lang:c ostep/ostep-homework/vm-api/null.c %}

运行没有异常。

2. 编译时加入 `-g` 标志后使用 gdb 运行也没有异常：

```
(gdb) run
Starting program: .../ostep-homework/vm-api/null
[Inferior 1 (process 5086) exited normally]
```

3. 书中命令 `valgrind --leak-check=yes null` 有误，应该加上路径 `./null`：

```
$ valgrind --leak-check=yes ./null
==7937== Memcheck, a memory error detector
==7937== Copyright (C) 2002-2017, and GNU GPL'd, by Julian Seward et al.
==7937== Using Valgrind-3.14.0 and LibVEX; rerun with -h for copyright info
==7937== Command: ./null
==7937==
==7937==
==7937== HEAP SUMMARY:
==7937==     in use at exit: 0 bytes in 0 blocks
==7937==   total heap usage: 0 allocs, 0 frees, 0 bytes allocated
==7937==
==7937== All heap blocks were freed -- no leaks are possible
==7937==
==7937== For counts of detected and suppressed errors, rerun with: -v
==7937== ERROR SUMMARY: 0 errors from 0 contexts (suppressed: 0 from 0)
```

没有错误。

4.

{% include_code lang:c ostep/ostep-homework/vm-api/p4.c %}

使用 gdb 没有异常，使用 `valgrind` 检测出内存泄露：

```
$ valgrind --leak-check=yes ./p4
==8015== Memcheck, a memory error detector
==8015== Copyright (C) 2002-2017, and GNU GPL'd, by Julian Seward et al.
==8015== Using Valgrind-3.14.0 and LibVEX; rerun with -h for copyright info
==8015== Command: ./p4
==8015==
==8015==
==8015== HEAP SUMMARY:
==8015==     in use at exit: 4 bytes in 1 blocks
==8015==   total heap usage: 1 allocs, 0 frees, 4 bytes allocated
==8015==
==8015== 4 bytes in 1 blocks are definitely lost in loss record 1 of 1
==8015==    at 0x483577F: malloc (vg_replace_malloc.c:299)
==8015==    by 0x109146: main (in .../ostep-homework/vm-api/p4)
==8015==
==8015== LEAK SUMMARY:
==8015==    definitely lost: 4 bytes in 1 blocks
==8015==    indirectly lost: 0 bytes in 0 blocks
==8015==      possibly lost: 0 bytes in 0 blocks
==8015==    still reachable: 0 bytes in 0 blocks
==8015==         suppressed: 0 bytes in 0 blocks
==8015==
==8015== For counts of detected and suppressed errors, rerun with: -v
==8015== ERROR SUMMARY: 1 errors from 1 contexts (suppressed: 0 from 0)
```

5.

{% include_code lang:c ostep/ostep-homework/vm-api/p5.c %}

运行没有错误，`valgrind` 检测出非法写入：

```
$ valgrind ./p5
==8052== Memcheck, a memory error detector
==8052== Copyright (C) 2002-2017, and GNU GPL'd, by Julian Seward et al.
==8052== Using Valgrind-3.14.0 and LibVEX; rerun with -h for copyright info
==8052== Command: ./p5
==8052==
==8052== Invalid write of size 4
==8052==    at 0x109165: main (in .../ostep-homework/vm-api/p5)
==8052==  Address 0x4a101d0 is 0 bytes after a block of size 400 alloc'd
==8052==    at 0x483577F: malloc (vg_replace_malloc.c:299)
==8052==    by 0x109156: main (in .../ostep-homework/vm-api/p5)
==8052==
==8052==
==8052== HEAP SUMMARY:
==8052==     in use at exit: 0 bytes in 0 blocks
==8052==   total heap usage: 1 allocs, 1 frees, 400 bytes allocated
==8052==
==8052== All heap blocks were freed -- no leaks are possible
==8052==
==8052== For counts of detected and suppressed errors, rerun with: -v
==8052== ERROR SUMMARY: 1 errors from 1 contexts (suppressed: 0 from 0)
```

6.

{% include_code lang:c ostep/ostep-homework/vm-api/p6.c %}

直接运行输出 `0`，`valgrind` 检测出非法读取：

```
$ ./p6
0
$ valgrind ./p6
==8070== Memcheck, a memory error detector
==8070== Copyright (C) 2002-2017, and GNU GPL'd, by Julian Seward et al.
==8070== Using Valgrind-3.14.0 and LibVEX; rerun with -h for copyright info
==8070== Command: ./p6
==8070==
==8070== Invalid read of size 4
==8070==    at 0x10917B: main (in .../ostep-homework/vm-api/p6)
==8070==  Address 0x4a10040 is 0 bytes inside a block of size 400 free'd
==8070==    at 0x48369AB: free (vg_replace_malloc.c:530)
==8070==    by 0x109176: main (in .../ostep-homework/vm-api/p6)
==8070==  Block was alloc'd at
==8070==    at 0x483577F: malloc (vg_replace_malloc.c:299)
==8070==    by 0x109166: main (in .../ostep-homework/vm-api/p6)
==8070==
0
==8070==
==8070== HEAP SUMMARY:
==8070==     in use at exit: 0 bytes in 0 blocks
==8070==   total heap usage: 2 allocs, 2 frees, 1,424 bytes allocated
==8070==
==8070== All heap blocks were freed -- no leaks are possible
==8070==
==8070== For counts of detected and suppressed errors, rerun with: -v
==8070== ERROR SUMMARY: 1 errors from 1 contexts (suppressed: 0 from 0)
```

7.

{% include_code lang:c ostep/ostep-homework/vm-api/p7.c %}

运行显示非法指针：

```
$ ./p7
free(): invalid pointer
Aborted
$ valgrind ./p7
==8094== Memcheck, a memory error detector
==8094== Copyright (C) 2002-2017, and GNU GPL'd, by Julian Seward et al.
==8094== Using Valgrind-3.14.0 and LibVEX; rerun with -h for copyright info
==8094== Command: ./p7
==8094==
==8094== Invalid free() / delete / delete[] / realloc()
==8094==    at 0x48369AB: free (vg_replace_malloc.c:530)
==8094==    by 0x10916A: main (in .../ostep-homework/vm-api/p7)
==8094==  Address 0x4a10044 is 4 bytes inside a block of size 400 alloc'd
==8094==    at 0x483577F: malloc (vg_replace_malloc.c:299)
==8094==    by 0x109156: main (in .../ostep-homework/vm-api/p7)
==8094==
==8094==
==8094== HEAP SUMMARY:
==8094==     in use at exit: 400 bytes in 1 blocks
==8094==   total heap usage: 1 allocs, 1 frees, 400 bytes allocated
==8094==
==8094== LEAK SUMMARY:
==8094==    definitely lost: 400 bytes in 1 blocks
==8094==    indirectly lost: 0 bytes in 0 blocks
==8094==      possibly lost: 0 bytes in 0 blocks
==8094==    still reachable: 0 bytes in 0 blocks
==8094==         suppressed: 0 bytes in 0 blocks
==8094== Rerun with --leak-check=full to see details of leaked memory
==8094==
==8094== For counts of detected and suppressed errors, rerun with: -v
==8094== ERROR SUMMARY: 1 errors from 1 contexts (suppressed: 0 from 0)
```

8.

{% include_code lang:c ostep/ostep-homework/vm-api/p8.c %}

添加元素时如果超过容量，需要使用 `realloc()` 重新分配内存，时间复杂度比链表高。

## 第 15 章 机制：地址转换

基于硬件的地址转换：简称为地址转换，在每次内存引用时，硬件将指令中的虚拟地址转换为数据实际存储的物理地址。

OS 必须在关键位置介入，设置硬件，管理内存。

### 15.1 假设

本章作以下假设：

1. 用户地址空间连续存放在物理内存中。
2. 地址空间小于物理内存大小。
3. 每个地址空间大小完全一样。

### 15.2 一个例子

下图中 OS 将进程地址空间重定位到 32KB 开始的物理地址：

![](/images/ostep-memory-virtualization/a-process-and-its-address-space.png)

![](/images/ostep-memory-virtualization/physical-memory-with-a-single-relocated-process.png)

### 15.3 动态重定位

在硬件支持重定位前，一些 OS 使用纯软件实现重定位，加载程序重写指令中的地址，称为静态重定位。它不提供访问保护，也很难将内存重定位到其他位置。

动态重定位也称为基址加界限机制，每个 CPU 使用两个寄存器：基址（base）寄存器和界限（bound）寄存器，前者保存物理起始地址，后者提供访问保护。

编写和编译程序时，假设地址空间从 0 开始，当程序执行时，将其转换为物理地址：

```
physical address = virtual address + base
```

硬件将虚拟地址转换为物理地址后，发送给内存系统。

这种重定位是在运行时发生的，OS 甚至可以在进程运行后改变其地址空间，因此称为动态重定位。

界限寄存器有两种使用方式：

1. 保存地址空间大小。在地址转换前，就检查这个界限。
2. 保存地址空间结束的物理地址。在地址转换后才检查这个界限。

一旦内存访问越界，CPU 将触发异常，进程可能被终止。

CPU 中负责地址转换的部分称为内存管理单元（Memory Management Unit，MMU）。

OS 还必须记录哪些空闲内存没有使用，最简单的方法是使用空闲列表（free list）。

### 15.4 硬件支持

硬件要求：

1. 特权模式：如使用处理器状态字来标识当前 CPU 的运行模式。
2. 基址/界限寄存器。
3. 实现地址转换并检查是否越界。
4. 修改基址/界限寄存器的特权指令。
5. 注册异常处理程序的特权指令。
6. 触发异常：如内存访问越界时。

### 15.5 软件支持（OS）

OS 介入：

1. 在进程创建时，为进程的地址空间寻找内存空间。
2. 在进程终止时，回收其所有内存。
3. 上下文切换时，在内存中保存（或从内存中恢复）基址寄存器和界限寄存器，即维护进程结构体或 PCB。OS 还可以动态改变其地址空间的物理位置。
4. 提供异常处理程序。例如当内存访问越界时，CPU 触发异常，OS 将错误进程终止。

系统启动时：

![](/images/ostep-memory-virtualization/limited-direct-execution-dynamic-relocation-boot.png)

运行进程 A，然后切换到 B，B 触发越界异常：

![](/images/ostep-memory-virtualization/limited-direct-execution-dynamic-relocation-runtime.png)

### 15.6 小结

本章中，为进程分配的物理内存区域是固定的连续空间，其内部有未使用的空间，称为内部碎片，这造成了内存的浪费。
