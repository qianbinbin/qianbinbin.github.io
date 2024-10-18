package io.binac.leetcode;

/**
 * <p>Given strings <code>s1</code>, <code>s2</code>, and <code>s3</code>, find whether <code>s3</code> is formed by an <strong>interleaving</strong> of <code>s1</code> and <code>s2</code>.</p>
 *
 * <p>An <strong>interleaving</strong> of two strings <code>s</code> and <code>t</code> is a configuration where they are divided into <strong>non-empty</strong> substrings such that:</p>
 *
 * <ul>
 * 	<li><code>s = s<sub>1</sub> + s<sub>2</sub> + ... + s<sub>n</sub></code></li>
 * 	<li><code>t = t<sub>1</sub> + t<sub>2</sub> + ... + t<sub>m</sub></code></li>
 * 	<li><code>|n - m| &lt;= 1</code></li>
 * 	<li>The <strong>interleaving</strong> is <code>s<sub>1</sub> + t<sub>1</sub> + s<sub>2</sub> + t<sub>2</sub> + s<sub>3</sub> + t<sub>3</sub> + ...</code> or <code>t<sub>1</sub> + s<sub>1</sub> + t<sub>2</sub> + s<sub>2</sub> + t<sub>3</sub> + s<sub>3</sub> + ...</code></li>
 * </ul>
 *
 * <p><strong>Note:</strong> <code>a + b</code> is the concatenation of strings <code>a</code> and <code>b</code>.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 * <img alt="" src="https://assets.leetcode.com/uploads/2020/09/02/interleave.jpg" style="width: 561px; height: 203px;">
 * <pre><strong>Input:</strong> s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc"
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> s1 = "", s2 = "", s3 = ""
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>0 &lt;= s1.length, s2.length &lt;= 100</code></li>
 * 	<li><code>0 &lt;= s3.length &lt;= 200</code></li>
 * 	<li><code>s1</code>, <code>s2</code>, and <code>s3</code> consist of lowercase English letters.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Follow up:</strong> Could you solve it using only <code>O(s2.length)</code> additional memory space?</p>
 */
public class InterleavingString {
    public static class Solution1 {
        public boolean isInterleave(String s1, String s2, String s3) {
            final int m = s1.length(), n = s2.length();
            if (m + n != s3.length()) return false;

            boolean[] dp = new boolean[n + 1];
            dp[0] = true;
            for (int j = 1; j <= n && s2.charAt(j - 1) == s3.charAt(j - 1); ++j)
                dp[j] = true;
            for (int i = 1, j; i <= m; ++i) {
                dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);
                for (j = 1; j <= n; ++j) {
                    dp[j] = (dp[j] && s1.charAt(i - 1) == s3.charAt(i + j - 1)) ||
                            (dp[j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
                }
            }
            return dp[n];
        }
    }
}
