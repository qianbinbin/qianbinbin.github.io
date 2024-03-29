"""
Given a string s, partition s such that every substring of the partition is a palindrome. Return all possible palindrome partitioning of s.

A palindrome string is a string that reads the same backward as forward.



Example 1:

Input: s = "aab"
Output: [["a","a","b"],["aa","b"]]

Example 2:

Input: s = "a"
Output: [["a"]]


Constraints:

1 <= s.length <= 16
s contains only lowercase English letters.
"""
from typing import List


class Solution1:
    def partition(self, s: str) -> List[List[str]]:
        size = len(s)
        dp = [[False] * size for _ in range(size)]
        for i in range(size - 1, -1, -1):
            for j in range(i, size):
                if s[i] == s[j] and (j - i < 2 or dp[i + 1][j - 1]):
                    dp[i][j] = True
        result = []
        self._partition(s, 0, dp, result, [])
        return result

    def _partition(self, s: str, i: int, palindrome: List[List[bool]], result: List[List[str]], path: List[str]):
        if i == len(s):
            result.append(path[:])
            return
        for j in range(i, len(s)):
            if palindrome[i][j]:
                path.append(s[i:j + 1])
                self._partition(s, j + 1, palindrome, result, path)
                path.pop()
