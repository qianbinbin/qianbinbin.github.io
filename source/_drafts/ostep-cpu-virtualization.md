---
title: OSTEP CPU 虚拟化
date: 2021-02-06 23:21:20
tags:
- OSTEP
- 操作系统
- 读书笔记
---

## 第 4 章 抽象：进程

进程就是运行中的程序。

OS 通过虚拟化 CPU 来提供几乎有无数个 CPU 可用的假象。一个进程只运行一个时间片，然后切换到其它进程，这就是时分共享 CPU 技术。

要实现 CPU 的虚拟化，OS 就需要一些低级机制以及一些高级智能。

机制是一些低级方法或协议，例如上下文切换，它让 OS 停止运行一个程序并开始在给定 CPU 上运行另一个程序。

在机制之上，OS 中有一些智能以策略的形式存在。策略是在 OS 内作出某种决定的算法，例如对多个程序选择一个运行的调度策略。

### 4.1 抽象：进程概念

OS 为正在运行的程序提供的抽象，就是进程。

进程的机器状态，指程序在运行时可以读取或更新的内容，包括内存和寄存器。

### 4.2 进程 API

- 创建：创建新进程。
- 销毁：强制销毁进程。
- 等待：等待进程停止运行。
- 其它控制：例如暂停和恢复。
- 状态：获取进程状态信息。

### 4.3 进程创建：更多细节

1. 将代码和静态数据（如初始化变量）加载到内存中。程序最初以可执行格式驻留在磁盘上，OS 从磁盘读取这些字节并放到内存中。

   早期 OS 中，加载在程序运行前全部完成，现代 OS 使用惰性加载，即只在程序执行时加载所需的代码或数据片段，这是通过分页和交换机制实现的。

2. OS 为程序的运行时栈分配内存。例如 C 程序使用栈存放局部变量、函数参数和返回地址。OS 可能会初始化栈，例如为 `main()` 函数填入 `argc` 和 `argv` 参数。

   OS 也可能为程序分配堆内存。在 C 中调用 `malloc()` 申请空间，`free()` 释放。

3. OS 执行一些初始化任务，尤其是 I/O 相关任务。例如 UNIX 默认每个进程都有 3 个打开的文件描述符：标准输入、输出和错误。

4. OS 启动程序，在入口处运行，即 `main()`。通过跳转到 `main()` 例程，OS 将 CPU 控制权转移到新创建的进程中。

### 4.4 进程状态

- 运行
- 就绪
- 阻塞

![](/images/ostep-cpu-virtualization/process-state-transitions.png)

### 4.5 数据结构

{% include_code lang:c from:16 to:52 xv6-public/proc.h %}

`context` 结构体是寄存器上下文。进程停止时，将寄存器内容保存到内存，进程恢复时，将内容取回到寄存器。这种技术叫上下文切换。

`procstate` 用于表示进程状态。

ZOMBIE 称为僵尸状态，表示进程处于已退出但尚未清理的最终状态。这个状态可用于其他进程（通常是父进程）检查进程的返回代码。完成后，父进程将进行最后一次调用（如 `wait()`）以等待子进程完成，并告诉 OS 可以清理这个进程的数据结构。

`proc` 结构体是 PCB（进程控制块）。

## 第 5 章 插叙：进程 API

### 5.1 fork() 系统调用

`fork()` 是 OS 提供的创建新进程的方法。

{% include_code lang:c ostep/ostep-code/cpu-api/p1.c %}

```sh
$ ./p1
hello world (pid:29146)
hello, I am child (pid:29147)
hello, I am parent of 29147 (pid:29146)
```

新创建的进行几乎与调用的进程完全一样，在 OS 看来就好像有两个 `p1` 的副本在运行，两者都将从 `fork()` 系统调用返回。

但子进程不会从 `main()` 函数开始执行，而是紧接着 `fork()` 之后。父进程获得的返回值是子进程的 PID，子进程获得的返回值是 0。这样就可以在编码中处理两种不同的情况。

子进程和父进程的先后顺序是不确定的。

### 5.2 wait() 系统调用

有时父进程需要等待子进程执行完毕，此时可以用 `wait()` 或 `waitpid()` 系统调用。

{% include_code lang:c ostep/ostep-code/cpu-api/p2.c %}

```sh
$ ./p2
hello world (pid:29266)
hello, I am child (pid:29267)
hello, I am parent of 29267 (rc_wait:29267) (pid:29266)
```

### 5.3 exec() 系统调用

`exec()` 系统调用可以让子进程执行与父进程不同的程序。

{% include_code lang:c ostep/ostep-code/cpu-api/p3.c %}

这里在子进程中调用 `wc p3.c` 来统计字符数（源码与书中不同，因此结果不同）：

```sh
$ ./p3
hello world (pid:29383) hello, I am child (pid:29384)
29 107 1030 p3.c
hello, I am parent of 29384 (rc_wait:29384) (pid:29383)
```

`exec()` 会从可执行程序中加载代码和静态数据，并覆盖自身，堆栈等内存空间也会初始化，然后 OS 执行该程序，将参数传递给该进程。

`exec()` 不会创建新进程，而是用当前程序替换。`exec()` 成功调用后不会返回。

### 5.4 为什么这样设计 API

将 `fork()` 与 `exec()` 分离，可以在 `fork()` 之后、`exec()` 之前运行一些代码，例如改变环境，从而实现特定功能。

shell 也是一个用户程序。当运行一个命令时，shell 先在文件系统中找到这个可执行程序，调用 `fork()` 创建新进程，并调用 `exec()` 来执行它，调用 `wait()` 来等待该命令完成。子进程执行结束后，shell 从 `wait()` 返回，等待用户输入下一条命令。

例如，

```sh
$ wc p3.c > newfile.txt
```

`wc` 的输出结果被重定向到 `newfile.txt` 中，shell 实现的方式为，当创建子进程后，在调用 `exec()` 之前先关闭标准输出，打开文件 `newfile.txt`，这样 `wc` 的输出结果就被发送到该文件。

{% include_code lang:c ostep/ostep-code/cpu-api/p4.c %}

UNIX 管道也是用类似方式实现的，但用的是 `pipe()` 系统调用。一个进程的输出被连接到一个内核管道上（如队列），另一个进程的输入也被连接到同一管道上，因此前一个进程的输出无缝作为后一个进程的输入。例如 `grep -o foo file | wc -l`。

### 5.5 其他 API

UNIX 还有其他与进程交互的方式，如 `kill()` 系统调用可以向进程发送信号，要求其睡眠、终止等。