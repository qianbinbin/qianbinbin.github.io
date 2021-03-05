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

1. 透明。OS 实现虚拟内存的方式对于用户程序是不可见的，程序就好像拥有自己的私有物理内存。
2. 效率。依靠 TLB 等硬件，实现时间和空间上的高效。
3. 保护。OS 通过隔离，确保进程受到保护，进程之间不会影响，OS 本身也不会受进程影响。

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

```sh
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

```sh
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

```sh
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

```sh
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

```sh
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
