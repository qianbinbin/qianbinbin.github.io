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

这会生成目标代码文件 [mstore.o](/downloads/code/csapp-machine-level-representation-of-programs/mstore.o)

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
multstore:
	pushq	%rbx		; 保存 %rbx
	movq	%rdx, %rbx	; 将 dest 复制到 %rbx
	call	mult2@PLT	; 调用 mult2(x, y)
	movq	%rax, (%rbx)	; 将结果保存到 *dest
	popq	%rbx		; 恢复 %rbx
	ret			; 返回
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

![](/images/csapp-machine-level-representation-of-programs/sizes-of-c-data-types-in-x86-64.png)

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

`cltq` 指令没有操作数，它会将 `%eax` 作为源，`%rax` 作为符号扩展结果的目的，其效果与 `movslq %eax, %rax` 完全一致。

## 3.4.3 数据传送示例

C 代码：

{% include_code lang:c csapp-machine-level-representation-of-programs/exchange.c %}

其[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/exchange.s)的核心功能如下（参数 `xp` 和 `y` 分别保存在 `%rdi` 和 `%rsi` 中）：

```asm
exchange:
	movq	(%rdi), %rax	; 将 xp 指向内存中的 8 字节传送到 %rax 中作为返回值
	movq	%rsi, (%rdi)	; 将 %rsi 中的内容传送到 %rdi 指向的内存，实现了 *xp = y
	ret			; `%rax` 从函数返回一个值
```

## 3.4.4 压入和弹出栈数据

在 x86-64 中，程序栈存放在内存中某个区域，栈顶元素地址最低，栈底元素地址最高。栈指针 `%rsp` 保存栈顶元素的地址。

![](/images/csapp-machine-level-representation-of-programs/push-and-pop-instructions.png)

`pushq` 和 `popq` 都只有一个操作数。

`pushq %rbp` 等价于以下两条指令：

```asm
subq $8,%rsp
movq %rbp,(%rsp)
```

但 `pushq` 指令编码只有一个字节，而上面两条指令需要 8 个字节。

`popq %rax` 等价于以下两条指令：

```asm
movq (%rsp),%rax
addq $8,%rsp
```

栈和程序代码以及其他形式的程序数据都是放在同一内存中，所以程序可以用标准的内存寻址方法访问栈内的任意位置。

# 3.5 算术和逻辑操作

![](/images/csapp-machine-level-representation-of-programs/integer-arithmetic-operations.png)

这些操作分为四组：

- 加载有效地址。

- 一元操作：自增、自减、取负、取反。

- 二元操作：加、减、乘、异或、或、与。

- 移位：左移、算术右移、逻辑右移。

## 3.5.1 加载有效地址

`leaq` 实际上是 `movq` 的变形，作用是将有效地址写入到目的操作数，可以实现 C 语言中的取址 `&`。

此外还可以实现简单的算术操作，例如，如果 `%rdx` 的值为 $x$，那么指令 `leaq 7(%rdx,%rdx,4),%rax` 就可以将寄存器 `%rax` 的值设置为 $5x + 7$。如果是 `movq 7(%rdx,%rdx,4),%rax` 的话，就是将该地址指向的内存中的 8 字节传送到 `%rax`。编译器经常会利用 `leaq` 的一些灵活用法。

例如，有 C 程序：

{% include_code lang:c csapp-machine-level-representation-of-programs/scale.c %}

GCC 使用 `-O1` 及以上优化级别时，得到[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/scale.s)的核心功能如下：

```
scale:
	leaq	(%rdi,%rsi,4), %rax	; x + 4*y
	leaq	(%rdx,%rdx,2), %rdx	; z + 2*z = 3*z
	leaq	(%rax,%rdx,4), %rax	; (x+4*y) + 4*(3*z) = x + 4*y + 12*z
	ret
```

`leaq` 能执行加法和有限形式的乘法，在编译如上简单的算术表达式时非常有用。

## 3.5.2 一元和二元操作

一元操作只有一个操作数，既是源又是目的，操作数可以为寄存器或内存位置。例如 `incq (%rsp)` 会使栈顶的 8 字节元素加 1。这种语法让人想起 C 语言中的 `++` 和 `--` 运算符。

二元操作中的第二个操作数既是源又是目的，这种语法让人想起 C 语言中类似 `x -= y` 的运算。注意目的操作数是第二个，例如 `subq %rax,%rdx` 会使寄存器 `%rdx` 的值减去 `%rax` 的值。第一个操作数可以是立即数、寄存器或内存位置，第二个操作数可以是寄存器或内存位置。当第二个操作数为内存位置时，处理器必须从内存读出，执行操作，再把结果写回内存。

## 3.5.3 移位操作

第一个操作数是移位量，第二个操作数指定要移位的数。移位量要么为立即数，要么存放在 `%cl` 这个单字节寄存器中。

x86-64 对 $w$ 位长的数据进行移位操作时，移位量是由 `%cl` 中的低 $m$ 位决定，其中 $2^m=w$。例如，如果 `%cl` 的内容为 `0xFF`，则 `salb` 会左移 7 位（$8=2^3$，取 `%cl` 低 3 位，则移位量为 7），`salw` 会左移 15 位，`sall` 会左移 31 位，`salq` 会左移 63 位。

左移指令 SAL 和 SHL 的效果是一样的。右移指令，SAR 执行算术移位，SHR 执行逻辑移位。目的操作数可以为寄存器或内存位置。

## 3.5.4 讨论

有 C 程序：

{% include_code lang:c csapp-machine-level-representation-of-programs/arith.c %}

GCC 生成[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/arith.s)的指令顺序和书中有区别，不影响结果。其核心功能如下：

```
arith:
	leaq	(%rdx,%rdx,2), %rax	; 3*z
	salq	$4, %rax		; t2 = 16 * (3*z) = 48*z
	xorq	%rsi, %rdi		; t1 = x ^ y
	andl	$252645135, %edi	; t1 & 0x0F0F0F0F
	subq	%rdi, %rax		; 返回 t2 - t3
	ret
```

`t1` 重用了 `x` 的寄存器 `%rdi`。`%rax` 先后存放 `3*z`、`48*z`、`t4`（作为返回值）的值。通常编译器产生的代码中，会用一个寄存器存放多个程序值，还会在寄存器之间传送程序值。

## 3.5.5 特殊的算术操作

两个 64 位整数相乘得到的乘积需要 128 位来表示。x86-64 对 128 位数的操作提供有限的支持。

![](/images/csapp-machine-level-representation-of-programs/special-arithmetic-operations.png)

`imulq` 指令有两种形式：

- 当有两个操作数时，它从两个 64 位操作数产生 64 位乘积（第二章 2.3.5 中已经证明，截断乘法用于补码和无符号整数时产生乘积的位级表示完全相同），这就是 IMUL 指令类的一种。

- 当只有一个操作数时，它可以计算两个 64 位值的全 128 位乘积，`imulq` 用于补码乘法，`mulq` 用于无符号乘法。它会将操作数与 `%rax` 中的值相乘，低位 64 位仍存放于 `%rax` 中，高 64 位存放于 `%rdx` 中。

不存在 `mulq` 有两个操作数的情况，因为补码和无符号整数共用截断乘法指令 `imulq`。

下面的 C 代码对 64 位无符号数产生 128 位全乘法：

{% include_code lang:c csapp-machine-level-representation-of-programs/store_uprod.c %}

C 标准不包括 128 位整数类型，但 GCC 提供了支持，使用 `__int128` 来声明。

其[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/store_uprod.s)的核心功能如下：

```asm
store_uprod:
	movq	%rsi, %rax	; 将 x 传送到 %rax
	mulq	%rdx		; x 乘以 y
	movq	%rax, (%rdi)	; 将乘积低 64 位保存到 dest
	movq	%rdx, 8(%rdi)	; 将乘积高 64 位保存到 dest+8
	ret
```

这里的环境是小端存储，因此低位保存在低地址，高位保存在高地址。

除法和取模操作由单操作数除法指令提供，与单操作数乘法指令类似。

有符号除法指令 `idivl` 将隐含的 128 位数作为被除数（高 64 位在 `%rdx` 中，低 64 位在 `%rax` 中），将操作数作为除数，将商保存在 `%rax` 中，将余数保存在 `%rdx` 中。

如果被除数是 64 位，那么它保存在 `%rax` 中，`%rdx` 应该全部设置为 0（无符号运算）或 `%rax` 的符号位（有符号运算）。后者可以用 `cqto` 指令来完成（对应 Intel 的 `cqo`，这是两者名称不匹配的少数情况之一），`cqto` 会隐含地读出 `%rax` 符号位，并填充到 `%rdx` 中。`cqto` 意为 Convert to oct word，即符号扩展为 128 位。

C 代码计算 64 位有符号数的商和余数：

{% include_code lang:c csapp-machine-level-representation-of-programs/remdiv.c %}

这里生成的[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/remdiv.s)与书中有区别，不影响结果。核心功能如下：

```asm
remdiv:
	movq	%rdi, %rax	; 将 x 传送到 %rax 作为被除数低 64 位
	movq	%rdx, %rdi	; 保存 qp
	cqto			; 将 x 符号扩展到 %rdx
	idivq	%rsi		; 除以 y
	movq	%rax, (%rdi)	; 将商保存到 qp 位置
	movq	%rdx, (%rcx)	; 将余数保存到 rp 位置
	ret
```

类似地，无符号除法使用 `divq` 指令，通常 `%rdx` 会被提前置为 0，例如使用 `movl $0,%edx`。

# 3.6 控制

C 语言中的某些结构，比如条件语句、循环语句、分支语句，要求有条件的执行。机器代码提供两种基本的低级机制来实现有条件的行为：测试数据值，并基于测试的结果来改变控制流或数据流。

与数据相关的控制流是实现有条件行为的更一般和更常见的方法。`jump` 指令可以改变一组机器代码指令的执行顺序。

## 3.6.1 条件码

CPU 维护着一组单个位的条件码寄存器，描述最近的算术或逻辑操作的属性：

- CF：进位标志。最近的操作使最高位产生了进位。可用来检查无符号操作的溢出。

- ZF：零标志。最近的操作得出的结果为 0。

- SF：符号标志。最近的操作得到的结果为负数。

- OF：溢出标志。最近的操作导致一个补码溢出——正溢出或负溢出。

例如使用 ADD 指令完成 C 表达式整数加法 `t = a + b` 的功能，根据以下 C 表达式来设置条件码：

```
CF      (unsigned) t < (unsigned) a             无符号溢出
ZF      (t == 0)                                零
SF      (t < 0)                                 负数
OF      (a < 0 == b < 0) && (t < 0 != a < 0)    有符号溢出
```

无符号和补码溢出的判断方法在第二章有过讨论。

执行不同指令，条件码的设置：

- 逻辑操作：CF 和 OF 会置为 0。

- 移位操作：CF 会置为最后一个被移出的位，OF 会置为 0。

- INC 和 DEC 指令：会设置 OF 和 ZF，但不会改变 CF。

- CMP 指令：根据两个操作数之差来设置条件码，除不更新目的操作数（中文版将 destinations 翻译为“目的寄存器”是错误的，因为目的操作数可以不为寄存器）外，与 SUB 指令行为相同。注意 ATT 格式中操作数的顺序是相反的。

- TEST 指令：根据两个操作数按位与的结果来设置条件码，除不更新目的操作数外，与 AND 指令行为相同。典型用法：
  - 使用两个重复的操作数来测试这个数本身，例如用 `testq %rax, %rax` 来测试 `%rax` 是正数、负数还是零。此时 SET 指令（见下一节）的效果就相当于将 `%rax` 与 0 比较。
  - 其中一个操作数是掩码，只测试那些需要的位。

![](/images/csapp-machine-level-representation-of-programs/comparison-and-test-instructions.png)

## 3.6.2 访问条件码

条件码不直接读取，使用条件码的三个常用方法：

- 根据条件码的某种组合，将一个字节设置为 0 或 1。

- 条件跳转到程序的其他部分。

- 条件传送数据。

第一种方法使用 SET 类指令：

![](/images/csapp-machine-level-representation-of-programs/the-set-instructions.png)

它们之间的区别就是条件码的不同组合。需要注意的是，指令后缀代表的是不同条件，而不是操作数大小。例如 `setl` 表示“小于时设置（set less）”，`setb` 表示“低于时设置（set below）”。

SET 指令的目的操作数可以为单字节寄存器或内存位置，指令只将一个字节设置为 0 或 1。如果需要多个字节的结果，则必须对高位清零。

例如 C 程序：

{% include_code lang:c csapp-machine-level-representation-of-programs/comp.c %}

[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/comp.s)核心功能如下：

```asm
comp:
	cmpq	%rsi, %rdi	; 比较 a 和 b
	setl	%al		; 将结果写入单字节寄存器 %al
	movzbl	%al, %eax	; 零扩展到 64 位
	ret
```

这里比较的是 `a` 和 `b`，`compq` 指令操作数顺序是相反的。`movzbl` 进行零扩展时，不仅会零扩展到 32 位 `%eax`，同时也将 `%rax` 高 32 位清零。

一些底层机器指令可能有多个名字，即同义名（Synonym）。

机器代码不区分数据类型（无符号数还是补码），只会使用不同的指令来处理不同数据类型的操作，例如不同的右移、除法、乘法指令，以及不同的条件码组合。

SET 指令的描述适用的情况是：执行比较指令，根据 $t = a - b$ 设置条件码。例如：

- `sete`：相等时设置，即 $t = 0$，ZF = 0。

- `setl`：有符号小于时设置。根据 $t = a - b$ 运算结果：
  - 不溢出 OF = 0：当且仅当 $t < 0$，即 SF = 1 时设置。
  - 正溢出 OF = 1：$a > 0, b < 0, t < 0$，此时 $a > b$，SF = 1，结果为 0。
  - 负溢出 OF = 1：$a < 0, b > 0, t \ge 0$，此时 $a < b$，SF = 0，结果为 1。

   OF | SF | `setl` 结果
  ----|----|-------------
   0 | 0 | 0
   0 | 1 | 1
   1 | 0 | 1
   1 | 1 | 0

  综合以上得 `setl` 结果应为 SF ^ OF。类似可以得出 `setle`、`setg`、`setge` 的结果。

- `setb`：无符号小于时设置。CF 在加法进位和减法借位时置为 1。（减法运算也通过加法实现，将减数取反 + 1，然后与被减数相加即可，此时的进位取反就是借位 CF。）

## 3.6.3 跳转指令

跳转指令会导致执行切换到全新的位置，跳转的目的通常用一个标签来指明。

```asm
	movq	$0, %rax	; 将 %rax 置为 0
	jmp	.L1		; 跳转到 .L1
	movq	(%rax), %rdx	; 空指针解引用（跳过）
.L1:
	popq	%rdx		; 跳转目标
```

`jmp .L1` 会导致程序跳转到 `popq` 指令继续执行。在生成目标代码文件时，汇编器会确定所有被标记指令的地址，并将跳转目标（目标指令的地址）编码为跳转指令的一部分。

`jmp` 指令是无条件跳转，它可以为：

- 直接跳转：跳转目标作为指令的一部分编码。汇编语言中，使用标签作为跳转目标，例如上面的 `.L1`。

- 间接跳转：跳转目标从寄存器或内存位置读出。汇编语言中，使用 `*` 后跟一个操作数指示符。

不同的跳转指令：

![](/images/csapp-machine-level-representation-of-programs/the-jump-instructions.png)

除 `jmp` 外其他跳转指令都是有条件的，他们根据条件码的某种组合跳转或者继续执行下一条指令。这些指令的命名和跳转条件与 SET 指令的命名和跳转条件是匹配的。

条件跳转只能是直接跳转。

## 3.6.4 跳转指令的编码

在汇编代码中，跳转目标用标签书写。汇编器和链接器会生成跳转目标的适当编码。

跳转指令有几种不同的编码，最常用的是 PC 相对的，即将目标指令地址与紧跟在跳转指令后的指令地址之间的差作为编码，这些地址偏移量可以编码为 1、2 或 4 个字节。第二种编码方法是给出绝对地址，用 4 个字节之间指定目标。汇编器和连接器会选择适当的编码。

下面是一个 PC 相对寻址的例子，C 程序：

{% include_code lang:c csapp-machine-level-representation-of-programs/branch.c %}

我本地生成的[汇编代码](/downloads/code/csapp-machine-level-representation-of-programs/branch.s)与书中不同，以书上的为例：

```asm
	movq	%rdi, %rax
	jmp	.L2
.L3:
	sarq	%rax
.L2:
	testq	%rax, %rax
	jg	.L3
	rep; ret
```

将目标文件反汇编结果如下：

```asm
    0:  48 89 f8        mov    %rdi,%rax
    3:  eb 03           jmp    8 <loop+0x8>
    5:  48 d1 f8        sar    %rax
    8:  48 85 c0        test   %rax,%rax
    b:  7f f8           jg     5 <loop+0x5>
    d:  f3 c3           repz retq
```

第 2 行 `jmp` 指令中的目标编码为 `0x03`，将下一条指令的地址 `0x5` 与 `0x03` 相加，即得到跳转目标地址 `0x8`。

第 5 行 `jg` 指令中的目标代码为 `0xf8`，即十进制 `-8`，将其与下一条指令的地址 `0xd` 相加，得到跳转目标地址 `0x5`。

当执行 PC 相对寻址时，PC 的值是跳转指令后面的那条指令的地址（而不是跳转指令本身的地址）。

将链接后的程序反汇编结果如下：

```asm
    4004d0:  48 89 f8        mov    %rdi,%rax
    4004d3:  eb 03           jmp    4004d8 <loop+0x8>
    4004d5:  48 d1 f8        sar    %rax
    4004d8:  48 85 c0        test   %rax,%rax
    4004db:  7f f8           jg     4004d5 <loop+0x5>
    4004dd:  f3 c3           repz retq
```

这些指令被重定位到不同的地址，但跳转目标的编码并没有变，通过使用 PC 相对的跳转目标编码，指令编码很简洁，而且目标代码无需修改就可以移动到内存中的不同位置。

上面汇编代码中的 `rep; ret`，对应反汇编的 `repz retq`，其中 `repz` 是 `rep` 的同义名，`retq` 是 `ret` 的同义名。`rep` 指令用于重复字符串，这里的作用是避免使 `ret` 成为条件跳转指令的目标。当分支不跳转时，跳转指令会继续执行接下来的 `ret` 指令，此时 AMD 处理器无法正确预测 `ret` 指令的目标，而插入一个 `rep` 作为一种空操作，AMD 处理器就可以运行更快。不会改变代码行为。

我在 Intel 处理器上生成的汇编没有出现这个指令。但这本书的作者似乎也是用的 Intel。

跳转指令提供了一种实现条件执行（`if`）以及循环结构的方法。
