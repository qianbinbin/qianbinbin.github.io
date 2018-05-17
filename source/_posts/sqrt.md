---
title: 牛顿法实现 sqrt() 开平方
date: 2018-04-30 15:58:14
tags:
- Algorithm
- 算法
- LeetCode
- C
- C 语言
---
在精度要求较低的情况下，可以用二分查找实现开平方，例如 [Sqrt(x) - LeetCode](https://leetcode.com/problems/sqrtx/description/) 就只需返回 `int`，即精确到个位。

在求更高精度开平方时，可以利用牛顿法。

本文涉及源码：
<https://github.com/qianbinbin/leetcode/blob/master/src/sqrtx.c>

<!-- more -->

# 从泰勒公式到牛顿法

泰勒公式

$$f(x) = f(x\_0) + f'(x\_0)(x − x\_0) + \frac{f''(x\_0)}{2!}(x − x\_0)^2+ ... + \frac{f^n(x\_0)}{n!}(x − x\_0)^n + R\_n(x).$$

余项 $R\_n(x)$ 为 $(x - x\_x)^n$ 的高阶无穷小。

现求 $f(x) = 0$ 的根，取展开式的前两项 $f(x\_0) + f'(x\_0)(x - x\_0)$，取 $x\_1$ 为一个比较接近根的值，令

$$f(x\_1) + f'(x\_1)(x - x\_1) = 0.$$

求得的解记为 $x\_2$，通常 $x\_2$ 会比 $x\_1$ 更接近方程 $f(x) = 0$ 的解。

用 $x\_2$ 代替方程中的 $x\_1$，继续求出 $x\_3, x\_4, x\_5, ...$，每次迭代都得到更精确的解。

事实上，$y = f(x\_0) + f'(x\_0)(x - x\_0)$ 是 $y = f(x)$ 在 $x = x\_0$ 的切线，切线与 $x$ 轴的交点横坐标比 $x\_0$ 更接近 $y = f(x)$ 的零点。

借助维基百科的图片更容易理解：

![](https://upload.wikimedia.org/wikipedia/commons/e/e0/NewtonIteration_Ani.gif)

令

$$f(x\_n) + f'(x\_n)(x\_{n + 1} - x\_n) = 0,$$

得迭代公式

$$ x\_{n + 1} = x\_n - \frac{f(x\_n)}{f'(x\_n)}.$$

# 开平方迭代公式

求开平方 $\sqrt{n}$，可令

$$x = \sqrt{n},$$

即求

$$x^2 - n = 0$$

的非负根。

设函数 $f(x) = x^2 - n$，$f'(x) = 2x$，代入迭代公式化简得

$$x\_{n + 1} = \frac{1}{2} (x\_n + \frac{n}{x\_n}).$$

在实现时可以设定一个允许的误差，不断迭代，一旦结果误差在允许之内就可以停止迭代。这就是牛顿法求开平方。

类似地还可以求更高次方根以及其它复杂方程的根。

# 参考资料

[Newton's method - Wikipedia](https://en.wikipedia.org/wiki/Newton%27s_method)
