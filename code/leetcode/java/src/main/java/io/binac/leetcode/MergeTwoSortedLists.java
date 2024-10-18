package io.binac.leetcode;

import io.binac.leetcode.util.ListNode;

/**
 * <p>Merge two sorted linked lists and return it as a <strong>sorted</strong> list. The list should be made by splicing together the nodes of the first two lists.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/10/03/merge_ex1.jpg" style="width: 662px; height: 302px;">
 * <pre><strong>Input:</strong> l1 = [1,2,4], l2 = [1,3,4]
 * <strong>Output:</strong> [1,1,2,3,4,4]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> l1 = [], l2 = []
 * <strong>Output:</strong> []
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> l1 = [], l2 = [0]
 * <strong>Output:</strong> [0]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li>The number of nodes in both lists is in the range <code>[0, 50]</code>.</li>
 * 	<li><code>-100 &lt;= Node.val &lt;= 100</code></li>
 * 	<li>Both <code>l1</code> and <code>l2</code> are sorted in <strong>non-decreasing</strong> order.</li>
 * </ul>
 */
public class MergeTwoSortedLists {
    public static class Solution1 {
        public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
            ListNode dummy = new ListNode(), tail = dummy;
            while (l1 != null && l2 != null) {
                if (l1.val <= l2.val) {
                    tail.next = l1;
                    tail = l1;
                    l1 = l1.next;
                } else {
                    tail.next = l2;
                    tail = l2;
                    l2 = l2.next;
                }
            }
            tail.next = l1 != null ? l1 : l2;
            return dummy.next;
        }
    }
}
