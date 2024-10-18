package io.binac.leetcode;

import io.binac.leetcode.util.ListNode;

/**
 * <p>Given the <code>head</code> of a linked list, remove the <code>n<sup>th</sup></code> node from the end of the list and return its head.</p>
 *
 * <p><strong>Follow up:</strong>&nbsp;Could you do this in one pass?</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/10/03/remove_ex1.jpg" style="width: 542px; height: 222px;">
 * <pre><strong>Input:</strong> head = [1,2,3,4,5], n = 2
 * <strong>Output:</strong> [1,2,3,5]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> head = [1], n = 1
 * <strong>Output:</strong> []
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> head = [1,2], n = 1
 * <strong>Output:</strong> [1]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li>The number of nodes in the list is <code>sz</code>.</li>
 * 	<li><code>1 &lt;= sz &lt;= 30</code></li>
 * 	<li><code>0 &lt;= Node.val &lt;= 100</code></li>
 * 	<li><code>1 &lt;= n &lt;= sz</code></li>
 * </ul>
 */
public class RemoveNthNodeFromEndOfList {
    public static class Solution1 {
        public ListNode removeNthFromEnd(ListNode head, int n) {
            ListNode dummy = new ListNode(0, head), fast = dummy, slow = dummy;
            for (; n >= 0; --n)
                fast = fast.next;
            while (fast != null) {
                slow = slow.next;
                fast = fast.next;
            }
            slow.next = slow.next.next;
            return dummy.next;
        }
    }
}
