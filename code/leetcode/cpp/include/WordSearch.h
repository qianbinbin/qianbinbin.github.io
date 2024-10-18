// Given an m x n grid of characters board and a string word, return true if
// word exists in the grid.
//
// The word can be constructed from letters of sequentially adjacent cells,
// where adjacent cells are horizontally or vertically neighboring. The same
// letter cell may not be used more than once.
//
//
//
// Example 1:
// https://assets.leetcode.com/uploads/2020/11/04/word2.jpg
//
// Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
// = "ABCCED" Output: true
//
// Example 2:
// https://assets.leetcode.com/uploads/2020/11/04/word-1.jpg
//
// Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
// = "SEE" Output: true
//
// Example 3:
// https://assets.leetcode.com/uploads/2020/10/15/word3.jpg
//
// Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
// = "ABCB" Output: false
//
//
// Constraints:
//
// m == board.length
// n = board[i].length
// 1 <= m, n <= 6
// 1 <= word.length <= 15
// board and word consists of only lowercase and uppercase English letters.
//
//
// Follow up: Could you use search pruning to make your solution faster with a
// larger board?

#ifndef LEETCODECPP_WORDSEARCH_H
#define LEETCODECPP_WORDSEARCH_H

#include <string>
#include <vector>

namespace lcpp {

class Solution79_1 {
public:
  bool exist(std::vector<std::vector<char>> &board, std::string word);
};

} // namespace lcpp

#endif // LEETCODECPP_WORDSEARCH_H
