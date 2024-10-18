package io.binac.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Given an array of strings <code>words</code> and a width <code>maxWidth</code>, format the text such that each line has exactly <code>maxWidth</code> characters and is fully (left and right) justified.</p>
 *
 * <p>You should pack your words in a greedy approach; that is, pack as many words as you can in each line. Pad extra spaces <code>' '</code> when necessary so that each line has exactly <code>maxWidth</code> characters.</p>
 *
 * <p>Extra spaces between words should be distributed as evenly as possible. If the number of spaces on a line does not divide evenly between words, the empty slots on the left will be assigned more spaces than the slots on the right.</p>
 *
 * <p>For the last line of text, it should be left-justified and no extra space is inserted between words.</p>
 *
 * <p><strong>Note:</strong></p>
 *
 * <ul>
 * 	<li>A word is defined as a character sequence consisting of non-space characters only.</li>
 * 	<li>Each word's length is guaranteed to be greater than 0 and not exceed maxWidth.</li>
 * 	<li>The input array <code>words</code> contains at least one word.</li>
 * </ul>
 *
 * <p>&nbsp;</p>
 * <p><strong>Example 1:</strong></p>
 *
 * <pre><strong>Input:</strong> words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
 * <strong>Output:</strong>
 * [
 * &nbsp; &nbsp;"This &nbsp; &nbsp;is &nbsp; &nbsp;an",
 * &nbsp; &nbsp;"example &nbsp;of text",
 * &nbsp; &nbsp;"justification. &nbsp;"
 * ]</pre>
 *
 * <p><strong>Example 2:</strong></p>
 *
 * <pre><strong>Input:</strong> words = ["What","must","be","acknowledgment","shall","be"], maxWidth = 16
 * <strong>Output:</strong>
 * [
 * &nbsp; "What &nbsp; must &nbsp; be",
 * &nbsp; "acknowledgment &nbsp;",
 * &nbsp; "shall be &nbsp; &nbsp; &nbsp; &nbsp;"
 * ]
 * <strong>Explanation:</strong> Note that the last line is "shall be    " instead of "shall     be", because the last line must be left-justified instead of fully-justified.
 * Note that the second line is also left-justified becase it contains only one word.</pre>
 *
 * <p><strong>Example 3:</strong></p>
 *
 * <pre><strong>Input:</strong> words = ["Science","is","what","we","understand","well","enough","to","explain","to","a","computer.","Art","is","everything","else","we","do"], maxWidth = 20
 * <strong>Output:</strong>
 * [
 * &nbsp; "Science &nbsp;is &nbsp;what we",
 *   "understand &nbsp; &nbsp; &nbsp;well",
 * &nbsp; "enough to explain to",
 * &nbsp; "a &nbsp;computer. &nbsp;Art is",
 * &nbsp; "everything &nbsp;else &nbsp;we",
 * &nbsp; "do &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"
 * ]</pre>
 *
 * <p>&nbsp;</p>
 * <p><strong>Constraints:</strong></p>
 *
 * <ul>
 * 	<li><code>1 &lt;= words.length &lt;= 300</code></li>
 * 	<li><code>1 &lt;= words[i].length &lt;= 20</code></li>
 * 	<li><code>words[i]</code> consists of only English letters and symbols.</li>
 * 	<li><code>1 &lt;= maxWidth &lt;= 100</code></li>
 * 	<li><code>words[i].length &lt;= maxWidth</code></li>
 * </ul>
 */
public class TextJustification {
    public static class Solution1 {
        private void appendSpace(StringBuilder sb, int count) {
            for (; count > 0; --count)
                sb.append(' ');
        }

        public List<String> fullJustify(String[] words, int maxWidth) {
            List<String> result = new ArrayList<>();
            int i = 0;
            while (i < words.length) {
                int start = i, len = words[i].length(), count = 1;
                while (i + 1 < words.length && len + words[i + 1].length() + count <= maxWidth) {
                    len += words[++i].length();
                    ++count;
                }
                StringBuilder sb = new StringBuilder();
                if (i < words.length - 1) {
                    int space = maxWidth - len;
                    final int quotient = count == 1 ? space : space / (count - 1);
                    int remainder = count == 1 ? 0 : space % (count - 1);
                    sb.append(words[start]);
                    for (int j = start + 1; j <= i; ++j) {
                        appendSpace(sb, quotient);
                        if (remainder > 0) {
                            sb.append(' ');
                            --remainder;
                        }
                        sb.append(words[j]);
                    }
                } else {
                    sb.append(words[start]);
                    for (int j = start + 1; j <= i; ++j) {
                        sb.append(' ');
                        sb.append(words[j]);
                    }
                }
                appendSpace(sb, maxWidth - sb.length());
                result.add(sb.toString());
                ++i;
            }
            return result;
        }
    }
}
