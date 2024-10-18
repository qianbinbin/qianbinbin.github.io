package io.binac.leetcode;

/**
 * <p>Given an <code>m x n</code> integer matrix <code>matrix</code>, if an element is <code>0</code>, set its entire row and column to <code>0</code>'s, and return <em>the matrix</em>.</p>
 *
 * <p>You must do it <a href="https://en.wikipedia.org/wiki/In-place_algorithm" target="_blank">in place</a>.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/08/17/mat1.jpg" style="width: 450px; height: 169px;">
 * <pre><strong>Input:</strong> matrix = [[1,1,1],[1,0,1],[1,1,1]]
 * <strong>Output:</strong> [[1,0,1],[0,0,0],[1,0,1]]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/08/17/mat2.jpg" style="width: 450px; height: 137px;">
 * <pre><strong>Input:</strong> matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
 * <strong>Output:</strong> [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>m == matrix.length</code></li>
 * 	<li><code>n == matrix[0].length</code></li>
 * 	<li><code>1 &lt;= m, n &lt;= 200</code></li>
 * 	<li><code>-2<sup>31</sup> &lt;= matrix[i][j] &lt;= 2<sup>31</sup> - 1</code></li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Follow up:</strong></p>
 *
 * <ul>
 * 	<li>A straightforward solution using <code>O(mn)</code> space is probably a bad idea.</li>
 * 	<li>A simple improvement uses <code>O(m + n)</code> space, but still not the best solution.</li>
 * 	<li>Could you devise a constant space solution?</li>
 * </ul>
 */
public class SetMatrixZeroes {
    public static class Solution1 {
        public void setZeroes(int[][] matrix) {
            final int m = matrix.length, n = matrix[0].length;
            boolean setFirstCol = false, setFirstRow = false;
            int i, j;
            for (i = 0; i < m; ++i) {
                if (matrix[i][0] == 0) {
                    setFirstCol = true;
                    break;
                }
            }
            for (j = 0; j < n; ++j) {
                if (matrix[0][j] == 0) {
                    setFirstRow = true;
                    break;
                }
            }
            for (i = 1; i < m; ++i) {
                for (j = 1; j < n; ++j) {
                    if (matrix[i][j] == 0) {
                        matrix[i][0] = 0;
                        matrix[0][j] = 0;
                    }
                }
            }

            for (i = 1; i < m; ++i) {
                if (matrix[i][0] == 0)
                    for (j = 1; j < n; ++j) matrix[i][j] = 0;
            }
            for (j = 1; j < n; ++j) {
                if (matrix[0][j] == 0)
                    for (i = 1; i < m; ++i) matrix[i][j] = 0;
            }
            if (setFirstCol)
                for (i = 0; i < m; ++i) matrix[i][0] = 0;
            if (setFirstRow)
                for (j = 0; j < n; ++j) matrix[0][j] = 0;
        }
    }
}
