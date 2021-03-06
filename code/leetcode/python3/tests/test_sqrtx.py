from unittest import TestCase

from leetcodepy.sqrtx import *

solution1 = Solution1()

solution2 = Solution2()

x1 = 4

expected1 = 2

x2 = 8

expected2 = 2


class TestSqrt(TestCase):
    def test1(self):
        self.assertEqual(expected1, solution1.mySqrt(x1))
        self.assertEqual(expected2, solution1.mySqrt(x2))

    def test2(self):
        self.assertEqual(expected1, solution2.mySqrt(x1))
        self.assertEqual(expected2, solution2.mySqrt(x2))
