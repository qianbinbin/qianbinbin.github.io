---
title: Gas Station
date: 2017-10-14 19:30:34
tags:
- Algorithm
- LeetCode
- C
---

加油站问题，LeetCode 链接：
<https://leetcode.com/problems/gas-station/description/>

> There are N gas stations along a circular route, where the amount of gas at station i is gas[i].
>
> You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from station i to its next station (i+1). You begin the journey with an empty tank at one of the gas stations.
>
> Return the starting gas station's index if you can travel around the circuit once, otherwise return -1.
>
> Note:
> The solution is guaranteed to be unique.

这道题难度为 Medium，但如果对每个加油站进行模拟，则时间复杂度为 $O(n^2)$，OJ 判定超时，而 $O(n)$ 的方法并不容易。

Discuss 区有人给出了 $O(n)$ 的方法：
<https://leetcode.com/problems/gas-station/discuss/42568/Share-some-of-my-ideas.>

但其中用到的结论并没有证明，而且描述不清晰。下面我会说明这两个结论的正确性。

本文涉及源码：
<https://github.com/qianbinbin/leetcode/blob/master/src/gas-station.c>

<!-- more -->

## 两个结论

1. **如果从 i 点出发，恰好无法到达 j 点（即能到达 j 前一点但无法到达 j 点），则如果从 i ~ j 之间任意一点出发，均无法到达 j 点。**

  这个结论很好理解，各个点是构成一个环的，但为方便起见，抽象为如下序列：

  ..., i, i + 1, ..., j - 1, j, ...

  既然从 i 出发可以到达 i + 1 ~ j - 1 所有点，那么到达这些点时剩余的油量一定 $\ge0$；而如果直接从这些点出发，初始油量为 0，就更加无法到达 j 点了。

  这样，我们就可以跳过 i + 1 ~ j - 1 所有点，下次判断时直接从 j 点开始。

2. **如果所有车站的油量总和大于等于所有车站到下一站需要的油量总和，即 $$\sum\_{i=0}^{N-1} gas[i] \ge \sum\_{i=0}^{N-1} cost[i]$$ 则问题必有解。**

  考虑两个点的情形，存在两种情况：

  - 若两点均满足 $gas[i] \ge cost[i]$，显然结论成立

  - 若某一点不满足 $gas[i] \ge cost[i]$，不妨设 $gas[0] < cost[0]$，由于$gas[0] + gas[1] \ge cost[0] + cost[1]$，则必有$gas[1] > cost[1]$，那么从 1 点出发可以到达 0 点并回到 1 点，结论仍成立

  <!--

  - 当有 3 个点时，

    - 若 3 个点均满足 $gas[i] \ge cost[i]$，显然结论成立

    - 若有且仅有一点不满足 $gas[i] \ge cost[i]$，不妨设 $gas[0] < cost[0]$，$gas[1] \ge cost[1]$，$gas[2] \ge cost[2]$，且不能同时取“=”，则从 1 点出发到达 2 点，可以再通过 0 点回到 1 点

      这种情况可以把 1、2 点结合为一个点，就退化为两个点的情形。

    - 若有且仅有两点不满足 $gas[i] \ge cost[i]$，不妨设 $gas[0] < cost[0]$，$gas[1] < cost[1]$，$gas[2] > cost[2]$，由于 $\sum\_{i=0}^{N-1} gas[i] \ge \sum\_{i=0}^{N-1} cost[i]$，此时必有 $gas[2] + gas[0] > cost[2] + cost[0]$，这说明从 2 点出发到达 0 点，可以再通过 1 点回到 2 点

      这种情况可以把 2、0 点结合为一个点，同样退化为两个点的情形。

  事实上，如果 $gas[i] + gas[i + 1] \ge cost[i] + cost[i + 1]$，那么 i、i + 1 两点就可以结合为一点。

  -->

  把 $gas[i] \ge cost[i]$ 的点称为“好点”，其余称为“坏点”（我自己造的词），考虑普遍情形，对 $N$ 个点进行如下操作：

  1. 把相邻的好点结合为新的好点，相邻的坏点结合为新的坏点，得到一个好点坏点相间的环

  2. 把好点和下一个坏点结合，得到 $N'$ 个点，由于 $\sum\_{i=0}^{N-1} gas[i] \ge \sum\_{i=0}^{N-1} cost[i]$，保证了至少有一个好点

  不断重复上述操作，最终退化为两个点的情形，且其中至少有一个好点，根据上面对两个点情形的讨论，问题必有解，且构成最终好点的第一个点即为出发点。

  在这个问题中，题目保证了解（如果有的话）是唯一的，那么最终就一定是一个好点和一个坏点。

## 解题思路

在这道题中，我们不需要对每个点模拟一整圈，只要找到第一个点，使得其与之后的点都能结合成为好点即可，这个点就是出发点，因为题中已经保证解是唯一的。一边扫描一边统计邮箱剩余油量和总剩余油量，如果最终总剩余油量 $< 0$，则说明无解，返回 -1。

这样也不需要对数组越界进行处理。

只扫描一遍数组，复杂度为 $O(n)$。
