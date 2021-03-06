// Given a positive integer n, find the least number of perfect square numbers
// (for example, 1, 4, 9, 16, ...) which sum to n.
//
// Example 1:
//
// Input: n = 12
// Output: 3
// Explanation: 12 = 4 + 4 + 4.
//
// Example 2:
//
// Input: n = 13
// Output: 2
// Explanation: 13 = 4 + 9.

#ifndef LEETCODECPP_PERFECTSQUARES_H
#define LEETCODECPP_PERFECTSQUARES_H

#include <limits>
#include <vector>

namespace lcpp {

class Solution279_1 {
public:
  int numSquares(int n);
};

class Solution279_2 {
public:
  int numSquares(int n);

private:
  static std::vector<int> &getNums();
  static const int Max = std::numeric_limits<int>::max();
};

class Solution279_3 {
public:
  int numSquares(int n);
};

class Solution279_4 {
public:
  int numSquares(int n);
};

} // namespace lcpp

#endif // LEETCODECPP_PERFECTSQUARES_H
