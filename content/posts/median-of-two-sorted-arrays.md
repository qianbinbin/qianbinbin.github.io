---
title: 求两个有序数组的中位数 Median of Two Sorted Arrays
date: 2017-07-24 22:47:04
tags:
- LeetCode
- 算法
---

这是 LeetCode 上的一道题，求两个有序数组的中位数：
<https://leetcode.com/problems/median-of-two-sorted-arrays/description/>

> <p>Given two sorted arrays <code>nums1</code> and <code>nums2</code> of size <code>m</code> and <code>n</code> respectively, return <strong>the median</strong> of the two sorted arrays.</p>
> 
> <p><strong>Follow up:</strong> The overall run time complexity should be <code>O(log (m+n))</code>.</p>
> 
> <p>&nbsp;</p>
> <p><strong>Example 1:</strong></p>
> 
> <pre><strong>Input:</strong> nums1 = [1,3], nums2 = [2]
> <strong>Output:</strong> 2.00000
> <strong>Explanation:</strong> merged array = [1,2,3] and median is 2.
> </pre>
> 
> <p><strong>Example 2:</strong></p>
> 
> <pre><strong>Input:</strong> nums1 = [1,2], nums2 = [3,4]
> <strong>Output:</strong> 2.50000
> <strong>Explanation:</strong> merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5.
> </pre>
> 
> <p><strong>Example 3:</strong></p>
> 
> <pre><strong>Input:</strong> nums1 = [0,0], nums2 = [0,0]
> <strong>Output:</strong> 0.00000
> </pre>
> 
> <p><strong>Example 4:</strong></p>
> 
> <pre><strong>Input:</strong> nums1 = [], nums2 = [1]
> <strong>Output:</strong> 1.00000
> </pre>
> 
> <p><strong>Example 5:</strong></p>
> 
> <pre><strong>Input:</strong> nums1 = [2], nums2 = []
> <strong>Output:</strong> 2.00000
> </pre>
> 
> <p>&nbsp;</p>
> <p><strong>Constraints:</strong></p>
> 
> <ul>
> 	<li><code>nums1.length == m</code></li>
> 	<li><code>nums2.length == n</code></li>
> 	<li><code>0 &lt;= m &lt;= 1000</code></li>
> 	<li><code>0 &lt;= n &lt;= 1000</code></li>
> 	<li><code>1 &lt;= m + n &lt;= 2000</code></li>
> 	<li><code>-10<sup>6</sup> &lt;= nums1[i], nums2[i] &lt;= 10<sup>6</sup></code></li>
> </ul>

确定一下输入应满足的条件：两个数组的长度可以有一个为 $0$（此时中位数就是另一个数组的中位数），但不能同时为 $0$。

很容易想到时间复杂度为 $O(m+n)$ 的方法，但是 follow-up 要求复杂度为 $O(\log(m+n))$。从复杂度看应该用分治法。事实上，复杂度可以减少到 $O(\log(\min(m, n)))$。

<!-- more -->

## 划分

设中位数为 $x$，$x$ 可将两个数组分别划分为两半：

```
A[0], A[1], ..., A[i - 1] | A[i], A[i + 1], ..., A[m - 1]
B[0], B[1], ..., B[j - 1] | B[j], B[j + 1], ..., B[n - 1]
```

其中 $0 \le i \le m, 0 \le j \le n$。

如果满足两个条件：

- $i + j = \begin{cases} m - i + n - j, & \text{$m + n$ 为偶数}\\
m - i + n - j + 1, & \text{$m + n$ 为奇数}
\end{cases}$
- 左边所有元素 $\le x \le$ 右边所有元素，

则找到了适当的划分位置，且

$$
x = \begin{cases}
(\max\{A[i - 1], B[j - 1]\} + \min\{A[i + 1], B[j + 1]\}) \div 2, & \text{$m + n$ 为偶数}\\
\max\{A[i - 1], B[j - 1]\}, & \text{$m + n$ 为奇数}
\end{cases}
$$

由此问题转化为：查找这样的 $i$，且同时满足以上两个条件，即可得出中位数 $x$。

- 根据第一个条件，在编程中可以直接用截断除法得出 $j$：

```c
j = (m + n + 1) / 2 - i;
```

- 若不考虑下标越界情况，由于两个数组升序，第二个条件等价于：

$$
\begin{cases}
A[i - 1] \le x \leq A[i]&\\
B[j - 1] \le x \leq B[j]
\end{cases}
\iff
\begin{cases}
A[i - 1] \le B[j]&\\
B[j - 1] \le A[i]
\end{cases}
$$

## 二分查找

### $j$ 的取值范围

现考察 $j$ 的范围，$j = \frac{m + n + k}{2} - i$，其中

$$
k = \begin{cases}
0, & \text{$m + n$ 为偶数}\\
1, & \text{$m + n$ 为奇数}
\end{cases}
$$

$j$ 随 $i$ 递减，只需

$$
\begin{cases}
j_{max} = (m + n + k) \div 2 - 0 \le n&\\
j_{min} = (m + n + k) \div 2 - m \ge 0
\end{cases}
\iff
m \le n - k
$$

即可保证 $0 \leq j \leq n$。

- 当 $m + n$ 为偶数时，$k = 0$，则 $m \leq n$。
- 当 $m + n$ 为奇数时，$k = 1$，则 $m \leq n - 1$，又 $m$、$n$ 必为一奇一偶，因此 $m < n$。

综上得 $m \le n$。

因此当 $m \le n$ 时，若 $0 \le i \le m$，则必有 $0 \le j \le n$。

在编程实现时，保证数组长度 $A$ 比 $B$ 小即可。

### 查找思路

要在 $i = 0, 1, ..., m$ 中查找合适的 $i$，自然联想到二分查找。初始情况下，$i$ 的下界为 $0$，上界为 $m$。

若不考虑下标越界，则有以下几种情形：

- $\begin{cases} A[i-1] \le B[j] &\\ B[j-1] \le A[i] \end{cases}$，此时 $i$ 查找成功。
- $A[i-1] > B[j]$，此时 $i$ 过大，应丢弃后半部分。
- $B[j-1] > A[i]$，此时 $i$ 过小，应丢弃前半部分。

考虑下标越界情况：

- $i = 0$，无需判断 $A[i-1] \le B[j]$。
- $i = m$，无需判断 $B[j-1] \le A[i]$。
- $j = 0$，无需判断 $B[j-1] \le A[i]$。
- $j = n$，无需判断 $A[i-1] \le B[j]$。

结合越界情况，会出现以下三种情形：

- $\begin{cases} i=0 \ 或\ j=n \ 或\ A[i-1] \le B[j] &\\ j=0 \ 或\ i=m \ 或\ B[j-1] \le A[i] \end{cases}$，此时 $i$ 查找成功。
- $i>0 \ 且\ j < n \ 且\ A[i-1] > B[j]$，此时 $i$ 过大。
- $j>0 \ 且\ i < m \ 且\ B[j-1] > A[i]$，此时 $i$ 过小。

这样，通过二分查找，找到所需要的 $i$ 的值。

也可以反过来考虑，合适的 $0 \le i \le m$ 必定存在，先考虑 $i$ 过大和过小的情形。

例如，若 $i$ 过大，要在 $< i$ 中查找，不可能有 $i = 0$ 或 $j = n$，因为 $i$ 减小同时 $j$ 增大后必定越界，而 $A[i-1] > B[j]$ 是一定成立的，所以 $i$ 过大 $\iff \begin{cases} i>0 &\\ j < n &\\ A[i-1] > B[j] \end{cases}$。排除过大和过小的情形，剩下只能是查找成功的情形。

## 计算中位数

现在已经找出了划分位置，如果共有奇数个元素，则中位数为左边元素的最大值，如果共有偶数个元素，则中位数为左边最大值和右边最小值的平均数。

例如求左边最大值：

- 若 $i = 0$，左边最大值即为 $B[j - 1]$，此时 $j > 0$ 必成立。
- 若 $j = 0$，左边最大值即为 $A[i - 1]$，此时 $i > 0$ 必成立。
- 若 $\begin{cases} i > 0 &\\ j > 0 \end{cases}$，左边最大值为 $\max\{A[i - 1], B[j - 1]\}$。

## 时间复杂度

在长度为 $m$ 的数组 $A$ 中进行二分查找，由于保证了 $m \leq n$，因此时间复杂度为 $O(\log(\min(m, n)))$。

## 实现源码

<https://github.com/qianbinbin/leetcode>
