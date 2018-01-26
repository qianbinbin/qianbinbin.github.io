---
title: Median of Two Sorted Arrays
date: 2017-07-24 22:47:04
tags:
- Algorithm
---

这是 LeetCode 上的一道题，求两个有序数组的中位数：
<https://leetcode.com/problems/median-of-two-sorted-arrays/description/>

```
There are two sorted arrays nums1 and nums2 of size m and n respectively.

Find the median of the two sorted arrays. The overall run time complexity should be O(log (m+n)).

Example 1:
nums1 = [1, 3]
nums2 = [2]

The median is 2.0
Example 2:
nums1 = [1, 2]
nums2 = [3, 4]

The median is (2 + 3)/2 = 2.5
```

这道题初看并不复杂，很容易想到时间复杂度为 $O(m+n)$ 的方法，但是要求的复杂度为 $O(\log(m+n))$，而且边缘情况很繁杂，所以难度为 hard。

很多中文博客都是用的寻找第 $k$ 小的数来实现的，这里我介绍另一种方法，时间复杂度为 $O(\log(\min\\{m, n\\}))$。

本文涉及源码：
<https://github.com/qianbinbin/leetcode/blob/master/src/median-of-two-sorted-arrays.c>

<!-- more -->

首先确定一下输入应满足的条件：两个数组可以有一个数组的长度为 0（此时中位数就是另一个数组的中位数），但长度不能同时为 0，因为此时不存在中位数。

## 划分

设两个数组中位数为 $x$，$x$ 可将两个数组分别划分为两半：

$$A[0], A[1], ..., A[i - 1] \quad \mid \quad A[i], A[i + 1], ..., A[m - 1]\\\\
B[0], B[1], ..., B[j - 1] \quad \mid \quad B[j], B[j + 1], ..., B[n - 1]$$

其中

$$i = 0, 1, ..., m\\\\
j = 0, 1, ..., n$$

这里先不关注 $i$、$j$ 在边缘情况的取值问题，稍后再详细说明。

如果同时满足以下两个条件：

1. $i + j =
\begin{cases}
m - i + n - j& \text{$m + n$ 为偶数}\\\\
m - i + n - j + 1& \text{$m + n$ 为奇数}
\end{cases}$
2. 左边最大的元素 $\leq x$，右边最小的元素 $\geq x$

则找到了划分位置，并且

$$x = \begin{cases}
\frac{(\max\\{A[i - 1], B[j - 1]\\} + \min\\{A[i + 1], B[j + 1]\\})}{2}& \text{$m + n$ 为偶数}\\\\
(\max\\{A[i - 1], B[j - 1]\\}& \text{$m + n$ 为奇数}
\end{cases}$$

由此，问题转化为：查找这样的 $i$，且同时满足以上两个条件，即可得出中位数 $x$。

根据第一个条件，由 $i$ 和 $j$ 之间的关系，在编程语言中可以直接根据 $i$ 计算得出 $j$：

```c
j = (m + n + 1) / 2 - i;
```

以上表达式在 $m + n$ 奇偶情况下均成立。

第二个条件等价于：

$$\begin{cases}
A[i - 1] \leq x \leq A[i]&\\\\
B[j - 1] \leq x \leq B[j]
\end{cases}
\iff
\begin{cases}
A[i - 1] \leq B[j]&\\\\
B[j - 1] \leq A[i]
\end{cases}$$

## 二分查找

### $j$ 的取值范围

现考察 $j$ 的范围，先将 $j$ 表示为

$$j = \frac{m + n + k}{2} - i，其中 k = \begin{cases}
0& \text{$m + n$ 为偶数}\\\\
1& \text{$m + n$ 为奇数}
\end{cases}$$

显然 $j$ 随 $i$ 递增而递减，只需 $\begin{cases}
j\_{max} \leq n&\\\\
j\_{min} \geq 0\end{cases}$ 即可，又$0 \leq i \leq m$，则：

$$\begin{cases}
j\_{max} = \frac{m + n + k}{2} \leq n&\\\\
j\_{min} = \frac{m + n + k}{2} - m \geq 0
\end{cases}
\iff
m \leq n - k$$

即可保证 $0 \leq j \leq n$。

- 当 $m + n$ 为偶数时，$k = 0$，则 $m \leq n$
- 当 $m + n$ 为奇数时，$k = 1$，则 $m \leq n - 1$，又 $m$、$n$ 必为一奇一偶，因此 $m < n$

综合得

$$m \leq n$$

因此只需 $m \leq n$，即保证 $0 \leq j \leq n$。

在编程实现时，就要先比较两个数组长度，如果 $m > n$，就要将两个数组交换。

### 查找思路

要在 $i = 0, 1, ..., m$ 中查找合适的 $i$，自然联想到二分查找。

初始情况下，令

```c
int begin = 0, end = m;
```

然后令

```c
i = begin + (end - begin) / 2;
j = (m + n + 1) / 2 - i;
```

如果`m == 0`，此时相当于只对 $B$ 数组进行划分，直接找到了 $i$、$j$ 的值。

如果`m != 0`，就要判断上面第二个条件是否成立，成立则找到所求划分位置，否则就舍去 $begin$ 到 $end$ 一半的元素，然后继续查找。

但是在$\begin{cases}
A[i - 1] \leq B[j]&\\\\
B[j - 1] \leq A[i]
\end{cases}$的判断中可能会有 $A[i - 1]$、$A[i]$、$B[j - 1]$、$B[j]$ 不存在的情况，又需要进一步讨论。

我这里给出的方法比较笨，分为如下三种情形，如果你有更好的方法欢迎讨论：

- $i = 0$ 时

  $A[i - 1]$ 不存在，相当于把数组 $A$ 的所有元素划分到右边，只需判断 $B[j - 1] \leq A[i]$ 是否成立即可。

  但 $B[j - 1]$ 是否存在，也就是说是否一定有 $0 \leq j - 1 \leq n - 1$ 呢？答案是肯定的。显然 $j - 1 \leq n - 1$，注意到 $i + j$ 是一个恒定的正数`(m + n + 1) / 2`，因此 $j \geq 1$，$0 \leq j - 1 \leq n - 1$ 。

  这样，

  - 若 $B[j - 1] > A[i]$，说明 $A[i]$ 太小，$i$ 的最终位置应在 $i + 1, i + 2, ..., m$ 之中，此时就应舍去一半元素：

  ```c
  begin = i + 1;
  ```

  - 否则说明找到 $i$


- $0 < i < m$ 时，$j$ 在 $i\_{min} = 0$、$i\_{max} = m$ 处分别取 $j\_{max}$、$j\_{min}$，则 $0 \leq j\_{min} < j < j\_{max} \leq n$ 。

  此时两个不等式中的元素均存在，并且

  - 若 $A[i - i] > B[j]$，说明 $j$ 太小，$i$ 太大，应：

    ```c
    end = i - 1;
    ```

  - 若 $B[j - 1] > A[i]$，说明 $i$ 太小，应：

    ```c
    begin = i + 1;
    ```

  - 否则说明找到 $i$

- $i = m$ 时

  $A[i]$ 不存在，只需判断 $A[i - 1] \leq B[j]$ 是否成立即可。显然 $A[i - 1]$ 必存在，又 $j = j\_{min} < j\_{max} \leq n$，故 $B[j]$ 也必存在。

  在这种情况下，

  - 若 $A[i - 1] > B[j]$，说明 $i$ 太大，应：

    ```c
    end = i - 1;
    ```

  - 否则说明找到 $i$

这样，通过二分查找，找到所需要的 $i$ 的值。

## 计算中位数

现在已经找出了划分位置，如果共有奇数个元素，则中位数为左边元素的最大值，如果共有偶数个元素，则中位数为左边最大值和右边最小值的平均数。

- 若 $A[i - 1]$ 不存在，即 $i = 0$，左边最大值即为 $B[j - 1]$（$i + j$ 为确定的正数`(m + n + 1) / 2`，故 $j > 0$，$B[j - 1]$ 必定存在）

- 若 $B[j - 1]$ 不存在，即 $j = 0$，左边最大值即为 $A[i - 1]$（同样 $A[i - 1]$ 必定存在）

- 若 $A[i - 1]$、$B[j - 1]$ 均存在时，左边最大值为 $\max\\{A[i - 1], B[j - 1]\\}$

如果 $m + n$ 为奇数，则直接返回左边最大值，否则同理求得右边最小值，返回它们的平均数。

## 时间复杂度

此算法相当于在长度为 $m$ 的数组 $A$ 中进行二分查找，由于保证了 $m \leq n$，因此时间复杂度为 $O(\log(\min\\{m, n\\}))$。
