---
title: CSAPP 程序的机器级表示
date: 2020-07-06 08:40:44
tags:
- CSAPP
- 读书笔记
- C
- 汇编
- GCC
- 编译
---

本文运行环境为 i7-7700HQ + Linux debian 4.19.0-9-amd64 + GCC 8.3.0。

# 3.2 程序编码

假设一个 C 程序有两个文件 `p1.c` 和 `p2.c`，编译这些代码：

```sh
$ gcc -Og -o p p1.c p2.c
```

参数 `-Og` 打开不影响 debug 的优化等级，会生成符合原始 C 代码整体结构的机器代码。

`gcc` 命令调用一整套程序，将源代码转化成可执行代码。

- 首先，C 预处理器扩展源代码，插入所有用 `#include` 命令指定的文件，并扩展所有用 `#define` 声明指定的宏。

- 其次，编译器产生两个源文件的汇编代码 `p1.s` 和 `p2.s`。

- 接下来，汇编器会将汇编代码转化成二进制目标代码文件 `p1.o` 和 `p2.o`。

- 最后，链接器将两个目标代码文件与实现库函数（例如 `printf`）的代码合并，并产生最终的可执行代码文件 `p`（由参数 `-o o` 指定）。

## 3.2.1 机器级代码

对于机器级编程来说，两种抽象尤为重要。

第一种是由指令集架构（ISA）来定义机器级程序的格式和行为，它定义了处理器状态、指令的格式，以及每条指令对状态的影响。大多数 ISA，包括 x86-64，将程序的行为描述成好像每条指令都是按顺序执行的，实际上处理器可以并发执行许多指令，但采取措施保证整体行为与 ISA 指定的顺序执行的行为完全一致。

第二种是机器级程序使用的内存地址是虚拟地址，提供的内存模型看上去是一个非常大的字节数组。

汇编代码表示非常接近于机器代码。

x86-64 的机器代码和原始 C 代码差别非常大，一些通常对 C 语言程序员隐藏的处理器状态都是可见的：

- 程序计数器：PC，在 x86-64 中用 `%rip` 表示，给出将要执行的下一条指令的地址。

- 整数寄存器文件：包含 16 个命名的位置，分别存储 64 位的值。可以存储地址（C 语言的指针）或整数数据，有的用来记录某些重要的程序状态，其他的寄存器用来保存临时数据，例如参数和局部变量，以及函数的返回值。

- 条件码寄存器：保存最近执行的算数或逻辑指令的状态信息。用来实现控制或数据流中的条件变化，例如实现 `if` 和 `while` 语句。

- 一组向量寄存器：存放一个或多个整数或浮点数值。

程序内存包含：程序的可执行机器代码，操作系统需要的一些信息，用来管理过程调用和返回的运行时栈，以及用户分配的内存块（例如 `malloc` 函数分配的）。程序内存用虚拟地址来寻址。x86-64 的虚拟地址为 64 位，在目前的实现中，高 16 位必须设置为 0，所以地址能够实际指定的是 $2^{48}B = 256 TiB$ 以内。操作系统负责管理虚拟地址空间，将虚拟地址翻译成实际处理器内存中的物理地址。

## 3.2.2 代码示例

C 语言代码文件 `mstore.c`：

{% include_code lang:c csapp-machine-level-representation-of-programs/mstore.c %}

使用 `-S` 参数产生汇编代码：

```sh
$ gcc -Og -S mstore.c
```

这会生成汇编文件 `mstore.s`：

{% include_code lang:asm csapp-machine-level-representation-of-programs/mstore.s %}

使用 `-c` 参数编译并汇编：

```sh
$ gcc -Og -c mstore.c
```

这回生成目标代码文件 [mstore.o](/downloads/code/csapp-machine-level-representation-of-programs/mstore.o)

要查看机器代码文件的内容，可以使用反汇编器，根据机器代码产生一种类似汇编代码的格式。在 Linux 中，使用：

```sh
$ objdump -d mstore.o

mstore.o:     file format elf64-x86-64


Disassembly of section .text:

0000000000000000 <multstore>:
   0:	53                   	push   %rbx
   1:	48 89 d3             	mov    %rdx,%rbx
   4:	e8 00 00 00 00       	callq  9 <multstore+0x9>
   9:	48 89 03             	mov    %rax,(%rbx)
   c:	5b                   	pop    %rbx
   d:	c3                   	retq
```

最后几行中，第一列是偏移量（起始位置），第二列若干字节是指令的 16 进制值，第三列是等价的汇编语言。可以看到和之前生成的汇编 `mstore.s` 基本一致。

值得注意的是：

- x86-64 指令长度从 1 到 15 个字节不等。常用到指令以及操作数较少的指令所需字节数少，不太常用或操作数较多的指令所需字节数较多。

- 设计指令格式的方式是，从指定位置开始，可以将字节唯一地解码成机器指令（前缀码）。例如，只有 `pushq %rbx` 是以字节值 53 开头的。

- 反汇编器基于机器代码文件中的字节来确定汇编代码，无需访问 C 程序源码或汇编源码。

- 反汇编器使用的指令命名规则与 GCC 生成的汇编代码有一些差别，例如省略了很多指令结尾的 `q`，这是大小指示符，指 4 字长度（一个字为 16 位，共 64 字节），在大多数情况下可以省略。反汇编器也给 `call` 和 `ret` 指令添加了 `q` 后缀，省略这些后缀也没有问题。

生成实际可执行的代码需要对一组目标代码文件运行链接器，且这一组目标代码文件中必须含有一个 `main` 函数。假设有：

{% include_code lang:c csapp-machine-level-representation-of-programs/main.c %}

生成可执行文件 [prog](/downloads/code/csapp-machine-level-representation-of-programs/prog)：

```sh
$ gcc -Og -o prog main.c mstore.c
```

对其进行反汇编，得到很长的代码序列，其中包括：

```
$ objdump -d prog
...
0000000000001175 <multstore>:
    1175:	53                   	push   %rbx
    1176:	48 89 d3             	mov    %rdx,%rbx
    1179:	e8 ef ff ff ff       	callq  116d <mult2>
    117e:	48 89 03             	mov    %rax,(%rbx)
    1181:	5b                   	pop    %rbx
    1182:	c3                   	retq
    1183:	66 2e 0f 1f 84 00 00 	nopw   %cs:0x0(%rax,%rax,1)
    118a:	00 00 00
    118d:	0f 1f 00             	nopl   (%rax)

0000000000001190 <__libc_csu_init>:
...
```

这段代码与 `mstore.c` 反汇编生成的几乎一样，但有几个区别：

- 左边列出的地址不同，链接器将这段代码的地址移到了一段不同的地址范围中。

- 填上了 `callq` 指令调用函数 `mult2` 需要的地址。链接器的任务之一就是为函数调用找到匹配的函数的可执行代码的位置。

- 最后多了三行代码，这三行代码出现在返回指令之后，对程序没有影响，插入若干字节，使得下一段代码块的存放有利于发挥内存性能（类似字节对齐。书中是多了两行，使函数变为 16 字节）。

## 3.2.3 关于格式的注解

之前用 `gcc -Og -S mstore.c` 生成的 `mstore.s` 的内容：

{% include_code lang:asm csapp-machine-level-representation-of-programs/mstore.s %}

其中所有以 `.` 开头的行都是指导汇编器和链接器工作的伪指令。省略这些伪指令，这些汇编代码及对应的解释如下：

```
void multstore(long x, long y, long *dest)
x 存放于 %rdi，y 存放于 %rsi，dest 存放于 %rdx

multstore:
	pushq	%rbx			保存 %rbx
	movq	%rdx, %rbx		将 dest 复制到 %rbx
	call	mult2@PLT		调用 mult2(x, y)
	movq	%rax, (%rbx)		将结果保存到 *dest
	popq	%rbx			恢复 %rbx
	ret				返回
```

之前的表述都是 ATT（根据运营贝尔实验室的 AT&T 公司名字而来）格式的汇编代码，这是 GCC、OBJDUMP 等工具的默认格式。

如果运行 `gcc -Og -S -masm=intel mstore.c` 则会生成 Intel 格式的代码：

{% include_code lang:asm csapp-machine-level-representation-of-programs/mstore.intel.s %}

与 ATT 格式相比，Intel 格式主要有以下区别：

- 省略了指示大小的后缀，例如 `push`、`mov` 而不是 `pushq` 和 `movq`。

- 省略了寄存器名字前的 `%`，例如 `rbx` 而不是 `%rbx`。

- 使用不同的方式来描述内存中的位置，例如 `QWORD PTR [rbx]` 而不是 `(%rbx)`。

- 指令带有多个操作数时，操作数的顺序相反。

一些机器特性是 C 程序无法利用的。例如 PF 标志（奇偶标志）会指定运算结果低 8 位是否有偶数个 1。如果用纯 C 语言实现这样的功能则需要数次移位、异或运算。在 C 程序中插入几条汇编代码也可以实现。

在 C 程序中插入汇编代码有两种方法：

- 单独在汇编代码文件中编写完整函数，并让汇编器和链接器将它和 C 语言代码合并。

- 使用 GCC 的内联汇编特性，用 `asm` 伪指令可以在 C 程序中插入简短的汇编代码。不过插入这些汇编会使代码与特定机器相关。

# 3.3 数据格式

Intel 用术语“字（word）”表示 16 位数据类型。

 C 声明 | Intel 数据类型 | 汇编代码后缀 | 大小（字节）
--------|----------------|--------------|------------
 char   | 字节           | b            | 1
 short  | 字             | w            | 2
 int    | 双字           | l            | 4
 long   | 四字           | q            | 8
 char\* | 四字           | q            | 8
 float  | 单精度         | s            | 4
 double | 双精度         | l            | 8

`l` 代表的是 `long`，可以同时用来表示 4 字节整数和 8 字节浮点数而不会产生歧义，因为它们使用的是完全不同的指令和寄存器。

浮点数主要有两种形式，单精度（4 字节）值对应 C 语言的 `float`，双精度（8 字节）值对应 C 语言的 `double`。x86 处理器还实现过一种 80 位（10 字节）的浮点格式，在 C 语言中使用 `long double` 来指定，但无法移植到其他类型的机器上，实现硬件也不如单精度和双精度高效。

# 3.4 访问信息

一个 x86-64 处理器包含一组 16 个存储 64 位值的通用目的寄存器（整数寄存器），用来存储整数数据和指针。

![](/images/csapp-machine-level-representation-of-programs/integer-registers.png)

所有 16 个寄存器低位部分都可以作为字节、字、双字、四字数来访问。

对于生成小于 8 字节结果的指令，寄存器中剩下的字节会怎么样，对此有两条规则：生成 1 字节和 2 字节数的指令会保持剩下的字节不变；生成 4 字节数的指令会把高位 4 个字节置为 0。

`%rsp` 用来指明运行时栈的结束位置。

## 3.4.1 操作数指示符

操作数有三种类型：

- 立即数：用来表示常数值。

- 寄存器：表示某个寄存器的内容。

- 存储器：根据计算出的有效地址来访问某个内存位置。

![](/images/csapp-machine-level-representation-of-programs/operand-forms.png)

寻址模式的中英文对应如下，不同的中文翻译可能有所区别：

- Immediate：立即数寻址
- Register：寄存器（直接）寻址
- Absolute：绝对/直接寻址
- Indirect：（寄存器）间接寻址
- Base + displacement：基址+偏移量寻址
- Indexed：变址寻址
- Scaled indexed：比例变址寻址

底部的 $Imm(r_b,r_i,s)$ 表示的是最常用的形式，有四个组成部分：立即数偏移量 $Imm$，基址寄存器 $r_b$，变址寄存器 $r_i$ 和比例因子 $s$。基址和变址寄存器都必须为 64 位寄存器，$s$ 必须为 1、2、4 或 8。计算有效地址为 $Imm + R[r_b] + R[r_i] \cdot s$。其他形式都是这种通用形式的特殊情况。复杂的寻址模式可以用于引用数组和结构元素。

x86-64 中的内存引用总是用四字寄存器（64 位）给出。

## 3.4.2 数据传送指令

最简单形式的数据传送指令是 MOV 类，这些指令把数据从源位置复制到目的位置。MOV 类由四条指令组成：`movb`、`movw`、`movl`、`movq`。

![](/images/csapp-machine-level-representation-of-programs/simple-data-movement-instructions.png)

源操作数指定的值可以为（此处中文版翻译不正确）：

- 立即数
- 存储在寄存器中
- 存储在内存中

目的操作数指定一个位置，可以为：

- 寄存器
- 内存地址

在 x86-64 中，传送指令的两个操作数不能同为内存位置，要将内存复制到另一个内存位置需要两条指令——先将源操作数复制到寄存器中，再将寄存器的值复制到目的位置。

这些指令的寄存器操作数可以是 16 个寄存器中任意一个的标号部分（此处中文翻译错误）。寄存器部分的大小必须与指令最后一个字符指定大小匹配。

根据 x86-64 的惯例，`movl` 以寄存器作为目的时，会把该寄存器高 4 字节置为 0。

`movq` 指令使用立即数作为源操作数时，只能使用 32 位补码数，存放到目的位置时会进行符号扩展到 64 位。`movabsq` 指令能够以任意 64 位立即数作为源操作数，但只能以寄存器作为目的。

将较小的源复制到较大的目的时可以用两类数据移动指令：MOVZ 类和 MOVS 类，分别填充 0 和使用符号扩展。每条指令的最后两个字符是大小指示符，第一个字符指定源的大小，第二个指定目的的大小。

![](/images/csapp-machine-level-representation-of-programs/zero-extending-data-movement-instructions.png)

![](/images/csapp-machine-level-representation-of-programs/sign-extending-data-movement-instructions.png)

MOVZ 中没有把 4 字节源零扩展到 8 字节目的的指令，如果存在的话逻辑上应该命名为 `movzlq`。事实上这样的传送可以用 `movl` 来实现，因为它会将高 4 字节置为 0。而 MOVS 中是有 `movslq` 的。

`cltq` 指令没有操作数，它会将 %eax 作为源，%rax 作为符号扩展结果的目的，其效果与 `movslq %eax, %rax` 完全一致。
