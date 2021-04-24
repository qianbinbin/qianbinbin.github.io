---
title: KMP 算法中的 next 数组
date: 2021-03-18 18:20:45
tags:
- 算法
- 数据结构
enableKatex: true
---

对于字符串匹配，即在字符串 s 中寻找 p 子串的位置，如果使用暴力匹配，则时间复杂度为 $O(mn)$。而 KMP 算法在字符串重复率较高时可以获得更好的性能，时间复杂度为 $O(m + n)$。

<!-- more -->

KMP 算法的关键在于 next 数组。它用来表示在某个位置查找时，如果 p 与 s 不匹配，则 p 应回退的位置。

例如（第一行 s，第二行 p）：

```
...ababax...
   ababay...
```

`x` 与 `y` 不匹配，则 s 指针无需移动，p 应从 p[3] 继续匹配，即：

```
...ababax...
     ababay...
```

next 数组利用了匹配到某个位置时，s 失配字符前面的部分子串是已知的，与 p 不匹配字符前的子串相同（在本例中即为 `ababa`），于是可以直接移动 p 指针到下一个可能的位置，从而减少了暴力匹配中的冗余。

显然，next 数组只与 p 有关。

观察得知，移动 p 指针后，一部分子串重合。也就是说子串 `ababa` 有相同的前缀和后缀 `aba`，这为 next 数组的计算提供了根据。事实上，如果子串不存在公共前后缀，那么就该让 s 指针移动了。

而且，这个公共前后缀应为最长，否则会丢失可能的位置，例如不能这样匹配（取公共前后缀为 `a`）：

```
...ababax...
       ababay...
```

## 计算 next 数组

首先明确 next 数组元素值的含义：

- next[i] = -1，表示应该让 s 指针移动到下一个位置，p 从头开始匹配。
- next[i] >= 0，表示 p 应回退到 next[i] 位置，s 指针不变。

next 数组的计算利用了类似动态规划的思想：在 next[0...i] 已知的情况下，求 next[i+1]。

```
0 ... j ...... i i+1
----- ?  ----- ?
** **    ** **
```

设 j = next[i]，说明 p[0:i] 有公共前后缀 p[0:j] = p[i-j:i]，即 `-----` 部分。

注：p[i:j] 是 Python 切片语法，表示 p 在坐标范围 [i,j) 中的子串。

- 若 p[j] = p[i]，则说明找到长度为 j+1 的公共前后缀 p[0:j+1] = p[i-j:i+1]，得 next[i+1] = j+1。
- 若 p[j] != p[i]，说明不存在长度为 j+1 的公共前后缀，但如果 next[j] 也存在的话，说明 p[0:j] 也存在公共前后缀，即 `**` 部分，而它必定也是 p[0:i] 的公共前后缀，此时递归地按照上面的流程查找即可。
- 若查找到最后，j = -1，则 next[i] = 0。

其余只需设置初始条件 next[0] = -1。

```c
int *get_next(char const *p, int size) {
    int *next = (int *) malloc(size * sizeof(int));
    next[0] = -1;
    int i = 0, j = -1;
    while (i < size - 1) {
        while (j != -1 && p[j] != p[i])
            j = next[j];
        next[++i] = ++j;
    }
    return next;
}
```

### 优化

上面的算法存在优化的可能，例如：

```
...abx...
   aba...
```

按照之前的算法，next[2] = 0，也就是从 p 头部继续匹配，但 p[2] = p[0] = `a`，而它必定与 s 中的 `x` 不匹配，应该为 next[2] = -1。

因此在上面的算法中，找到公共前后缀后，还要判断是否有 p[i+1] = p[j+1]，若有，则 next[i+1] 不再是 j+1，而是 next[j+1]：

```c
int *get_next(char const *p, int size) {
    int *next = (int *) malloc(size * sizeof(int));
    next[0] = -1;
    int i = 0, j = -1;
    while (i < size - 1) {
        while (j != -1 && p[j] != p[i])
            j = next[j];
        ++i;
        ++j;
        next[i] = p[j] != p[i] ? j : next[j];
        // if (p[j + 1] == p[i + 1])
        //     next[++i] = next[++j];
        // else
        //     next[++i] = ++j;
    }
    return next;
}
```

注意这一判断不能放在内层 while 循环，因为 j 的实际含义不是应回退的位置，而是 p[0:i+1] 的最长公共前后子串长度，这一结果需要在下次外层 while 循环中使用。

## 参考资料

[KMP算法的Next数组详解 - 唐小喵 - 博客园](https://www.cnblogs.com/tangzhengyue/p/4315393.html)
