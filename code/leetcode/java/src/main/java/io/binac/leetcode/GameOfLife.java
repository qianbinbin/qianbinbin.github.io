package io.binac.leetcode;

/**
 * <p>According to&nbsp;<a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life" target="_blank">Wikipedia's article</a>: "The <b>Game of Life</b>, also known simply as <b>Life</b>, is a cellular automaton devised by the British mathematician John Horton Conway in 1970."</p>
 *
 * <p>The board is made up of an <code>m x n</code> grid of cells, where each cell has an initial state: <b>live</b> (represented by a <code>1</code>) or <b>dead</b> (represented by a <code>0</code>). Each cell interacts with its <a href="https://en.wikipedia.org/wiki/Moore_neighborhood" target="_blank">eight neighbors</a> (horizontal, vertical, diagonal) using the following four rules (taken from the above Wikipedia article):</p>
 *
 * <ol>
 * 	<li>Any live cell with fewer than two live neighbors dies as if caused by under-population.</li>
 * 	<li>Any live cell with two or three live neighbors lives on to the next generation.</li>
 * 	<li>Any live cell with more than three live neighbors dies, as if by over-population.</li>
 * 	<li>Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.</li>
 * </ol>
 *
 * <p><span>The next state is created by applying the above rules simultaneously to every cell in the current state, where births and deaths occur simultaneously. Given the current state of the <code>m x n</code> grid <code>board</code>, return <em>the next state</em>.</span></p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/12/26/grid1.jpg" style="width: 562px; height: 322px;">
 * <pre><strong>Input:</strong> board = [[0,1,0],[0,0,1],[1,1,1],[0,0,0]]
 * <strong>Output:</strong> [[0,0,0],[1,0,1],[0,1,1],[0,1,0]]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/12/26/grid2.jpg" style="width: 402px; height: 162px;">
 * <pre><strong>Input:</strong> board = [[1,1],[1,0]]
 * <strong>Output:</strong> [[1,1],[1,1]]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>m == board.length</code></li>
 * 	<li><code>n == board[i].length</code></li>
 * 	<li><code>1 &lt;= m, n &lt;= 25</code></li>
 * 	<li><code>board[i][j]</code> is <code>0</code> or <code>1</code>.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Follow up:</strong></p>
 *
 * <ul>
 * 	<li>Could you solve it in-place? Remember that the board needs to be updated simultaneously: You cannot update some cells first and then use their updated values to update other cells.</li>
 * 	<li>In this question, we represent the board using a 2D array. In principle, the board is infinite, which would cause problems when the active area encroaches upon the border of the array (i.e., live cells reach the border). How would you address these problems?</li>
 * </ul>
 */
public class GameOfLife {
    public static class Solution1 {
        private int neighborsCount(int[][] board, int m, int n, int i, int j) {
            int result = 0;
            for (int p = Math.max(0, i - 1), pe = Math.min(m, i + 2), q, qe; p < pe; ++p) {
                for (q = Math.max(0, j - 1), qe = Math.min(n, j + 2); q < qe; ++q)
                    result += board[p][q] & 1;
            }
            result -= board[i][j] & 1;
            return result;
        }

        public void gameOfLife(int[][] board) {
            final int m = board.length, n = board[0].length;
            for (int i = 0, j; i < m; ++i) {
                for (j = 0; j < n; ++j) {
                    switch (neighborsCount(board, m, n, i, j)) {
                        case 2:
                            board[i][j] |= (board[i][j] & 1) << 1;
                            break;
                        case 3:
                            board[i][j] |= 2;
                            break;
                        default:
                            break;
                    }
                }
            }
            for (int i = 0, j; i < m; ++i) {
                for (j = 0; j < n; ++j)
                    board[i][j] >>>= 1;
            }
        }
    }
}
