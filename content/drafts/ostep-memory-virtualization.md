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

本章中，为进程分配的物理内存区域是固定大小的连续空间，其内部有未使用的空间，称为内部碎片，这造成了内存的浪费。

## 第 16 章 分段

在上一章中，除了内部碎片的问题外，还要求使用连续的物理内存来存放地址空间，否则无法运行。

### 16.1 分段：泛化的基址/界限

分段可以解决这个问题，在 MMU 中引入不止一对基址和界限寄存器，而是每个逻辑段一对。

段是地址空间里一个连续的特定长度的区域，例如一个典型的地址空间包括 3 个逻辑段：代码、栈和堆，此时需要 3 对基址和界限寄存器。

这样，OS 可以将不同的段放到不同的物理内存区域，避免内部碎片。

例如，一个地址空间如下：

![](/images/ostep-memory-virtualization/an-address-space-again.png)

存放到物理内存中：

![](/images/ostep-memory-virtualization/placing-segments-in-physical-memory.png)

其段寄存器的值为：

![](/images/ostep-memory-virtualization/segment-register-values.png)

在支持分段的机器上访问非法地址，称为段错误（segment fault）。但即使在不支持分段的机器上也使用这个术语。

### 16.2 段及偏移量

硬件如何知道地址引用的是哪个段，及段内的偏移量？

常见的方式是显式方式，即使用虚拟地址开头几位来标识段。例如有 3 个段：

![](/images/ostep-memory-virtualization/segment-offset.png)

使用 2 位来标识段，`00` 表示代码段，`01` 表示堆地址。

剩余位是段内偏移量。只要检查偏移量是否小于界限，即可判断是否越界。

伪代码描述（硬件）：

```
// get top 2 bits of 14-bit VA
Segment = (VirtualAddress & SEG_MASK) >> SEG_SHIFT
// now get offset
Offset = VirtualAddress & OFFSET_MASK
if (Offset >= Bounds[Segment])
    RaiseException(PROTECTION_FAULT)
else
    PhysAddr = Base[Segment] + Offset
    Register = AccessMemory(PhysAddr)
```

在上例中，`SEG_MASK` 为 `0x3000`，`SEG_SHIFT` 为 12，`OFFSET_MASK` 为 `0xFFF`。

有的系统会将代码和堆放在同一段，这时只需要一个位来标识段。

这一方式的问题在于限制了每个段的大小。

在隐式方式中，硬件通过地址产生的方式来确定段，例如 PC 产生的地址（取指）则在代码段中，基于栈或基址指针的地址在栈段中，其他地址在堆段。

### 16.3 栈

栈是方向增长的，因此硬件需要一个位来区分段的增长方式，例如使用 1 表示从小到大，0 反之：

![](/images/ostep-memory-virtualization/segment-registers-with-negative-growth-support.png)

栈的偏移量计算方式也有区别。

### 16.4 支持共享

有时需要在地址空间之间共享内存段，如代码共享，此时可以使用保护位，来标识程序是否可以读写该段或执行其中的代码。

例如，将代码段标记为只读，那么代码就可以被共享，而不用担心破坏隔离，每个进程都认为自己独占了这块内存。

![](/images/ostep-memory-virtualization/segment-registers-with-protection.png)

此时，硬件除了要检查是否越界，还要检查特定访问是否允许，如不允许则触发异常。

### 16.5 分段粒度

如果分段较少，这种分段是粗粒度的。早期系统允许将地址空间划分为大量小段，称为细粒度。这种方式需要其他硬件，并在内存中维护段表。

### 16.6 软件支持（OS）

1. 在切换上下文时，保存和恢复各个段寄存器。
2. 段可以增长或减少，例如使用 `sbrk()` 系统调用。
3. 管理物理内存的空闲空间。

   分配物理内存区域之间会产生许多小的空闲空间，很难分配给新的段，这种问题就是外部碎片。

   此时可以用紧凑（compact）来解决，即将进程停止所有正在运行的进程，将它们的数据复制到连续的内存区域中，并更新其段寄存器，从而获得大的连续空闲空间。但紧凑的成本很高，因为段的复制是内存密集型的，会占用很多 CPU 时间。

   还可以使用空闲列表管理算法，如最优匹配、首次匹配、伙伴算法等。这些算法都无法消除外部碎片，只能试图减小。

### 16.7 小结

分段的好处有：

1. 避免内部碎片，更好地支持稀疏地址空间。
2. 开销小。
3. 支持代码共享。

存在的问题：

1. 外部碎片。
2. 不适合更一般化的稀疏地址空间。例如一个很大的稀疏的堆，整个堆都必须完全分配物理内存才能运行。

## 第 17 章 空闲空间管理

如果空间被划分为固定大小的单元，例如分页，管理空闲空间就很容易；否则就很难，例如分段，或用户级内存分配库，如 `malloc()` 和 `free()`。

### 17.1 假设

本章作以下假设：

1. 基本接口类似于 `malloc()` 和 `free()`。
2. 只关注外部碎片。
3. 内存一旦分配，就不能重定向到其他位置。不使用紧凑。
4. 管理的是连续区域。

简而言之，本章讨论的是 `malloc()` 和 `free()` 的一些原理。

### 17.2 底层机制

空闲列表是一个链表，每个元素对应一个空闲区域。

#### 分割与合并

在请求分配内存时，如果申请的内存比找到的空闲块小，则分配程序会进行分割，第一块返回给用户，第二块留在空闲列表；如果找不到大小足够的空闲块，则分配程序会将相邻的空闲块合并为大的空闲块。

#### 追踪已分配空间大小

`free(void *ptr)` 接口没有块大小的参数，块大小实际上由分配程序记录在返回的内存块之前，称为头块（header）。

例如，一个简单的头块：

```c
typedef struct {
    int size;
    int magic;
} header_t;
```

它至少包含空间大小，还可能包含加速释放的额外指针，以及幻数（magic number）。

幻数起验证作用，例如设置为常数 `1234567`，断言 `assert(hptr->magic == 1234567)`。

![](/images/ostep-memory-virtualization/specific-contents-of-the-header.png)

释放空间时，通过简单的计算得出实际起始地址：

```c
void free(void *ptr) {
    header_t *hptr = (header_t *) ptr - 1;
    ...
```

这些头块对用户是透明的，因此实际分配和释放的空间比用户空间略大。

#### 嵌入空闲列表

在内存分配库内，要使用空闲内存建立列表，显然不能像 C 程序那样直接使用 `malloc()`。

需要在空闲空间本身中建立空闲列表。

链表结点：

```c
typedef struct __node_t {
    int size;
    struct __node_t *next;
} node_t;
```

现初始化一个 4KB 的堆，假设通过 `mmap()` 系统调用获取：

```c
// mmap() returns a pointer to a chunk of free space
node_t *head = mmap(NULL, 4096, PROT_READ|PROT_WRITE,
                    MAP_ANON|MAP_PRIVATE, -1, 0);
head->size = 4096 - sizeof(node_t);
head->next = NULL;
```

和已分配内存块的头块类似，空闲块头部也有一个链表结点的结构体。

此时，列表中只有一个结点，记录的大小为 4096 - 8 = 4088：

![](/images/ostep-memory-virtualization/a-heap-with-one-free-chunk.png)

假设申请 100 字节，由于只有一个块，那么这个块被分割为两块，一块满足请求，一块为剩余空闲块：

![](/images/ostep-memory-virtualization/a-heap-after-one-allocation.png)

当连续分配 3 个 100 字节的块时：

![](/images/ostep-memory-virtualization/free-space-with-three-chunks-allocated.png)

如果用户通过 `free()` 释放第二块，则调用的是 `free(16500)`，即释放 `sptr` 指向的内存。

库读取头块，得到需释放的空间大小，释放后将其加入空闲列表。假设这个空闲块插入到列表头部：

![](/images/ostep-memory-virtualization/free-space-with-two-chunks-allocated.png)

假设剩余的两块也被释放，则需要合并相邻的空闲块。

#### 让堆增长

大多数传统分配程序一开始会申请较小的堆，当空间耗尽时再申请更大的堆。例如 OS 执行 `sbrk()` 系统调用，找到空闲的物理内存页，将其映射到进程的地址空间，并返回新的堆的末尾地址。

### 17.3 基本策略

#### 最优匹配（best fit）

遍历整个空闲列表，找到足够大的最小空闲块并返回。

其思路是避免空间浪费。会导致很多小块。

#### 最差匹配（worst fit）

与最优匹配相反，它返回的是最大的空闲块。

其思路是试图保留更大的空闲块。会导致很多碎片。

#### 首次匹配（first fit）

即找到第一个足够大的空闲块并返回。

不需要遍历所有空闲块，但可能会在空闲列表开头产生很多小块。可以保持空闲块按内存地址有序，有利于合并，从而减少碎片。

#### 下次匹配（next fit）

维护一个指针指向上次查找结束的位置，每次查找都从上次结束位置开始。

其思路是将查找操作分散到整个列表，避免列表开头产生很多小块。

### 17.4 分离空闲列表（segregated free lists）

本节参考自 CSAPP。

分离空闲列表使用分离存储，即维护多个空闲列表，每个列表中的块大小在一定范围内，称为大小类（size class）。分配程序维护一个数组，按大小升序排列这些列表。

这样，在查找时效率会更高。

例如，按 2 的幂来划分块大小：

{1}, {2}, {3, 4}, {5–8}, . . . , {1,025–2,048}, {2,049–4,096}, {4,097–∞}

或者，对于小的块，每种大小一个列表，大的块按 2 的幂划分：

{1}, {2}, {3}, . . . , {1,023}, {1,024}, {1,025–2,048}, {2,049–4,096}, {4,097–∞}

#### 简单分离存储

每个列表内的块大小是相等的，都等于对应大小类的最大值。例如大小类为 {17-32}，则列表内块大小都为 32。

当请求分配时，如果列表非空，则直接返回一个块，但块不会分割。

如果找不到满足的列表，则向 OS 请求足够大的内存块（固定大小，通常是页的整数倍），并将其分为大小相等的块，组成新列表，返回一个块即可。

简单分离存储不分割、不合并。

#### 分离适配

每个列表中的块大小可以不同。

分配时，在对应的列表内查找足够大的块（例如使用首次适配），将其分割，剩余部分放入合适的列表。如果找不到这样的块，则在下一级列表中查找，直到找到为止。如果所有列表均不存在，则向 OS 申请，将剩余部分放入合适的列表。

释放时，需要合并相邻的空闲块，并将新块放入合适的列表。

#### 伙伴系统

伙伴系统是分离适配的一个特例，每个大小类都是 2 的幂，每个块大小也是 2 的幂。

分配时，空闲块被递归地一分为二，直到刚好可以满足请求大小（再划分就不满足），那些分割生成的块被放入对应的列表。

释放时，分配程序检查其伙伴是否为空闲块，如果是则递归地合并。

![](/images/ostep-memory-virtualization/example-buddy-managed-heap.png)

由于每个块都通过二分生成，寻找相邻伙伴非常容易（只相差一个位），因此大大减小了合并的开销。

### 17.5 小结

这些分配策略的问题在于缺乏可扩展性，即块越多查找列表越慢，可以采用平衡二叉树等换取性能。
