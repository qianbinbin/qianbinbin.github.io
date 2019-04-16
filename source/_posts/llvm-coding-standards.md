---
title: LLVM 编码规范
date: 2019-04-15 14:23:36
tags:
- C++
---

个人翻译，仅供参考。

# Introduction | 引言

This document attempts to describe a few coding standards that are being used in the LLVM source tree. Although no coding standards should be regarded as absolute requirements to be followed in all instances, coding standards are particularly important for large-scale code bases that follow a library-based design (like LLVM).

本文意在描述正在 LLVM 源码树中使用的一些编码规范。即使任何情况下都不应将编码规范视为绝对要求，编码规范对遵循基于库的设计的大规模代码库（如 LLVM）而言也是尤为重要的。

While this document may provide guidance for some mechanical formatting issues, whitespace, or other “microscopic details”, these are not fixed standards. Always follow the golden rule:

尽管本文可能在一些机械的格式问题、空格或其它“细枝末节”提供指南，这些也不是固定标准。始终遵循黄金准则：

  **If you are extending, enhancing, or bug fixing already implemented code, use the style that is already being used so that the source is uniform and easy to follow.**

  **如果你要在已实现的代码中扩展、增强或修复 bug，请使用已有的风格，以便源码风格统一，易于遵循。**

Note that some code bases (e.g. libc++) have really good reasons to deviate from the coding standards. In the case of libc++, this is because the naming and other conventions are dictated by the C++ standard. If you think there is a specific good reason to deviate from the standards here, please bring it up on the LLVM-dev mailing list.

注意，一些代码库（如 libc++）有着很好的理由背离编码规范。就 libc++ 来说，这是因为其命名和其它惯例遵循了 C++ 标准。如果你认为有具体充分的理由背离这里的规范，请将其提交到 LLVM-dev 邮件列表中。

There are some conventions that are not uniformly followed in the code base (e.g. the naming convention). This is because they are relatively new, and a lot of code was written before they were put in place. Our long term goal is for the entire codebase to follow the convention, but we explicitly do not want patches that do large-scale reformatting of existing code. On the other hand, it is reasonable to rename the methods of a class if you’re about to change it in some other way. Just do the reformatting as a separate commit from the functionality change.

代码库中一些惯例并没有统一遵循（例如命名惯例）。这是因为规范相对较新，而很多代码是在规范实施之前写的。我们的长期目标是让整个代码库遵循惯例，但我们明确不要对已有代码进行大规模格式化的补丁。但另一方面，如果你要修改一个类的用法，将其函数重命名也是合理的。只要将格式化与功能修改分离，作为单独的提交即可。

The ultimate goal of these guidelines is to increase the readability and maintainability of our common source base. If you have suggestions for topics to be included, please mail them to [Chris](mailto:sabre@nondot.org).

这些指南的最终目标是提高我们公共源码库的可读性和可维护性。如果你对相关主题有什么建议，请发邮件给[Chris](mailto:sabre@nondot.org)。


# Languages, Libraries, and Standards | 语言，库和标准

Most source code in LLVM and other LLVM projects using these coding standards is C++ code. There are some places where C code is used either due to environment restrictions, historical restrictions, or due to third-party source code imported into the tree. Generally, our preference is for standards conforming, modern, and portable C++ code as the implementation language of choice.

LLVM 及其它应用这些编码规范的 LLVM 项目中的大部分源码都是 C++ 代码。由于环境限制、历史限制，或引入源码树的第三方源码，有些地方使用 C 代码。通常我们倾向于将符合规范、现代和可移植的 C++ 代码作为首选实现语言。

## C++ Standard Versions | C++ 标准版本

LLVM, Clang, and LLD are currently written using C++11 conforming code, although we restrict ourselves to features which are available in the major toolchains supported as host compilers. The LLDB project is even more aggressive in the set of host compilers supported and thus uses still more features. Regardless of the supported features, code is expected to (when reasonable) be standard, portable, and modern C++11 code. We avoid unnecessary vendor-specific extensions, etc.

LLVM、Clang 和 LLD 目前使用 C++11 代码编写，尽管我们
