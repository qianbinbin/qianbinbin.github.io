package io.binac.leetcode;

/**
 * <p>Implement the <code>myAtoi(string s)</code> function, which converts a string to a 32-bit signed integer (similar to C/C++'s <code>atoi</code> function).</p>
 *
 * <p>The algorithm for <code>myAtoi(string s)</code> is as follows:</p>
 *
 * <ol>
 * 	<li>Read in and ignore any leading whitespace.</li>
 * 	<li>Check if the next character (if not already at the end of the string) is <code>'-'</code> or <code>'+'</code>. Read this character in if it is either. This determines if the final result is negative or positive respectively. Assume the result is positive if neither is present.</li>
 * 	<li>Read in next the characters until the next non-digit charcter or the end of the input is reached. The rest of the string is ignored.</li>
 * 	<li>Convert these digits into an integer (i.e. <code>"123" -&gt; 123</code>, <code>"0032" -&gt; 32</code>). If no digits were read, then the integer is <code>0</code>. Change the sign as necessary (from step 2).</li>
 * 	<li>If the integer is out of the 32-bit signed integer range <code>[-2<sup>31</sup>, 2<sup>31</sup> - 1]</code>, then clamp the integer so that it remains in the range. Specifically, integers less than <code>-2<sup>31</sup></code> should be clamped to <code>-2<sup>31</sup></code>, and integers greater than <code>2<sup>31</sup> - 1</code> should be clamped to <code>2<sup>31</sup> - 1</code>.</li>
 * 	<li>Return the integer as the final result.</li>
 * </ol>
 *
 * <p><strong>Note:</strong></p>
 *
 * <ul>
 * 	<li>Only the space character <code>' '</code> is considered a whitespace character.</li>
 * 	<li><strong>Do not ignore</strong> any characters other than the leading whitespace or the rest of the string after the digits.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 *
 * <pre><strong>Input:</strong> str = "42"
 * <strong>Output:</strong> 42
 * <strong>Explanation:</strong> The underlined characters are what is read in, the caret is the current reader position.
 * Step 1: "42" (no characters read because there is no leading whitespace)
 *          ^
 * Step 2: "42" (no characters read because there is neither a '-' nor '+')
 *          ^
 * Step 3: "<u>42</u>" ("42" is read in)
 *            ^
 * The parsed integer is 42.
 * Since 42 is in the range [-2<sup>31</sup>, 2<sup>31</sup> - 1], the final result is 42.
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> str = "   -42"
 * <strong>Output:</strong> -42
 * <strong>Explanation:</strong>
 * Step 1: "<u>   </u>-42" (leading whitespace is read and ignored)
 *             ^
 * Step 2: "   <u>-</u>42" ('-' is read, so the result should be negative)
 *              ^
 * Step 3: "   -<u>42</u>" ("42" is read in)
 *                ^
 * The parsed integer is -42.
 * Since -42 is in the range [-2<sup>31</sup>, 2<sup>31</sup> - 1], the final result is -42.
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> str = "4193 with words"
 * <strong>Output:</strong> 4193
 * <strong>Explanation:</strong>
 * Step 1: "4193 with words" (no characters read because there is no leading whitespace)
 *          ^
 * Step 2: "4193 with words" (no characters read because there is neither a '-' nor '+')
 *          ^
 * Step 3: "<u>4193</u> with words" ("4193" is read in; reading stops because the next character is a non-digit)
 *              ^
 * The parsed integer is 4193.
 * Since 4193 is in the range [-2<sup>31</sup>, 2<sup>31</sup> - 1], the final result is 4193.
 * </pre>
 *
 * <p><strong>Example 4:</strong></p>
 *
 * <pre><strong>Input:</strong> str = "words and 987"
 * <strong>Output:</strong> 0
 * <strong>Explanation:
 * </strong>Step 1: "words and 987" (no characters read because there is no leading whitespace)
 *          ^
 * Step 2: "words and 987" (no characters read because there is neither a '-' nor '+')
 *          ^
 * Step 3: "words and 987" (reading stops immediately because there is a non-digit 'w')
 *          ^
 * The parsed integer is 0 because no digits were read.
 * Since 0 is in the range [-2<sup>31</sup>, 2<sup>31</sup> - 1], the final result is 4193.
 * </pre>
 *
 * <p><strong>Example 5:</strong></p>
 *
 * <pre><strong>Input:</strong> str = "-91283472332"
 * <strong>Output:</strong> -2147483648
 * <strong>Explanation:
 * </strong>Step 1: "-91283472332" (no characters read because there is no leading whitespace)
 *          ^
 * Step 2: "<u>-</u>91283472332" ('-' is read, so the result should be negative)
 *           ^
 * Step 3: "-<u>91283472332</u>" ("91283472332" is read in)
 *                      ^
 * The parsed integer is -91283472332.
 * Since -91283472332 is less than the lower bound of the range [-2<sup>31</sup>, 2<sup>31</sup> - 1], the final result is clamped to -2<sup>31</sup> = -2147483648.<strong><span style="display: none;"> </span></strong>
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>0 &lt;= s.length &lt;= 200</code></li>
 * 	<li><code>s</code> consists of English letters (lower-case and upper-case), digits (<code>0-9</code>), <code>' '</code>, <code>'+'</code>, <code>'-'</code>, and <code>'.'</code>.</li>
 * </ul>
 */
public class StringToIntegerAtoi {
    public static class Solution1 {
        private static boolean isDigit(char c) {
            return '0' <= c && c <= '9';
        }

        public int myAtoi(String s) {
            s = s.trim();
            final int len = s.length();
            if (len == 0)
                return 0;
            int i = 0;
            int sign = 1;
            if (s.charAt(0) == '-') {
                sign = -1;
                ++i;
            } else if (s.charAt(0) == '+') {
                ++i;
            }
            int value = 0;
            final int LIMIT = Integer.MAX_VALUE / 10;
            for (; i < len && isDigit(s.charAt(i)); ++i) {
                if (value > LIMIT || (value == LIMIT && s.charAt(i) - '0' > 7))
                    return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                value = value * 10 + s.charAt(i) - '0';
            }
            return sign * value;
        }
    }
}