package io.binac.leetcode;

/**
 * <p>Given a <code>m x n</code> <code>grid</code> filled with non-negative numbers, find a path from top left to bottom right, which minimizes the sum of all numbers along its path.</p>
 *
 * <p><strong>Note:</strong> You can only move either down or right at any point in time.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/11/05/minpath.jpg" style="width: 242px; height: 242px;">
 * <pre><strong>Input:</strong> grid = [[1,3,1],[1,5,1],[4,2,1]]
 * <strong>Output:</strong> 7
 * <strong>Explanation:</strong> Because the path 1 → 3 → 1 → 1 → 1 minimizes the sum.
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> grid = [[1,2,3],[4,5,6]]
 * <strong>Output:</strong> 12
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>m == grid.length</code></li>
 * 	<li><code>n == grid[i].length</code></li>
 * 	<li><code>1 &lt;= m, n &lt;= 200</code></li>
 * 	<li><code>0 &lt;= grid[i][j] &lt;= 100</code></li>
 * </ul>
 */
public class MinimumPathSum {
    public static class Solution1 {
        public int minPathSum(int[][] grid) {
            final int m = grid.length, n = grid[0].length;
            int[] dp = new int[n];
            dp[0] = grid[0][0];
            for (int j = 1; j < n; ++j)
                dp[j] = dp[j - 1] + grid[0][j];
            for (int i = 1; i < m; ++i) {
                dp[0] += grid[i][0];
                for (int j = 1; j < n; ++j)
                    dp[j] = Math.min(dp[j - 1], dp[j]) + grid[i][j];
            }
            return dp[n - 1];
        }
    }
}
