---
title: 幂运算 pow(x, n) 的一个迭代实现
date: 2018-08-24 03:49:03
tags:
- LeetCode
- 算法
- 位操作
math: true
---

求一个浮点数的整数次幂：
<https://leetcode.com/problems/powx-n/> 
> <p>Implement <a href="http://www.cplusplus.com/reference/valarray/pow/" target="_blank">pow(<em>x</em>, <em>n</em>)</a>, which calculates&nbsp;<em>x</em> raised to the power <em>n</em> (i.e. x<sup><span style="font-size:10.8333px">n</span></sup>).</p>
> 
> <p>&nbsp;</p>
> <p><strong>Example 1:</strong></p>
> 
> <pre><strong>Input:</strong> x = 2.00000, n = 10
> <strong>Output:</strong> 1024.00000
> </pre>
> 
> <p><strong>Example 2:</strong></p>
> 
> <pre><strong>Input:</strong> x = 2.10000, n = 3
> <strong>Output:</strong> 9.26100
> </pre>
> 
> <p><strong>Example 3:</strong></p>
> 
> <pre><strong>Input:</strong> x = 2.00000, n = -2
> <strong>Output:</strong> 0.25000
> <strong>Explanation:</strong> 2<sup>-2</sup> = 1/2<sup>2</sup> = 1/4 = 0.25
> </pre>
> 
> <p>&nbsp;</p>
> <p><strong>Constraints:</strong></p>
> 
> <ul>
> 	<li><code>-100.0 &lt;&nbsp;x&nbsp;&lt; 100.0</code></li>
> 	<li><code>-2<sup>31</sup>&nbsp;&lt;= n &lt;=&nbsp;2<sup>31</sup>-1</code></li>
> 	<li><code>-10<sup>4</sup> &lt;= x<sup>n</sup> &lt;= 10<sup>4</sup></code></li>
> </ul>

基本就是分治思想，用递归就能很方便地实现，也可以把递归改写成迭代，但我在 Discuss 区看见了一个精奇的迭代思路[^1]。

<!-- more -->

例如，现求 $x^9$，把指数写成二进制形式，并按照每个二进制位拆分为累乘：

$$x^9 = x^{1001\_2} = x^{1000\_2} x^{000\_2} x^{00\_2} x^{1\_2}$$

观察可以发现，当对应的二进制位为 0 时，该项值为 1，可以忽略；当对应的二进制位为 1 时，该项保留，其值为 $x^1, x^2, x^4, x^8, ...$

迭代思路：从低到高依次取指数的二进制位，如果该位为 1，结果累乘，否则忽略。

C 语言实现如下：

{{< code file=leetcode/c/src/powx_n.c from=3 lang=c >}}

注意指数为负数的情况，求绝对值用 `0u - n` 先转换为无符号整数，否则当指数为 `INT_MIN` 时 LeetCode 编译器会报错。

## 实现源码

<https://github.com/qianbinbin/leetcode>

## 参考资料

[^1]: [Iterative Log(N) solution with Clear Explanation - LeetCode Discuss](https://leetcode.com/problems/powx-n/discuss/19563/Iterative-Log%28N%29-solution-with-Clear-Explanation)
