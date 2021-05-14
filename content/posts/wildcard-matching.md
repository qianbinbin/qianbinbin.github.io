---
title: 通配符匹配 Wildcard Matching
date: 2018-06-12 23:38:42
tags:
- LeetCode
- 算法
- 动态规划
- 贪心算法
enableKatex: true
---

通配符匹配问题：
<https://leetcode.com/problems/wildcard-matching/>

> Given an input string (s) and a pattern (p), implement wildcard pattern matching with support for '?' and '*'.
> 
> '?' Matches any single character.
> '*' Matches any sequence of characters (including the empty sequence).
> 
> The matching should cover the entire input string (not partial).
> 
> Note:
> 
> s could be empty and contains only lowercase letters a-z.
> p could be empty and contains only lowercase letters a-z, and characters like ? or *.

待匹配字串 `s` 中只有字母，模式串中 `p` 只含有字母和 `?`、`*`，`?` 匹配任意单个字符，`*` 匹配任意多个字符（包括空字串），判断字串是否完全匹配。

使用递归算法会超时。

<!-- more -->

## 动态规划

设 $f(i, j)$ 表示 `s` 前 $i$ 个字符与 `p` 前 $j$ 个字符是否匹配，当 $i \le 1$ 时不难写出递推公式：

$$f(i, j) = \begin{cases} false & \text{s[i - 1] 与 p[j - 1] 不匹配} \\\ f(i - 1, j - 1) & \text{s[i - 1] 与 p[j - 1] 匹配} \\\ f(i, j - 1) 或 f(i - 1, j) & \text{p[i - 1] 为 *} \end{cases}$$

对边界情况提前做特殊处理即可。C 语言实现：

{{< code file=leetcode/c/src/wildcard_matching.c lang=c from=6 to=26 >}}

时间复杂度为 $O(mn)$。

## 贪心法

递归版本实现可能出现以下逻辑：

```c
if (p[j] == '*')
    return match(i, j + 1) || match(i + 1, j);
```

这里 `match(i, j)` 表示从 `s[i]`、`p[j]` 开始是否匹配。

可见，每当遇到一个 `*`，都会产生新的分支。

而贪心法只对模式串上一个（已扫描部分的最后一个）`*` 进行继续搜索，只需记录上一个 `*` 指针即可。

C 语言实现：

{{< code file=leetcode/c/src/wildcard_matching.c lang=c from=28 to=47 >}}

这样，分支明显少于递归版本。

以下是不严格说明：

```
            i
[match][*]abc[*][to match]
[match] * abc * [to match]
```

第一行为 `s`，第二行为 `p`。`[match]` 表示已匹配子串，`[to match]` 表示待匹配子串。`s` 中的 `[*]` 与 `p` 中的 `*` 匹配。`abc` 是一段匹配的字符串（可能含有 `?`）。

设 `s` 中 `c` 的下标为 `i`。如果回溯第一个 `*`，即让它匹配更长的子串，则新匹配（如果有的话）的 `c` 位置一定在 `i` 之后，它可能在 `[*]` 中，也可能在 `[to match]` 中，但无论如何，这些情况都被“只回溯第二个 `*` ”包括在内了。因此，只回溯最后一个 `*` 就可以了。

在最坏情况下，这一算法的时间复杂度也为 $O(mn)$。

## 实现源码

<https://github.com/qianbinbin/leetcode>

## 参考资料

1. [Wildcard Matching - LeetCode](https://leetcode.com/problems/wildcard-matching/discuss/17810/Linear-runtime-and-constant-space-solution)
