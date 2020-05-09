---
title: Floyd 判圈算法
date: 2017-12-01 22:34:49
tags:
- LeetCode
- 算法
---

判断一个单链表中是否存在环，并找出环的入口：
<https://leetcode.com/problems/linked-list-cycle-ii/>

> Given a linked list, return the node where the cycle begins. If there is no cycle, return null.
>
> Note: Do not modify the linked list.
>
> Follow up:
> Can you solve it without using extra space?

不知道 LeetCode 难度评级的标准是什么，有的 hard 题看完题目就有思路，像这种 medium 题我琢磨半天也想不出空间复杂度为 $O(1)$ 的解法。后来了解到这一题已经有著名的解法了，即 Floyd 判圈算法，没错，就是求最短路径的弗洛伊德算法的那个 Floyd。

<!-- more -->

# 哈希表法

时间复杂度 $O(n)$，空间复杂度 $O(n)$。略。

# Floyd 判圈算法

在一个环中，如果两人速度不等且保持不变，那么总有一个时刻两者会相遇。应用此技巧，让一个`slow`指针每次移动一个结点，`fast`指针每次移动两个结点，如果两者最终能相遇，则说明单链表中存在环，如果`fast`走到了表尾，则说明不存在。

如果要求环长也好办，当两者相遇时，让其中一人保持不动，另一人再移动一圈，再次相遇时移动的路程即为环长。

但如何求出环的入口呢？

现已知单链表中存在环，设头结点与环入口距离为 $a$，从环入口到相遇结点的距离为 $b$，环长度为 $r$，`slow`指针在相遇前已经在环中移动了 $n_1$ 圈，`fast`指针在相遇前已经在环中移动了 $n_2$ 圈，那么`slow`指针移动的总路程为
$$s_1 = a + n_1r + b$$

`fast`指针移动的总路程为
$$s_2 = a + n_2r + b$$

由于`fast`移动速度为`slow`的两倍，路程也为两倍，则 $s_2 = 2s_1$，两式相减得
$$s_2 - s_1 = s_1 = (n_2 - n_1)r$$

这说明`slow`和`fast`指针移动路程都为环长的整数倍。

现增设一个指针`slow1`，让其从头结点开始，每次移动一个结点，与相遇结点处的`slow`指针同时出发。当`slow1`移动到环的入口，即距离为 $a$ 时，`slow`移动的距离为
$$s_1' = s_1 + a$$

`slow`指针也一定停留在环的入口，它可以看作先移动了距离 $a$，即环入口处，又绕环移动了若干圈。

这说明，当`slow`和`slow1`相遇时，相遇结点必定为环的入口。

时间复杂度 $O(n)$，空间复杂度 $O(1)$。

# 实现源码

<https://github.com/qianbinbin/leetcode>
