package io.binac.leetcode;

/**
 * <p>Given an unsorted integer array <code>nums</code>, find the smallest missing&nbsp;positive integer.</p>
 *
 * <p><strong>Follow up:</strong>&nbsp;Could you implement an&nbsp;algorithm that runs in <code>O(n)</code> time and uses constant extra space.?</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <pre><strong>Input:</strong> nums = [1,2,0]
 * <strong>Output:</strong> 3
 * </pre><p><strong>Example 2:</strong></p>
 * <pre><strong>Input:</strong> nums = [3,4,-1,1]
 * <strong>Output:</strong> 2
 * </pre><p><strong>Example 3:</strong></p>
 * <pre><strong>Input:</strong> nums = [7,8,9,11,12]
 * <strong>Output:</strong> 1
 * </pre>
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>0 &lt;= nums.length &lt;= 300</code></li>
 * 	<li><code>-2<sup>31</sup> &lt;= nums[i] &lt;= 2<sup>31</sup> - 1</code></li>
 * </ul>
 */
public class FirstMissingPositive {
    public static class Solution1 {
        private static void swap(int[] x, int a, int b) {
            int t = x[a];
            x[a] = x[b];
            x[b] = t;
        }

        public int firstMissingPositive(int[] nums) {
            for (int i = 0, val; i < nums.length; ++i) {
                while ((val = nums[i]) != i + 1 && 0 < val && val <= nums.length && nums[val - 1] != val)
                    swap(nums, i, val - 1);
            }
            for (int i = 0; i < nums.length; ++i) {
                if (nums[i] != i + 1)
                    return i + 1;
            }
            return nums.length + 1;
        }
    }
}
