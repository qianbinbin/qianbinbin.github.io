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

**但子进程不会从 `main()` 函数开始执行，而是紧接着 `fork()` 之后。父进程获得的返回值是子进程的 PID，子进程获得的返回值是 0。这样就可以在编码中处理两种不同的情况。**

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

### 作业

1.

{% include_code lang:c ostep/ostep-homework/cpu-api/p1.c %}

2.

{% include_code lang:c ostep/ostep-homework/cpu-api/p2.c %}

3.

{% include_code lang:c ostep/ostep-homework/cpu-api/p3.c %}

4. 根据 <https://en.wikipedia.org/wiki/Exec_(system_call)>：

```c
int execl(char const *path, char const *arg0, ...);
int execle(char const *path, char const *arg0, ..., char const *envp[]);
int execlp(char const *file, char const *arg0, ...);
int execv(char const *path, char const *argv[]);
int execve(char const *path, char const *argv[], char const *envp[]);
int execvp(char const *file, char const *argv[]);
```

> e – An array of pointers to environment variables is explicitly passed to the new process image.
>
> l – Command-line arguments are passed individually (a list) to the function.
>
> p – Uses the PATH environment variable to find the file named in the file argument to be executed.
>
> v – Command-line arguments are passed to the function as an array (vector) of pointers.

- `e`：`environment variables`，指定环境变量。
- `l`：`list`，通过列表传递参数。
- `p`：`PATH environment variable`，即 `PATH` 环境变量，从 `PATH` 中查找可执行程序。
- `v`：`vector`，通过数组传递参数。

{% include_code lang:c ostep/ostep-homework/cpu-api/p4.c %}

5. 父进程调用 `wait()` 返回的是子进程的 `pid`，子进程调用 `wait()` 返回值为 `-1`：

{% include_code lang:c ostep/ostep-homework/cpu-api/p5.c %}

6. `wait(&wstatus)` 等价于 `waitpid(-1, &wstatus, 0)`：

{% include_code lang:c ostep/ostep-homework/cpu-api/p6.c %}

7. 关闭标准输出后，`printf()` 不会有内容输出：

{% include_code lang:c ostep/ostep-homework/cpu-api/p7.c %}

8. 使用 `pipe()` 系统调用创建管道，使用 `dup2()` 系统调用将标准输入和输出重定向到管道两端：

{% include_code lang:c ostep/ostep-homework/cpu-api/p8.c %}

```c
int dup2(int oldfd, int newfd);
```

`dup2()` 将 `newfd` 指向 `oldfd`，如果 `newfd` 已经打开，则首先会被关闭，然后再指向 `oldfd`。这样在访问 `newfd` 时，实际上访问的是 `oldfd`。

`reader` 进程中最后 3 行是读取标准输入并输出，如果将其注释，则不会有 `writer` 输出，可见实现了将一个进程的标准输出连接到另一个进程的标准输入。

## 第 6 章 机制：受限直接执行

### 6.1 基本技巧：受限直接执行

如果直接在 CPU 上执行程序，会有两个问题：

1. OS 如何确保程序不做任何我们不希望它做的事？
2. OS 如何实现虚拟化 CPU，即时分共享？

### 6.2 问题 1：受限制的操作

例如，如果用户进程可以任意访问磁盘 I/O，那么文件系统权限保护就会失效。为此必须引入新的处理器模式。

- 用户模式：用户模式下运行的代码会受到限制，例如无法发起 I/O 请求，这样会导致处理器异常，OS 可能会终止该进程。
- 内核模式：OS 以这种模式运行，运行代码可以任意进行特权操作，如发起 I/O 请求、使用受限指令等。

如果用户希望执行某种特权操作，现代硬件提供了用户程序执行系统调用的能力，系统调用允许内核向用户程序暴露某些关键功能，如访问文件系统、创建和销毁进程、与其他进程通信、分配内存等。

要执行系统调用，程序必须执行特殊的陷阱指令，该指令跳入内核，并将特权级别提升到内核模式。完成工作后，OS 调用特殊的从陷阱返回指令，返回到发起调用的用户程序中，同时回到用户模式。

执行陷阱指令时，硬件需要将调用者的寄存器保存起来，以便在 OS 发出从陷阱指令时正确返回。例如，在 x86 上，处理器会将程序计数器、标志和其他一些寄存器送入每个进程的内核栈。从陷阱返回时，从栈弹出这些值，并恢复执行用户程序。

系统调用看起来就像过程调用，实际上系统调用就是将陷阱指令隐藏在过程调用中。C 库中的系统调用部分是用汇编手工编码的。

陷阱如何知道在 OS 内运行哪些代码？显然不应该由用户程序指定。这是由陷阱表来实现的。

机器启动时在内核模式下执行，根据需要配置机器硬件，设置陷阱表。OS 首先做的事之一，就是告诉硬件在发生异常事件时要运行哪些代码。

当发生中断时，OS 通常通过某种特殊指令，通知硬件这些陷阱处理程序的位置。一旦硬件被通知，它就会记住这些处理程序的位置，直到重启。这种指令也是特权操作。

假设每个进程都有一个内核栈，用于在进入和离开内核时，保存和恢复寄存器：

![](/images/ostep-cpu-virtualization/limited-direct-excution-protocol.png)

LDE 协议（Limited Direct Excution Protocol，受限直接执行协议）有两个阶段：

1. 系统启动时，内核初始化陷阱表，并且 CPU 记住它的位置。内核通过特权指令执行此操作。
2. 运行进程时，内核设置一些内容，然后使用从陷阱返回指令，将 CPU 切换到用户模式并开始运行该进程。进程发出系统调用时，会重新陷入 OS，OS 处理完成后再次通过从陷阱返回指令，返回进程。进程完成工作后，从 `main()` 返回，然后通过 `exit()` 系统调用等方式陷入 OS。OS 清理干净，至此完成。

### 6.3 问题 2：在进程之间切换

OS 实现进程之间的切换，但进程在 CPU 运行时，意味着 OS 不在运行，于是有了问题：如何让 OS 重新获得 CPU 控制权，以便实现进程切换？

#### 协作方式：等待系统调用

早期 OS 采用这种方式，OS 相信进程会合理运行，假定运行时间长的进程会定期放弃 CPU。

大多数进程通过系统调用将 CPU 控制权交给 OS。这样的 OS 通常包括一个显式的 `yield` 系统调用，它除了移交控制权外什么都不做。

如果进程执行了非法操作，也会将控制权交给 OS。例如将 `0` 作为除数，或访问应该无法访问的内存，就会陷入 OS。OS 再次控制 CPU，且可能会终止该进程。

这种方式可能造成 CPU 一直被进程占用。

#### 非协作方式：操作系统进行控制




































