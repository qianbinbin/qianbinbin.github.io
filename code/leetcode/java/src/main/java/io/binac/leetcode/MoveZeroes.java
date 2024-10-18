package io.binac.leetcode;

import java.util.Arrays;

/**
 * <p>Given an integer array <code>nums</code>, move all <code>0</code>'s to the end of it while maintaining the relative order of the non-zero elements.</p>
 *
 * <p><strong>Note</strong> that you must do this in-place without making a copy of the array.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <pre><strong>Input:</strong> nums = [0,1,0,3,12]
 * <strong>Output:</strong> [1,3,12,0,0]
 * </pre><p><strong>Example 2:</strong></p>
 * <pre><strong>Input:</strong> nums = [0]
 * <strong>Output:</strong> [0]
 * </pre>
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>1 &lt;= nums.length &lt;= 10<sup>4</sup></code></li>
 * 	<li><code>-2<sup>31</sup> &lt;= nums[i] &lt;= 2<sup>31</sup> - 1</code></li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <strong>Follow up:</strong> Could you minimize the total number of operations done?
 */
public class MoveZeroes {
    public static class Solution1 {
        public void moveZeroes(int[] nums) {
            int size = 0;
            for (int n : nums) {
                if (n != 0)
                    nums[size++] = n;
            }
            Arrays.fill(nums, size, nums.length, 0);
        }
    }
}
