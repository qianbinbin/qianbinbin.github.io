---
title: Single Number II
date: 2017-11-27 16:46:19
tags:
- LeetCode
- 算法
---

LeetCode 的一道题，在一个 int 数组中，其余数值均出现了 3 次，只有一个数值出现了 1 次，求这个数：
<https://leetcode.com/problems/single-number-ii/description/>

> Given an array of integers, every element appears three times except for one, which appears exactly once. Find that single one.
>
> Note:
> Your algorithm should have a linear runtime complexity. Could you implement it without using extra memory?

要求时间复杂度为 $O(n)$，并挑战读者是否能在空间复杂度 $O(1)$ 的条件下解决。

<!-- more -->

# 哈希表法

一种显而易见的方法就是用哈希表，但是空间复杂度为 $O(n)$。这里不做赘述。

# 统计二进制位出现次数

对 int 整型数的每个二进制位，统计其在所有元素中出现的次数。出现次数有 3 种情况：

1. 为 0

2. 为 3 的倍数

3. 为 1

我们只需要把出现次数模 3 后为 1 的位重新组合，就得到所求数值。

为此，我们可以定义一个长度为 int 类型比特位数，也就是`sizeof(int) * 8`长度的 int 数组，用来统计每个二进制位出现次数。

数组长度在特定机器上是固定的，因此空间复杂度为 $O(1)$。C 语言实现参考上面的链接。

# 位操作法

在上面的方法中，用一个 int 数组来统计二进制位出现次数有点浪费，事实上在统计次数时，只需要考虑 3 种情况：0、1、2，当次数到达 3 时，将其重置为 0 即可，这其实是 3 进制运算。

要保存 3 种状态，只需要 2 个比特：

  0 |  1 |  2
----|----|----
 00 | 01 | 10

int 类型的每个比特需要 2 个比特保存状态，一个保存低位比特，一个保存高位比特，那么总共只需要 2 个 int 来保存。接下来要做的就是用 2 个比特模拟 3 进制运算。

## 通过真值表写出逻辑表达式

设 A 表示低位，B 表示高位，C 表示当前统计的比特，列出真值表：

 B A | C | B'A'
-----|---|-----
 0 0 | 0 | 0 0
 0 0 | 1 | 0 1
 0 1 | 0 | 0 1
 0 1 | 1 | 1 0
 1 0 | 0 | 1 0
 1 0 | 1 | 0 0

先计算新的低位，逻辑表达式为

$$A'=\overline{B} \, \overline{A} \, C + \overline{B} \, A \, \overline{C}
    =(A\oplus{C}) \, \overline{B}$$

在编码实现时，计算 A' 覆盖了原先的 A，再计算 B' 时就需要用 A' 计算：

$$B'=\overline{B} \, C \, \overline{A'} + B \, \overline{C} \, \overline{A'}
    =(B\oplus{C}) \, \overline{A'}$$

用 C 语言编码为：

```c
int one = 0, two = 0;
for (int i = 0; i < numsSize; ++i) {
    one = (one ^ nums[i]) & ~two;
    two = (two ^ nums[i]) & ~one;
}
```

最终的`one`恰好就是所求的数值，而`two`理论上为 0，因为根据题中条件不会有出现 2 次的情况。

## 复杂度

时间复杂度为 $O(n)$，空间复杂度为 $O(1)$，只用了两个`int`。

# 实现源码

<https://github.com/qianbinbin/leetcode>

# 参考资料

1. [Single Number II - LeetCode](https://leetcode.com/problems/single-number-ii/discuss/43294/Challenge-me-thx)
