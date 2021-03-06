package io.binac.leetcode;

import io.binac.leetcode.util.TreeLinkNode;

/**
 * Given a binary tree
 * <blockquote><pre>
 *     struct TreeLinkNode {
 *       TreeLinkNode *left;
 *       TreeLinkNode *right;
 *       TreeLinkNode *next;
 *     }
 * </blockquote></pre>
 * Populate each next pointer to point to its next right node. If there is no next right node, the next pointer should be set to NULL.
 * <p>
 * <p>Initially, all next pointers are set to NULL.
 * <p>
 * <p>Note:
 * <p>
 * <p>You may only use constant extra space.
 * <p>Recursive approach is fine, implicit stack space does not count as extra space for this problem.
 * <p>
 * <p>Example:
 * <p>
 * <p>Given the following binary tree,
 * <blockquote><pre>
 *          1
 *        /  \
 *       2    3
 *      / \    \
 *     4   5    7
 * </blockquote></pre>
 * After calling your function, the tree should look like:
 * <blockquote><pre>
 *          1 -> NULL
 *        /  \
 *       2 -> 3 -> NULL
 *      / \    \
 *     4-> 5 -> 7 -> NULL
 * </blockquote></pre>
 */
public class PopulatingNextRightPointersInEachNodeII {
    public static class Solution1 {
        public void connect(TreeLinkNode root) {
            if (root == null) return;

            TreeLinkNode dummy = new TreeLinkNode(0), tail = dummy;
            while (root != null) {
                if (root.left != null) {
                    tail.next = root.left;
                    tail = tail.next;
                }
                if (root.right != null) {
                    tail.next = root.right;
                    tail = tail.next;
                }
                if (root.next != null) {
                    root = root.next;
                } else {
                    root = dummy.next;
                    dummy.next = null;
                    tail = dummy;
                }
            }
        }
    }
}
