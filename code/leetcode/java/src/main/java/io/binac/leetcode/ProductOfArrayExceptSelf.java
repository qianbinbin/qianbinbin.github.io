package io.binac.leetcode;

/**
 * <p>Given an integer array <code>nums</code>, return <em>an array</em> <code>answer</code> <em>such that</em> <code>answer[i]</code> <em>is equal to the product of all the elements of</em> <code>nums</code> <em>except</em> <code>nums[i]</code>.</p>
 *
 * <p>The product of any prefix or suffix of <code>nums</code> is <strong>guaranteed</strong> to fit in a <strong>32-bit</strong> integer.</p>
 *
 * <p>You must write an algorithm that runs in&nbsp;<code>O(n)</code>&nbsp;time and without using the division operation.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <pre><strong>Input:</strong> nums = [1,2,3,4]
 * <strong>Output:</strong> [24,12,8,6]
 * </pre><p><strong>Example 2:</strong></p>
 * <pre><strong>Input:</strong> nums = [-1,1,0,-3,3]
 * <strong>Output:</strong> [0,0,9,0,0]
 * </pre>
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>2 &lt;= nums.length &lt;= 10<sup>5</sup></code></li>
 * 	<li><code>-30 &lt;= nums[i] &lt;= 30</code></li>
 * 	<li>The product of any prefix or suffix of <code>nums</code> is <strong>guaranteed</strong> to fit in a <strong>32-bit</strong> integer.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Follow up:</strong>&nbsp;Can you solve the problem in <code>O(1)&nbsp;</code>extra&nbsp;space complexity? (The output array <strong>does not</strong> count as extra space for space complexity analysis.)</p>
 */
public class ProductOfArrayExceptSelf {
    public static class Solution1 {
        public int[] productExceptSelf(int[] nums) {
            final int n = nums.length;
            int[] result = new int[n];
            result[0] = 1;
            for (int i = 1; i < n; ++i)
                result[i] = result[i - 1] * nums[i - 1];
            int rightProduct = 1;
            for (int i = n - 1; i >= 0; --i) {
                result[i] *= rightProduct;
                rightProduct *= nums[i];
            }
            return result;
        }
    }
}
