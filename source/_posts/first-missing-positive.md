---
title: 数组中首个缺失的正整数 First Missing Positive
date: 2018-06-08 21:14:21
tags:
- LeetCode
- 算法
- 哈希表
---

找出数组中首个缺失的正整数：
<https://leetcode.com/problems/first-missing-positive/>

> Given an unsorted integer array, find the smallest missing positive integer.
> 
> Example 1:
> 
> Input: [1,2,0]
> Output: 3
> 
> Example 2:
> 
> Input: [3,4,-1,1]
> Output: 2
> 
> Example 3:
> 
> Input: [7,8,9,11,12]
> Output: 1
> 
> Note:
> 
> Your algorithm should run in O(n) time and uses constant extra space.

看起来很简单，首先想到排序的方法，时间复杂度为 $O(n\log(n))$。

也可以用最小堆/优先队列，在平均情况下也只要 $O(n)$，最坏情况下为 $O(n\log(n))$，空间复杂度为 $O(n)$。

以上两种方法均能 AC。然而题目要求时间复杂度为 $O(n)$，空间复杂度为 $O(1)$。

<!-- more -->

其实这里要运用桶排序的思想，也类似哈希表中的哈希桶。

要求第一个缺少的正整数，我们可以将数组中的元素以 $1, 2, 3, ...$ 的顺序依次映射到桶中。

显然非正数不用考虑。超过数组长度 $n$ 的数也不用考虑，因为缺少的数一定在 $1$ ~ $n$ 中，如果数组中恰好是 $1$ ~ $n$ 的数，那么缺少的数就是 $n + 1$。

题目要求不能使用额外空间，那么可以将数组本身作为桶。

以 `[3, 4, -1, 1]` 为例，映射后即为：

下标 $i$ | 0 | 1 | 2 | 3
---------|---|---|---|--
映射值 $i + 1$ | 1 | 2 | 3 | 4
数组（映射前） | 3 | 4 | -1| 1
数组（映射后） | 1 | -1| 3 | 4

然后依次遍历数组，找出第一个不等于 $i + 1$ 的值即为所求值，这里为 $2$。

# 实现源码

<https://github.com/qianbinbin/leetcode>
