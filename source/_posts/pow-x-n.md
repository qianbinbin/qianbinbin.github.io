---
title: 幂运算 pow(x, n) 的一个迭代实现
date: 2018-08-24 03:49:03
mathjax: true
tags:
- LeetCode
- 算法
- 位操作
---

求一个浮点数的整数次幂：
<https://leetcode.com/problems/powx-n/>

> Implement pow(x, n), which calculates x raised to the power n ($x^n$).
> 
> Example 1:
> 
> Input: 2.00000, 10
> Output: 1024.00000
> 
> Example 2:
> 
> Input: 2.10000, 3
> Output: 9.26100
> 
> Example 3:
> 
> Input: 2.00000, -2
> Output: 0.25000
> Explanation: $2^{-2}$ = $1/2^2$ = 1/4 = 0.25
> 
> Note:
> 
> -100.0 < x < 100.0
> n is a 32-bit signed integer, within the range [$−2^{31}$, $2^{31} − 1$]


基本就是分治思想，用递归就能很方便地实现。也可以直接把递归改写成迭代，但我在 Discuss 区看见了一个精奇的迭代思路。

<!-- more -->

例如，现求 $x^9$，把指数写成 2 进制形式，并按照每个二进制位拆分为累乘：

$$
x^9
= x^{1001}
= x^{1000} x^{000} x^{00} x^1
$$

观察可以发现，当对应的二进制位为 0 时，该项值为 1，可以忽略；当对应的二进制位为 1 时，该项保留，其值为 $x^1, x^2, x^4, x^8, ...$

迭代思路：从低到高依次取指数的二进制位，如果该位为 1，结果累乘，否则忽略，每次迭代后 $x$ 均需要平方。

Java 实现如下：

```java
public double myPow(double x, int n) {
    if (n == 0) return 1;

    long e = Math.abs((long) n);
    double result = 1;
    while (e > 0) {
        if ((e & 1) == 1)
            result = result * x;
        x *= x;
        e >>= 1;
    }
    return n > 0 ? result : 1 / result;
}
```

注意指数为 0 和负数的情况，当指数为 int 最小值的情况也要做防溢出处理。

# 其他语言实现

<https://github.com/qianbinbin/leetcode>

# 参考资料

1. [Iterative Log(N) solution with Clear Explanation - LeetCode Discuss](https://leetcode.com/problems/powx-n/discuss/19563/Iterative-Log%28N%29-solution-with-Clear-Explanation)
