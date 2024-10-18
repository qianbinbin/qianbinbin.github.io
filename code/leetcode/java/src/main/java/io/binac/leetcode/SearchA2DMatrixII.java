package io.binac.leetcode;

/**
 * <p>Write an efficient algorithm that searches for a <code>target</code> value in an <code>m x n</code> integer <code>matrix</code>. The <code>matrix</code> has the following properties:</p>
 *
 * <ul>
 * 	<li>Integers in each row are sorted in ascending from left to right.</li>
 * 	<li>Integers in each column are sorted in ascending from top to bottom.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/11/24/searchgrid2.jpg" style="width: 300px; height: 300px;">
 * <pre><strong>Input:</strong> matrix = [[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]], target = 5
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/11/24/searchgrid.jpg" style="width: 300px; height: 300px;">
 * <pre><strong>Input:</strong> matrix = [[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]], target = 20
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>m == matrix.length</code></li>
 * 	<li><code>n == matrix[i].length</code></li>
 * 	<li><code>1 &lt;= n, m &lt;= 300</code></li>
 * 	<li><code>-10<sup>9</sup> &lt;= matrix[i][j] &lt;= 10<sup>9</sup></code></li>
 * 	<li>All the integers in each row are <strong>sorted</strong> in ascending order.</li>
 * 	<li>All the integers in each column are <strong>sorted</strong> in ascending order.</li>
 * 	<li><code>-10<sup>9</sup> &lt;= target &lt;= 10<sup>9</sup></code></li>
 * </ul>
 */
public class SearchA2DMatrixII {
    public static class Solution1 {
        public boolean searchMatrix(int[][] matrix, int target) {
            final int m = matrix.length, n = matrix[0].length;
            int i = m - 1, j = 0;
            while (i >= 0 && j < n) {
                if (matrix[i][j] < target)
                    ++j;
                else if (matrix[i][j] > target)
                    --i;
                else
                    return true;
            }
            return false;
        }
    }
}
