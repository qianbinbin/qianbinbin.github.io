"""
Say you have an array for which the ith element is the price of a given stock on day i.

Design an algorithm to find the maximum profit. You may complete at most two transactions.

Note: You may not engage in multiple transactions at the same time (i.e., you must sell the stock before you buy again).

Example 1:

Input: [3,3,5,0,0,3,1,4]
Output: 6
Explanation: Buy on day 4 (price = 0) and sell on day 6 (price = 3), profit = 3-0 = 3.
             Then buy on day 7 (price = 1) and sell on day 8 (price = 4), profit = 4-1 = 3.

Example 2:

Input: [1,2,3,4,5]
Output: 4
Explanation: Buy on day 1 (price = 1) and sell on day 5 (price = 5), profit = 5-1 = 4.
             Note that you cannot buy on day 1, buy on day 2 and sell them later, as you are
             engaging multiple transactions at the same time. You must sell before buying again.

Example 3:

Input: [7,6,4,3,1]
Output: 0
Explanation: In this case, no transaction is done, i.e. max profit = 0.
"""
from typing import List


class Solution1:
    def maxProfit(self, prices):
        """
        :type prices: List[int]
        :rtype: int
        """
        hold1, release1 = float('-inf'), 0
        hold2, release2 = float('-inf'), 0
        for price in prices:
            release2 = max(release2, hold2 + price)
            hold2 = max(hold2, release1 - price)
            release1 = max(release1, hold1 + price)
            hold1 = max(hold1, -price)
        return release2


class Solution2:
    def maxProfit(self, prices):
        """
        :type prices: List[int]
        :rtype: int
        """
        if not prices:
            return 0
        size = len(prices)
        f, g = [0] * size, [0] * size
        min_price = prices[0]
        for i in range(1, size):
            f[i] = max(f[i - 1], prices[i] - min_price)
            min_price = min(min_price, prices[i])
        max_price = prices[size - 1]
        for i in range(size - 2, -1, -1):
            g[i] = max(g[i + 1], max_price - prices[i])
            max_price = max(max_price, prices[i])
        result = 0
        for i in range(size):
            result = max(result, f[i] + g[i])
        return result
