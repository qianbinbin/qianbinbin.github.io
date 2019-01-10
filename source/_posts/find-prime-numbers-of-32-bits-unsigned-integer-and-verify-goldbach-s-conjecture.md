---
title: 寻找 32 位无符号整数（约 40 多亿）中的所有素数并验证哥德巴赫猜想
date: 2019-01-03 19:06:44
tags:
- C
---

纯属无聊写着玩的，起因是看到了这个帖子[如果高中生能证明哥德巴赫猜想，会被清华北大数学系保送吗？ - 知乎](https://www.zhihu.com/question/306537777)，emmm...


# 试除法

一开始并没有打算寻找所有素数，而是暴力判断是否为素数。一个小技巧是将自然数表示为 6k + x，当中只有 6k + 1 和 6k + 5 的形式可能为素数。也可以用 30k + x 等，简单起见不再优化了。

后来发现我还是 naive，验证所有的 32 位无符号整数（约 40 亿）耗时太长，验证到 1000 万都要 10 秒左右。

[源码](https://github.com/qianbinbin/qianbinbin.github.io/blob/source/code/goldbachs_conjecture.c)：

```c
bool prime(uint32_t number) {
    if (number < 6)
        return number == 2 || number == 3 || number == 5;
    if (number % 6 != 1 && number % 6 != 5)
        return false;

    for (uint32_t i = 5, sq = (uint32_t) sqrt(number); i <= sq; i += 6)
        if (!(number % i) || !(number % (i + 2)))
            return false;
    return true;
}
```


# 埃拉托斯特尼筛法

如果能先求得所有素数，然后查表，速度会有很大提升。

32 位无符号整数如果每个整数使用一个 bit 来标识是否为素数，大约需要 2^32 / 8 = 512 MB 空间，剔除所有的偶数，需要一半即 256 MB 空间。如果剔除 6k + 1 和 6k + 5 以外的数，只需要 1/3 的空间，但是不利于映射时使用位操作，故只剔除偶数。

经了解，筛选素数常见的有两种方法：埃拉托斯特尼筛法和欧拉筛法。前者思路简单，且无需保存素数序列，时间复杂度为 $O(n \log n \log n)$；后者需要保存素数序列，时间复杂度为 $O(n)$。

所谓埃氏筛法，就是对 $\sqrt{n}$ 以内的素数 $2, 3, 5, ...$ 的 $k$ 倍（$k$ 为整数且 $k \ge 2$）全部筛去，维基百科的图片可以说非常生动了：

![](https://upload.wikimedia.org/wikipedia/commons/b/b9/Sieve_of_Eratosthenes_animation.gif)

[源码](https://github.com/qianbinbin/qianbinbin.github.io/blob/source/code/goldbachs_conjecture_sieve_of_eratosthenes.c)：

```c
bool prime(char *data, uint32_t n) {
    if (n == 2)
        return true;
    if (!(n & 1))
        return false;
    return !(data[n >> 4] & (1 << (n >> 1 & 7)));
}

void sieve_of_eratosthenes(char *data) {
    data[0] = 1;
    uint32_t n, sq, start, c, step;
    for (n = 3, sq = (uint32_t) sqrt(UINT32_MAX); n <= sq; n += 2) {
        if (data[n >> 4] & (1 << (n >> 1 & 7)))
            continue;
        for (start = n * n, step = n << 1, c = start; c >= start; c += step)
            data[c >> 4] |= 1 << (c >> 1 & 7);
    }
}
```

这里除了位操作外，还用到一些小技巧，当筛选素数 $n$ 的倍数时（$n \ge 3$），只需从 $n^2$ 开始，前面的素数的 $n$ 倍无需再筛选，因为已经筛选过了，筛选序列即为 $n^2, n^2 + n, n^2 + 2n...$。当 $k$ 为奇数时，$n^2 + kn$ 一定为偶数，故可以跳过，即 $k$ 只取偶数即可，筛选序列即为 $n^2, n^2 + 2n, n^2 + 4n...$。

在 MacBook Pro (15-inch, 2017) 低配版，即使开启 O3 优化后耗时还是不少的：

```sh
Sieve of Eratosthenes cost 21.723315 seconds.
Same story.
Verification cost 63.079718 seconds.
```

但这个程序有个明显的缺点：不利于 CPU 利用程序的局部性原理。筛选的时候并不是按顺序读写的，而是类似列优先读写二维数组那样，cache 命中率太低，大量耗时都用在读写内存上了。


# 分段埃氏筛法

要筛选 32 位无符号整数，需要筛选掉 `sqrt(UINT32_MAX)` 以内的素数的倍数。根据[素数计数函数](https://zh.wikipedia.org/wiki/%E7%B4%A0%E6%95%B0%E8%AE%A1%E6%95%B0%E5%87%BD%E6%95%B0)约有 8000 个，每个数用 32 位无符号整数保存，共需要约 32 KB，而不再查询 `data` 数组，只花费少量空间换取时间。

cache 命中率对程序速度有很大影响，故尝试分段筛选。我的 CPU L1 数据 cache 大小为 32 KB，L2 cache 为 256 KB，L3 cache 为 6 MB。数据段不宜过大，否则 cache 命中率会降低。

将 32 位无符号整数划分为若干个相等的段，先使用埃氏筛法筛选第一个段，并收集 `sqrt(UINT32_MAX)` 以内的素数，对其它段筛选前若干个素数的倍数。数据段不宜过小，否则复杂度也会增大。

在实践中，发现选择 128 KB 作为一个段大小较为合适。这一点可能因环境而异。

[源码](https://github.com/qianbinbin/qianbinbin.github.io/blob/source/code/goldbachs_conjecture_segmented_sieve_of_eratosthenes.c)：

```c
// Note: the size must be power of 2.
#define SEGMENT_SIZE 0x200000

// L1 data cache 32 KB = 2^15*2^3 = 2^18 bits, identifying 2^19 numbers.
// #define SEGMENT_SIZE 0x80000

// L2 cache 256 KB
// #define SEGMENT_SIZE 0x400000

// L3 cache 6 MB, use 2 MB of it
// #define SEGMENT_SIZE 0x2000000

bool prime(char *data, uint32_t n) {
    if (n == 2)
        return true;
    if (!(n & 1))
        return false;
    return !(data[n >> 4] & (1 << (n >> 1 & 7)));
}

/*
 * Use sieve of Eratosthenes in [0, max(SEGMENT_SIZE, sqrt(UINT32_MAX))),
 * and generate prime numbers in [3, sqrt(UINT32_MAX)].
 */
uint32_t initial_sieve(char *data, uint32_t *primes) {
    const uint32_t SQ = (uint32_t) sqrt(UINT32_MAX), LIMIT = SEGMENT_SIZE > SQ ? SEGMENT_SIZE : SQ;
    data[0] = 1;
    uint32_t n, sq, c, step, count = 0;
    for (n = 3, sq = (uint32_t) sqrt(LIMIT); n <= sq; n += 2) {
        if (data[n >> 4] & (1 << (n >> 1 & 7)))
            continue;
        for (c = n * n, step = n << 1; c <= LIMIT; c += step)
            data[c >> 4] |= 1 << (c >> 1 & 7);
    }
    for (n = 3; n <= SQ; n += 2) {
        if (!(data[n >> 4] & (1 << (n >> 1 & 7))))
            primes[count++] = n;
    }
    return count;
}

void segmented_sieve_of_eratosthenes(char *data) {
    // store prime numbers in [3, sqrt(UINT32_MAX)]
    uint32_t *primes = (uint32_t *) malloc(8192 * sizeof(uint32_t));
    const uint32_t count = initial_sieve(data, primes);

    uint32_t sq, i, p, c, step;
    const uint32_t SEGMENTS = (uint32_t)(1 << 31) / (SEGMENT_SIZE >> 1);
    // first segments has been sieved
    for (uint32_t s = 1, low = SEGMENT_SIZE, high = (SEGMENT_SIZE << 1) - 1;
         s < SEGMENTS; ++s, low += SEGMENT_SIZE, high += SEGMENT_SIZE) {
        sq = (uint32_t) sqrt(high);
        for (i = 0; i < count && (p = primes[i]) <= sq; ++i) {
            // c = p^2 + 2k * p
            c = low / p * p;
            if (c < low) c += p;
            if (!(c % 2)) c += p;
            // c will be overflow in last segment
            for (step = p << 1; low <= c && c <= high; c += step)
                data[c >> 4] |= 1 << (c >> 1 & 7);
        }
    }
    free(primes);
}
```

速度有了明显的提升：

```sh
Segmented sieve of Eratosthenes cost 5.576287 seconds.
Same story.
Verification cost 63.089097 seconds.
```

这里对每个素数都计算其倍数 `c`，也可以将所有素数的倍数保存到一个数组中，与 `primes` 数组一样只占 32 KB，这样每次无需重新计算。但在实践中发现性能反而下降了一点，推测是仅仅几千个数的乘除和加法 \* 段数，耗时并不大，而内存读写已经超过了这个耗时。如果数据更大的话应该会有所提升。


# 欧拉筛法

在埃氏筛法中，一个合数会被多次筛选到，因此它不是线性的算法。欧拉筛法使每个合数只被其最小质因数筛去一次，其复杂度为 $O(n)$，在数量很大时有很大优势。

[源码](https://github.com/qianbinbin/qianbinbin.github.io/blob/source/code/goldbachs_conjecture_sieve_of_euler.c)：

```c
bool prime(char *data, uint32_t n) {
    if (n == 2)
        return true;
    if (!(n & 1))
        return false;
    return !(data[n >> 4] & (1 << (n >> 1 & 7)));
}

void sieve_of_euler(char *data) {
    uint32_t *primes = (uint32_t *) malloc(8192 * sizeof(uint32_t));
    const uint32_t SQ = (uint32_t) sqrt(UINT32_MAX), LIMIT_N = UINT32_MAX / 3;
    uint32_t count = 0, n, p, c, i, LIMIT_P;
    data[0] = 1;
    for (n = 3; n <= LIMIT_N; n += 2) {
        if (n <= SQ && !(data[n >> 4] & (1 << (n >> 1 & 7))))
            primes[count++] = n;
        LIMIT_P = UINT32_MAX / n;
        for (i = 0; i < count && (p = primes[i]) <= LIMIT_P; ++i) {
            c = p * n;
            data[c >> 4] |= 1 << (c >> 1 & 7);
            if (!(n % p))
                break;
        }
    }
    free(primes);
}
```

所有合数都能被筛去（算法的正确性）：对于任意合数 $c$，将其写成质因数乘积 $c = p\_1^{k\_1} p\_2^{k\_2}... p\_j^{k\_j}$，$c$ 一定会在 $n = p\_1^{k\_1 - 1} p\_2^{k\_2}... p\_j^{k\_j}$ 被筛去，因为 $n \ge p\_1$，即素数 $p\_1$ 必然已经被收集。

合数只被最小质因数筛去一次（算法的复杂度）：假设合数 $c = p\_1^{k\_1} p\_2^{k\_2}... p\_x^{k\_x}... p\_j^{k\_j}$ 也被 $p\_x$ 筛去，此时 $n = p\_1^{k\_1} p\_2^{k\_2}... p\_x^{k\_x - 1}... p\_j^{k\_j}$。若 $n < p\_x$，此时 $p\_x$ 还没被收集，不符合。若 $n \ge p\_x$，之前的循环中已经枚举了 $p\_1, p\_2..., p\_i$，在 $p\_1$ 的时候就 `break` 了，故不符合。

虽然是线性复杂度，实际运行好于埃氏筛法，但比起分段埃氏筛法还是慢很多：

```sh
Sieve of Euler cost 15.921141 seconds.
Same story.
Verification cost 63.412436 seconds.
```

而欧拉筛法本身又不适合改写成分段算法，cache 的威力还是相当可观的。


# 总结

试除法适用于数据较小的情况，埃氏筛法和欧拉筛法适用于数据较大的情况。如果对性能要求较高，可以考虑分段埃氏筛法。


# 参考资料

[埃拉托斯特尼筛法 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/%E5%9F%83%E6%8B%89%E6%89%98%E6%96%AF%E7%89%B9%E5%B0%BC%E7%AD%9B%E6%B3%95)

[primesieve - Segmented sieve of Eratosthenes](https://primesieve.org/segmented_sieve.html)

[筛法小结 (Eratosthenes/Euler) | __debug's Home](http://debug18.com/posts/introduction-to-sieve-method/)
