from unittest import TestCase

from leetcodepy.remove_duplicates_from_sorted_array_ii import *

SOLUTION1 = Solution1()


def NUMS1():
    return [1, 1, 1, 2, 2, 3]


EXPECTED1 = [1, 1, 2, 2, 3]


def NUMS2():
    return [0, 0, 1, 1, 1, 1, 2, 3, 3]


EXPECTED2 = [0, 0, 1, 1, 2, 3, 3]


class TestRemoveDuplicatesFromSortedArrayII(TestCase):
    def test1(self):
        nums1 = NUMS1()
        size1 = SOLUTION1.removeDuplicates(nums1)
        self.assertListEqual(EXPECTED1, nums1[:size1])

        nums2 = NUMS2()
        size2 = SOLUTION1.removeDuplicates(nums2)
        self.assertListEqual(EXPECTED2, nums2[:size2])
