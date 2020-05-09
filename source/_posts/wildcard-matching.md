---
title: 通配符匹配 Wildcard Matching
date: 2018-06-12 23:38:42
tags:
- LeetCode
- 算法
- 动态规划
- 贪心算法
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

# 动态规划

设 $f(i, j)$ 表示 `s` 前 $i$ 个字符与 `p` 前 $j$ 个字符是否匹配，当 $i \le 1$ 时不难写出递推公式：

$$
f(i, j) =
\begin{cases}
false & \text{s[i - 1] 与 p[j - 1] 不匹配}\\
f(i - 1, j - 1) & \text{s[i - 1] 与 p[j - 1] 匹配}\\
f(i, j - 1) 或 f(i - 1, j) & \text{p[i - 1] 为 *}
\end{cases}
$$

对边界情况提前做特殊处理即可。C 语言实现：

```c
bool isMatch(char *s, char *p) {
    if (s == NULL || p == NULL) return false;

    const size_t len_s = strlen(s), len_p = strlen(p);
    bool **match = (bool **) malloc((len_s + 1) * sizeof(bool *));
    for (size_t i = 0; i <= len_s; ++i) match[i] = (bool *) calloc(len_p + 1, sizeof(bool));
    match[0][0] = true;
    for (size_t j = 1; j <= len_p; ++j) {
        if (p[j - 1] == '*' && match[0][j - 1])
            match[0][j] = true;
        else break;
    }
    for (size_t i = 1; i <= len_s; ++i) {
        for (size_t j = 1; j <= len_p; ++j) {
            if (p[j - 1] == '?' || p[j - 1] == s[i - 1])
                match[i][j] = match[i - 1][j - 1];
            else if (p[j - 1] == '*')
                match[i][j] = match[i][j - 1] || match[i - 1][j];
        }
    }
    bool ret = match[len_s][len_p];
    for (size_t i = 0; i <= len_s; ++i) free(match[i]);
    free(match);
    return ret;
}
```

时间复杂度为 $O(mn)$。

# 贪心法

这里的贪心法其实可以看作是对递归版本算法（DFS）的优化，在递归版本中，当在模式串中遇到 `*` 时，是这样实现的：

```c
if (p[j] == '*')
    return isMatch(i, j + 1) || isMatch(i + 1, j);
```

即每遇到一个 `*` 均会产生一个分支，而贪心法则只对模式串上一个（已扫描部分的最后一个） `*` 进行继续搜索，实现时只要不断更新 `*` 的指针即可。C 语言实现：

```c
bool isMatch(char *s, char *p) {
    if (s == NULL || p == NULL) return false;

    char *star = NULL, *last_str = NULL;
    while (*s != '\0') {
        if (*p == '?' || *p == *s) {
            ++s;
            ++p;
        } else if (*p == '*') {
            last_str = s;
            star = p;
            ++p;
        } else if (star != NULL) {
            s = ++last_str;
            p = star + 1;
        } else {
            return false;
        }
    }
    while (*p == '*') ++p;
    return *p == '\0';
}
```

显然这样比递归版本少了很多分支，可以看作是贪心法版本的 DFS，但如何证明算法是正确的呢？

很遗憾，虽然方法是对的，也有很多人用这个方法 AC 了，但没有找到严格证明。

以下是我给出的不严格说明：

```
[match][*]abc[*][to match]
[match] * abc * [to match]
```

第一行为 `s`，第二行为 `p`，`[match]` 表示已匹配子串，`[to match]` 表示待匹配子串。`s` 中的 `[*]` 与 `p` 中的 `*` 匹配。

设 `s` 中 `c` 的位置为 $i_0$。

如果回溯第一个 `*`，让它匹配更长的子串，则若能找到与 `p` 中 `abc` 匹配的串，则对应的 `c` 位置一定在 $i_0$ 之后，它可能在 `[*]` 中，也可能在 `[to match]` 中，但无论如何，这些情况都被“只回溯第二个 `*` ”包括在内了。因此，只回溯最后一个 `*` 就可以了。

# 实现源码

<https://github.com/qianbinbin/leetcode>

# 参考资料

1. [Wildcard Matching - LeetCode](https://leetcode.com/problems/wildcard-matching/discuss/17810/Linear-runtime-and-constant-space-solution)
