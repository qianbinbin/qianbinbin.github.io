---
title: Best Time to Buy and Sell Stock III
date: 2018-05-04 22:25:48
mathjax: true
tags:
- LeetCode
- 算法
- 动态规划
---

股票买入卖出的最佳时间问题：
<https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/>

> Say you have an array for which the ith element is the price of a given stock on day i.
> 
> Design an algorithm to find the maximum profit. You may complete at most two transactions.
> 
> Note: You may not engage in multiple transactions at the same time (i.e., you must sell the stock before you buy again).
> 
> Example 1:
> 
> Input: [3,3,5,0,0,3,1,4]
> Output: 6
> Explanation: Buy on day 4 (price = 0) and sell on day 6 (price = 3), profit = 3-0 = 3.
>              Then buy on day 7 (price = 1) and sell on day 8 (price = 4), profit = 4-1 = 3.
> 
> Example 2:
> 
> Input: [1,2,3,4,5]
> Output: 4
> Explanation: Buy on day 1 (price = 1) and sell on day 5 (price = 5), profit = 5-1 = 4.
>              Note that you cannot buy on day 1, buy on day 2 and sell them later, as you are
>              engaging multiple transactions at the same time. You must sell before buying again.
> 
> Example 3:
> 
> Input: [7,6,4,3,1]
> Output: 0
> Explanation: In this case, no transaction is done, i.e. max profit = 0.

意即给定一个数组，数组下标表示时间，元素值表示股票价格，买入、卖出最多两次，且买入前必须先卖出，求最大利润。

<!-- more -->

# 只买卖一次的情况

与 [Best Time to Buy and Sell Stock - LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/description/) 不同的是，这道题可以买卖两次，而后者只能买卖一次。

对于只能买卖一次的情况，可以轻松求解：遍历数组，保存截止目前最小值，并不断更新最大利润，这也可以用动态规划来解释，递推公式为：

$$f(i) = \max\\{f(i - 1), prices[i] - price\_{min}\\}$$

$price\_{min}$ 也可以用递推公式表示：

$$price\_{min}(i) = \min\\{price\_{min}(i - 1), prices[i]\\}$$

# 最多买卖两次的情况

对于最多买卖两次的情况，其实可以简单地看作买卖两次，当第一次卖出和第二次买入同时发生时，就可以看作是买卖一次的情况。因此，可以直接按照买卖两次处理。

为方便起见，用 $hold\_1$、$release\_1$ 分别表示第一次买入和卖出后的剩余资产，$hold\_2$、$release\_2$ 分别表示第二次买入和卖出后的剩余资产，我们的目标是使每次净资产最大化，最后得到的就是利润。

参考只允许买卖一次的问题，很容易得出以下递推公式：

$$release\_1(i) = \max\\{release\_1(i - 1), prices[i] + hold\_1(i - 1)\\}$$

$$hold\_1(i) = \max\\{hold\_1(i - 1), -prices[i]\\}$$

第二次买卖递推公式：

$$release\_2(i) = \max\\{release\_2(i - 1), prices[i] + hold\_2(i - 1)\\}$$

$$hold\_2(i) = \max\\{hold\_2(i - 1), release\_1(i - 1) - prices[i]\\}$$

注意先后顺序，由于卖出时要用到之前买入的剩余资产，因此应先计算卖出，再计算买入。同样地，应先计算第二次买卖，再计算第一次买卖。如果顺序打乱，即使得到的结果是正确的，但算法思想就不对了。

如果对两次买卖都使用长度为 $n$ 的数组保存，空间复杂度就为 $O(n)$，但元素只利用一次，可以优化为 $O(n)$ 的空间复杂度。

# 最多买卖 k 次的情况

明白了买卖两次的原理，就能很容易扩展到最多买卖 $k$ 次的情况，只需要把 $release\_i$ 和 $hold\_i$ 改为长度为 $k$ 的数组，更新步骤写成一个 $k$ 次的循环即可。

这就是 [Best Time to Buy and Sell Stock IV - LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/description/) 的思路。

# 实现源码

<https://github.com/qianbinbin/leetcode>

# 参考资料

1. [Is it Best Solution with O(n), O(1). - LeetCode Discuss](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/discuss/39611/Is-it-Best-Solution-with-O%28n%29-O%281%29.)
