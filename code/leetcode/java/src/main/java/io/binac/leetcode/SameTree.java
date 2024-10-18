package io.binac.leetcode;

import io.binac.leetcode.util.TreeNode;

/**
 * <p>Given the roots of two binary trees <code>p</code> and <code>q</code>, write a function to check if they are the same or not.</p>
 *
 * <p>Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/12/20/ex1.jpg" style="width: 622px; height: 182px;">
 * <pre><strong>Input:</strong> p = [1,2,3], q = [1,2,3]
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/12/20/ex2.jpg" style="width: 382px; height: 182px;">
 * <pre><strong>Input:</strong> p = [1,2], q = [1,null,2]
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/12/20/ex3.jpg" style="width: 622px; height: 182px;">
 * <pre><strong>Input:</strong> p = [1,2,1], q = [1,1,2]
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li>The number of nodes in both trees is in the range <code>[0, 100]</code>.</li>
 * 	<li><code>-10<sup>4</sup> &lt;= Node.val &lt;= 10<sup>4</sup></code></li>
 * </ul>
 */
public class SameTree {
    public static class Solution1 {
        public boolean isSameTree(TreeNode p, TreeNode q) {
            if (p == q) return true;
            if (p == null || q == null) return false;
            return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
        }
    }
}
