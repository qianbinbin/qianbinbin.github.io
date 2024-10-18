package io.binac.leetcode;

/**
 * <p>Given an integer array <code>nums</code>, find the contiguous subarray (containing at least one number) which has the largest sum and return <em>its sum</em>.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 *
 * <pre><strong>Input:</strong> nums = [-2,1,-3,4,-1,2,1,-5,4]
 * <strong>Output:</strong> 6
 * <strong>Explanation:</strong> [4,-1,2,1] has the largest sum = 6.
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> nums = [1]
 * <strong>Output:</strong> 1
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> nums = [0]
 * <strong>Output:</strong> 0
 * </pre>
 *
 * <p><strong>Example 4:</strong></p>
 *
 * <pre><strong>Input:</strong> nums = [-1]
 * <strong>Output:</strong> -1
 * </pre>
 *
 * <p><strong>Example 5:</strong></p>
 *
 * <pre><strong>Input:</strong> nums = [-100000]
 * <strong>Output:</strong> -100000
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>1 &lt;= nums.length &lt;= 3 * 10<sup>4</sup></code></li>
 * 	<li><code>-10<sup>5</sup> &lt;= nums[i] &lt;= 10<sup>5</sup></code></li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <strong>Follow up:</strong> If you have figured out the <code>O(n)</code> solution, try coding another solution using the <strong>divide and conquer</strong> approach, which is more subtle.
 */
public class MaximumSubarray {
    public static class Solution1 {
        public int maxSubArray(int[] nums) {
            int dp = -1, result = Integer.MIN_VALUE;
            for (int num : nums) {
                dp = dp < 0 ? num : dp + num;
                result = Math.max(result, dp);
            }
            return result;
        }
    }
}
