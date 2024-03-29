package io.binac.leetcode;

import io.binac.leetcode.util.ListNode;

/**
 * <p>Given a&nbsp;linked list, swap every two adjacent nodes and return its head.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/10/03/swap_ex1.jpg" style="width: 422px; height: 222px;">
 * <pre><strong>Input:</strong> head = [1,2,3,4]
 * <strong>Output:</strong> [2,1,4,3]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> head = []
 * <strong>Output:</strong> []
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> head = [1]
 * <strong>Output:</strong> [1]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li>The number of nodes in the&nbsp;list&nbsp;is in the range <code>[0, 100]</code>.</li>
 * 	<li><code>0 &lt;= Node.val &lt;= 100</code></li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <strong>Follow up:</strong> Can you solve the problem without&nbsp;modifying the values in the list's nodes? (i.e., Only nodes themselves may be changed.)
 */
public class SwapNodesInPairs {
    public static class Solution1 {
        public ListNode swapPairs(ListNode head) {
            ListNode dummy = new ListNode(0, head);
            ListNode tail = dummy, p1, p2;
            while ((p1 = tail.next) != null && (p2 = p1.next) != null) {
                p1.next = p2.next;
                p2.next = p1;
                tail.next = p2;
                tail = p1;
            }
            return dummy.next;
        }
    }
}
