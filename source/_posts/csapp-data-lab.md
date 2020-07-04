---
title: CSAPP Data Lab
date: 2020-07-02 19:06:04
tags:
- CSAPP
- 算法
- 位操作
---

Data Lab 的题[^1]，第一眼觉得不难，仔细一看发现限制非常严格，比如只允许部分位运算符等，难度一下子就上去了，所以花了不少时间。

`dlc` 是 64 位 Linux 程序，使用 `./dlc bits.c` 来检查是否符合限制条件。但 `Makefile` 里却指定了 `-m32`，可能是考虑到 C 标准只规定 `int` 至少为 16 位，一些环境可能会生成非 32 位的 `int`。

Debian 系 64 位 Linux 发行版可以安装：

```sh
$ sudo apt install build-essential gcc-multilib g++-multilib
```

后两个库用于在 64 位环境下编译 32 位程序。

编译并运行测试程序：

```sh
$ make clean && make btest && ./btest
```

<!-- more -->

# bitXor

用 `~` 和 `&` 来实现 `^`：

{% include_code lang:c from:138 to:147 csapp-labs/datalab-handout/bits.c %}

根据

$$A \oplus{B} = A \cdot \overline{B} + \overline{A} \cdot B$$

来实现 `^`，再根据

$$A + B = \overline{\overline{A + B}} = \overline{\overline{A} \cdot \overline{B}}$$

来实现 `|`。

# tmin

求补码最小值，送分题：

{% include_code lang:c from:148 to:156 csapp-labs/datalab-handout/bits.c %}

# isTmax

判断是否为 `int` 最大值 `0x7fffffff`，只允许使用 `! ~ & ^ | +`：

{% include_code lang:c from:158 to:170 csapp-labs/datalab-handout/bits.c %}

先将问题转化为判断是否为最大值的取反 `0x80000000`。

利用补码的特性，`int` 类型可以用取反加一来实现求相反数，而只有两个数满足与自身的相反数相等，即 `0` 和 `0x80000000`，其中后者是因为溢出。

再利用逻辑非 `!` 排除 `0` 即可。

# allOddBits

判断是否所有奇数位（最低位为 0 位）都为 `1`：

{% include_code lang:c from:171 to:184 csapp-labs/datalab-handout/bits.c %}

只允许使用 `0` 到 `0xff` 之间的常数，故先用移位求 `0xaaaaaaaa`，再用 `^` 实现 `==`。

# negate

求相反数，送分题：

{% include_code lang:c from:185 to:194 csapp-labs/datalab-handout/bits.c %}

# isAsciiDigit

判断是否为 `'0'` 到 `'9'` 之间的 ASCII 码：

{% include_code lang:c from:196 to:213 csapp-labs/datalab-handout/bits.c %}

用求相反数和 `+` 来实现 `-`，再取符号位判断其正负。

中间可能有溢出的问题，当 `x - 0x30 >= 0` 时，可能 `x` 是一个很小的负数，负负得正产生溢出；当 `0x39 - x >= 0` 时，不会产生溢出，从而把前一种溢出的情况排除。

# conditional

实现三元运算符 `a ? b : c`：

{% include_code lang:c from:214 to:225 csapp-labs/datalab-handout/bits.c %}

将非零数转换为掩码 `0xffffffff`，零转换为掩码 `0`。

# isLessOrEqual

实现 `<=`：

{% include_code lang:c from:226 to:238 csapp-labs/datalab-handout/bits.c %}

问题转化为判断 `-x + y` 正负。

考虑溢出的问题，取 `x`、`y` 的符号位，不同号时直接判断，若同号再判断 `-x + y` 正负。

# logicalNeg

实现逻辑非 `!`：

{% include_code lang:c from:240 to:258 csapp-labs/datalab-handout/bits.c %}

第一反应是将首个 `1` 之后的位全部置 `1`，若原值非零，则最低位为 `1`，否则为 `0`，然后将最低位直接取反即可。

后来发现了一种更简便的方法[^2]，除 `0` 和 `0x80000000` 外，一个数必定与其相反数异号，若将 `x` 与 `-x` 符号异或，则包括 `0x80000000` 在内 ，所有非零数异或结果必定为 `1`。

# howManyBits

求补码表示的最少位数：

{% include_code lang:c from:259 to:281 csapp-labs/datalab-handout/bits.c %}

个人认为是最难的一题，参考了网上的答案[^3]，并对其作了改进。

## 如何确定最少位数

对于非负数而言，开头连续的 `0` 可以去掉，只需保留其中最后一个 `0` 作为符号位即可（否则就会被当成负数了）。例如，`0001...` 与 `01...` 表示的是相等值。

对于负数而言，同样从符号位开始，开头连续的 `1` 只需保留最后一个即可。例如，`1110...` 与 `10...` 表示的是相等值。

## 将负数的情况转化为非负数的情况

`x = x ^ (x >> 31)` 的作用就是，若 `x` 为非负，则保持不变，若为负数，则取反。这样无论 `x` 符号，都可以按照非负数的情况来求解。

## 求最高位的 `1` 到最低位的长度，最终结果就是长度 + 1

这个步骤需要一些技巧。

例如，`01** **** **** **** **** **** **** ****` 的最高位 1 到最低位是 31 位，最终结果就是 32。

观察 31 = 16 + 8 + 4 + 2 + 1，与 `x` 右移试零的过程一致：首先取 `x` 最高 16 位，若非零，说明需要 16 位以上的位数，此时 `n` 计数 + 16，并将低 16 位去掉。接着尝试将低 8 位去掉，以此类推，直到 `x` 只剩一位。手动模拟一下会更容易理解。

上个步骤已经排除了负数的情况，所以无需担心算数右移高位产生 1 的问题。

```
01** **** **** **** **** **** **** ****

n = 16

01** **** **** ****

n = 16 + 8

01** ****

n = 16 + 8 + 4

01**

n = 16 + 8 + 4 + 2

01

n = 16 + 8 + 4 + 2 + 0

01

n = 16 + 8 + 4 + 2 + 0 + 1
```

# floatScale2

书上的习题，求 $2x$：

{% include_code lang:c from:283 to:308 csapp-labs/datalab-handout/bits.c %}

浮点数题目的限制比前面宽松很多，虽然构造复杂一点，只要按照定义求解即可。

# floatFloat2Int

书上的习题，`float` 转换为 `int`：

{% include_code lang:c from:309 to:332 csapp-labs/datalab-handout/bits.c %}

`float` 范围比 `int` 更广，但精度（有效数字）更低，注意溢出的情况。

# floatPower2

书上的习题，求 $2^x$：

{% include_code lang:c from:333 to:357 csapp-labs/datalab-handout/bits.c %}

# 参考资料

[^1]: [CS:APP3e, Bryant and O'Hallaron](http://csapp.cs.cmu.edu/3e/labs.html)

[^2]: [Introduction to CSAPP（八）：Datalab - 知乎](https://zhuanlan.zhihu.com/p/82529114)

[^3]: [CSAPP DataLab 题解 | Claude's Blog](http://claude-ray.com/2019/10/02/csapp-datalab/#howManyBits)
