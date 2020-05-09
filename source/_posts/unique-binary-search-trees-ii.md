---
title: 批量构建二叉查找树时的一个常见错误
date: 2018-04-03 20:52:42
tags:
- LeetCode
- 算法
- 二叉树
---

给定一个正整数 n，生成结点值为 1 ~ n 的所有二叉查找树：
<https://leetcode.com/problems/unique-binary-search-trees-ii/>

> Given an integer n, generate all structurally unique BST's (binary search trees) that store values 1...n.
> 
> For example,
> Given n = 3, your program should return all 5 unique BST's shown below.
```
1         3     3      2      1
 \       /     /      / \      \
  3     2     1      1   3      2
 /     /       \                 \
2     1         2                 3
```

这个问题可以用递归来求解，对于一个值，先分别生成它的左右子树的集合，然后把这些子树组合即可。

算法明明已经 AC 了，然而我在本地测试运行时却出现了 `Exception: EXC_BAD_ACCESS (code=EXC_I386_GPFLT)` 错误，这个错误是在访问已经 free 掉的内存时产生的。

经过一番 debug，终于发现了问题所在，同时发现网上很多方法也有这个 bug。

<!-- more -->

# 排查调用 free 函数

我在两个地方调用 `free` 函数释放内存，一个是在组合左右子树后，释放存放左右子树地址集合的数组：

```c
static struct TreeNode **generate_trees(int start, int end, int *size) {

    // ...

    for (int val = start; val <= end; ++val) {
        int left_size = 0;
        struct TreeNode **left_trees = generate_trees(start, val - 1, &left_size);
        int right_size = 0;
        struct TreeNode **right_trees = generate_trees(val + 1, end, &right_size);

        // combine left and right trees

        free(left_trees);
        free(right_trees);
    }
}
```

看起来并没有什么异常。

一个是在单元测试时，遍历返回的每棵树之后：

```cpp
TEST(leetcode_95, normal) {
    int size = 0;
    struct TreeNode **trees = generateTrees_95(4, &size);
    for (int i = 0; i < size; ++i) {
        tree_preorder_print(trees[i]);
        tree_free(trees[i]);
    }
    free(trees);
}
```

`tree_free` 用于释放一棵树，函数本身没有什么问题。如果把这一句注释掉就不会报错，这说明 bug 与这一句有关。身为一个强迫症我无法容忍这个莫名其妙的 bug。

加 log 发现是在 $i = 6$ 时报错，并且对函数传递参数 $\le 3$ 时，不会报错，$\ge 4$ 则会报错。

# 二叉查找树

写出 $n = 4$ 时，$i \le 6$ 时的二叉查找树：

```
1
 \
  2
   \
    3
     \
      4

1
 \
  2
   \
    4
   /
  3

1
 \
  3
 / \
2   4

1
 \
  4
 /
2
 \
  3

  1
   \
    4
   /
  3
 /
2

  2
 / \
1   3
     \
      4

  2
 / \
1   4
   /
  3
```

观察可以发现，$i \le 5$ 时，左子树都为空，右子树各不相同，而 $i = 6$ 与 $i = 5$ 的左子树都为 $1$。

事实上，我们在组合左右子树时，是把子树地址直接赋给根结点的左右结点，因此对于另外一个子树有多种的情况，就会有共用的现象，比如 $i = 5$ 和 $i = 6$ 时就会共用左子树 $1$。而空子树共用则不会有 bug。

当 $i = 5$ 时，我们已经将这棵树完全释放，而 $i = 6$ 的树也用了这棵树的结点 $1$，再次访问时就报错了。

# 解决方法

知道问题所在后，解决方法就很简单了，组合子树时不要直接赋给左右结点，而是把子树完全复制一遍即可。

当然，在用完这些子树后，记得把这些子树也要释放掉。

网上很多方法都是直接赋值，虽然算法可以 AC，但如果用到实际项目中肯定会出现莫名其妙的 bug。

# 实现源码

<https://github.com/qianbinbin/leetcode>
