---
title: 素数筛法和哥德巴赫猜想
date: 2019-01-03 19:06:44
mathjax: true
tags:
- 算法
- 位操作
---

起因是看到了这个帖子[如果高中生能证明哥德巴赫猜想，会被清华北大数学系保送吗？ - 知乎](https://www.zhihu.com/question/306537777)，emmm...

本文的目标是筛选 32 位无符号整数（约 40 亿）中的所有素数，并在此范围内验证哥德巴赫猜想。

# 试除法

一开始并没有打算寻找所有素数，而是暴力判断是否为素数。一个小技巧是将自然数表示为 6k + b，当中只有 6k + 1 和 6k + 5 的形式可能为素数。也可以用 30k + b 等，简单起见不再优化了。

后来发现我还是 naive，验证所有的 32 位无符号整数耗时太长，验证到 1000 万都要 10 秒左右。

{% include_code lang:c from:8 to:18 prime-sieve-and-goldbach-s-conjecture/trial_division.c %}

# 埃拉托斯特尼筛法

如果能先求得所有素数，然后查表，速度会有很大提升。

对于每个整数使用一个 bit 来标识是否为素数，32 位无符号整数需要 2^32 / 8 = 512 MiB 空间，剔除所有的偶数，需要一半即 256 MiB。如果剔除 6k + 1 和 6k + 5 以外的数，只需要 1/3 的空间，但是不利于映射时使用位操作，故只剔除偶数。

经了解，筛选素数常见的有两种方法：埃拉托斯特尼筛法和欧拉筛法。前者思路简单，且无需保存素数序列，时间复杂度为 $O(n \log \log n)$；后者需要保存素数序列，时间复杂度为 $O(n)$。

所谓埃氏筛法，就是筛去 $\le \sqrt{n}$ 的素数 $2, 3, 5, ...$ 的倍数，维基百科的图片清晰易懂：

![](https://upload.wikimedia.org/wikipedia/commons/b/b9/Sieve_of_Eratosthenes_animation.gif)

{% include_code lang:c from:9 to:26 prime-sieve-and-goldbach-s-conjecture/sieve_of_eratosthenes.c %}

这里除了位操作外，还用到一些优化：

- 当筛选素数 $n$ 的倍数时，$2n, 3n, ..., (n-1)n$ 必然已经筛选过了，故只需从 $n^2$ 开始，于是筛选序列为 $n^2, n^2 + n, n^2 + 2n...$
- 当 $k$ 为奇数时，由于 $n$ 为奇数，则 $n^2 + kn$ 必为偶数，算法只对奇数保存标识，故 $k$ 只需取偶数，筛选序列只剩下 $n^2, n^2 + 2n, n^2 + 4n...$

在 i7-7700HQ + 16 GiB 内存的 MacBook Pro 上，开启 O3 优化后耗时约 22 秒：

```shell
Sieve of Eratosthenes cost 21.723315 seconds.
Same story.
Verification cost 63.079718 seconds.
```

这个算法的主要缺陷是，不利于 CPU 利用程序的局部性原理。筛选的时候 `data` 数组并不是按顺序读写的，而是类似列优先读写二维数组那样，cache 命中率太低，大量耗时都用在了读写内存上。

# 分段埃氏筛法

cache 命中率对程序速度有很大影响，于是有了分段筛选。

在 32 位无符号整数范围内，埃氏筛法需要筛掉 `sqrt(UINT32_MAX)` 以内的素数的倍数。根据[素数计数函数](https://zh.wikipedia.org/wiki/%E7%B4%A0%E6%95%B0%E8%AE%A1%E6%95%B0%E5%87%BD%E6%95%B0)，在此范围内的素数约有 8000 个，保存只需占用约 32 KiB，不再像上面那样查询 `data` 数组。

将 32 位无符号整数划分为若干个等长的段，先使用埃氏筛法筛选第一个段，并收集 `sqrt(UINT32_MAX)` 以内的素数，对其它段筛选前若干个素数的倍数。数据段不宜过大，否则 cache 命中率会降低，但也不宜过小，否则复杂度也会增大。

i7-7700HQ 的 L1 数据 cache 大小为 32 KiB，L2 cache 为 256 KiB，L3 cache 为 6 MiB。在实践中，发现选择 128 KiB 作为一个段大小较为合适。

{% include_code lang:c from:9 to:72 prime-sieve-and-goldbach-s-conjecture/segmented_sieve_of_eratosthenes.c %}

速度有了明显的提升：

```shell
Segmented sieve of Eratosthenes cost 5.576287 seconds.
Same story.
Verification cost 63.089097 seconds.
```

这里对每个素数都计算其倍数 `c`，也可以将所有素数的倍数保存到一个数组中，与 `primes` 数组一样只占 32 KiB，这样每次无需重新计算。但在实践中发现性能反而下降了一点，推测是仅仅几千个数的乘除和加法 \* 段数，耗时并不多，而内存读写已经超过了这个耗时。

# 欧拉筛法

在埃氏筛法中，一个合数会被多次筛选到，因此它不是线性的算法。欧拉筛法使每个合数只被其最小质因数筛去一次，其复杂度为 $O(n)$，在数量很大时有很大优势。

{% include_code lang:c from:9 to:34 prime-sieve-and-goldbach-s-conjecture/sieve_of_euler.c %}

关键在当 `n` 是素数 `p` 的倍数时直接 `break`。

算法的正确性（所有合数都能被筛去）：对于任意合数 $c$，将其写成质因数乘积 $c = p\_1^{k\_1} p\_2^{k\_2}... p\_j^{k\_j}$，$c$ 一定能在 $n = p\_1^{k\_1 - 1} p\_2^{k\_2}... p\_j^{k\_j}$ 被筛去，因为 $n \ge p\_1$，即素数 $p\_1$ 必然已经被收集。

算法的复杂度（合数只被最小质因数筛去一次）：假设合数 $c = p\_1^{k\_1} p\_2^{k\_2}... p\_x^{k\_x}... p\_j^{k\_j}$ 也被 $p\_x$ 筛去，此时 $n = p\_1^{k\_1} p\_2^{k\_2}... p\_x^{k\_x - 1}... p\_j^{k\_j}$。若 $n < p\_x$，此时 $p\_x$ 还没被收集，不符合；若 $n \ge p\_x$，之前的循环中已经枚举了 $p\_1, p\_2..., p\_i$，在 $p\_1$ 的时候就 `break` 了，故不符合。

虽然是线性复杂度，实际运行好于埃氏筛法，但比起分段埃氏筛法还是慢很多：

```shell
Sieve of Euler cost 15.921141 seconds.
Same story.
Verification cost 63.412436 seconds.
```

而欧拉筛法本身又不适合改写成分段算法，看来 cache 的威力还是相当可观的。

# 总结

试除法适用于数据较小的情况，埃氏筛法和欧拉筛法适用于数据较大的情况。如果对性能要求较高，可以考虑分段埃氏筛法。

# 参考资料

1. [埃拉托斯特尼筛法 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/%E5%9F%83%E6%8B%89%E6%89%98%E6%96%AF%E7%89%B9%E5%B0%BC%E7%AD%9B%E6%B3%95)
2. [primesieve - Segmented sieve of Eratosthenes](https://primesieve.org/segmented_sieve.html)
3. [筛法小结 (Eratosthenes/Euler) | __debug's Home](http://debug18.com/posts/introduction-to-sieve-method/)
