package io.binac.leetcode;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>Given a string <code>s</code> containing just the characters <code>'('</code>, <code>')'</code>, <code>'{'</code>, <code>'}'</code>, <code>'['</code> and <code>']'</code>, determine if the input string is valid.</p>
 *
 * <p>An input string is valid if:</p>
 *
 * <ol>
 * 	<li>Open brackets must be closed by the same type of brackets.</li>
 * 	<li>Open brackets must be closed in the correct order.</li>
 * </ol>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "()"
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "()[]{}"
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "(]"
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p><strong>Example 4:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "([)]"
 * <strong>Output:</strong> false
 * </pre>
 *
 * <p><strong>Example 5:</strong></p>
 *
 * <pre><strong>Input:</strong> s = "{[]}"
 * <strong>Output:</strong> true
 * </pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>1 &lt;= s.length &lt;= 10<sup>4</sup></code></li>
 * 	<li><code>s</code> consists of parentheses only <code>'()[]{}'</code>.</li>
 * </ul>
 */
public class ValidParentheses {
    public static class Solution1 {
        public boolean isValid(String s) {
            Deque<Character> stack = new ArrayDeque<>();
            for (int i = 0, len = s.length(); i < len; ++i) {
                final char ch = s.charAt(i);
                switch (ch) {
                    case '(':
                    case '{':
                    case '[':
                        stack.push(ch);
                        break;
                    case ')':
                        if (stack.isEmpty() || stack.pop() != '(')
                            return false;
                        break;
                    case '}':
                        if (stack.isEmpty() || stack.pop() != '{')
                            return false;
                        break;
                    case ']':
                        if (stack.isEmpty() || stack.pop() != '[')
                            return false;
                        break;
                    default:
                        break;
                }
            }
            return stack.isEmpty();
        }
    }
}
