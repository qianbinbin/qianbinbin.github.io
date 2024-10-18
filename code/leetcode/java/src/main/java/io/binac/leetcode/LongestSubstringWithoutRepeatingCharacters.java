package io.binac.leetcode;


import java.util.Arrays;

/**
 * <p>Given a string <code>s</code>, find the length of the <b>longest substring</b> without repeating characters.</p>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "abcabcbb"
 * <strong>Output:</strong> 3
 * <strong>Explanation:</strong> The answer is "abc", with the length of 3.
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "bbbbb"
 * <strong>Output:</strong> 1
 * <strong>Explanation:</strong> The answer is "b", with the length of 1.
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "pwwkew"
 * <strong>Output:</strong> 3
 * <strong>Explanation:</strong> The answer is "wke", with the length of 3.
 * Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
 * </pre>
 *
 * <p><strong>Example 4:</strong></p>
 *
 * <pre><strong>Input:</strong> s = ""
 * <strong>Output:</strong> 0
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>0 &lt;= s.length &lt;= 5 * 10<sup>4</sup></code></li>
 * 	<li><code>s</code> consists of English letters, digits, symbols and spaces.</li>
 * </ul>
 */
public class LongestSubstringWithoutRepeatingCharacters {
    public static class Solution1 {
        public int lengthOfLongestSubstring(String s) {
            int[] map = new int[128];
            Arrays.fill(map, -1);
            int pre = -1, i, len = 0, size = s.length();
            char ch;
            for (i = 0; i < size; ++i) {
                ch = s.charAt(i);
                pre = Math.max(pre, map[ch]);
                len = Math.max(len, i - pre);
                map[ch] = i;
            }
            return len;
        }
    }
}
