package io.binac.leetcode;

import io.binac.leetcode.util.ListNode;

/**
 * <p>Given the <code>head</code> of a linked&nbsp;list, rotate the list to the right by <code>k</code> places.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/11/13/rotate1.jpg" style="width: 600px; height: 254px;">
 * <pre><strong>Input:</strong> head = [1,2,3,4,5], k = 2
 * <strong>Output:</strong> [4,5,1,2,3]
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/11/13/roate2.jpg" style="width: 472px; height: 542px;">
 * <pre><strong>Input:</strong> head = [0,1,2], k = 4
 * <strong>Output:</strong> [2,0,1]
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li>The number of nodes in the list is in the range <code>[0, 500]</code>.</li>
 * 	<li><code>-100 &lt;= Node.val &lt;= 100</code></li>
 * 	<li><code>0 &lt;= k &lt;= 2 * 10<sup>9</sup></code></li>
 * </ul>
 */
public class RotateList {
    public static class Solution1 {
        public ListNode rotateRight(ListNode head, int k) {
            if (head == null)
                return null;
            int n = 0;
            for (ListNode p = head; p != null; p = p.next)
                ++n;
            k %= n;

            ListNode slow = head, fast = head;
            while (k-- > 0) fast = fast.next;
            while (fast.next != null) {
                fast = fast.next;
                slow = slow.next;
            }
            fast.next = head;
            head = slow.next;
            slow.next = null;
            return head;
        }
    }
}
