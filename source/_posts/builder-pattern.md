---
title: 生成器模式 Builder Pattern
date: 2020-01-29 10:56:31
tags:
- Java
- 设计模式
---

在设计类时，如果包含或将来可能会包含很多属性，就要重载很多构造方法，不仅设计参数麻烦，调用也比较混乱。

为此可以使用 getter、setter 来逐步构造，但这样构造就分为很多步骤，但这可能会产生其它问题，比如维护者看到一部分代码会认为对象已经构造完毕，而实际上并非如此，使用未完全构造的对象就可能出错。另外这种方法也无法处理 `final` 字段。

Builder 模式就可以很方便地解决这个问题。

<!-- more -->

# 实例

```java
public class Product {
    private int mA;
    private int mB;

    private Product(Builder builder) {
        mA = builder.mA;
        mB = builder.mB;
    }

    @Override
    public String toString() {
        return "A: " + mA + "; B: " + mB;
    }

    public static class Builder {
        private int mA;
        private int mB;

        public Builder setA(int a) {
            mA = a;
            return this;
        }

        public Builder setB(int b) {
            mB = b;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
```

## 测试

```java
Product product = new Product.Builder().setA(1).setB(2).build();
System.out.println(product);
```

```shell
A: 1; B: 2
```

# 问题

链式调用是通过 Builder 类返回 `this` 实现的，那么为什么不让 `Product` 类本身直接实现 setter 并返回 `this` 呢？

一方面，setter 返回 `this` 不是习惯做法，另一方面，客户端调用 setter 仍然可能得到不完整的对象，Builder 则强制创建完整对象。

# 传统写法

上面的写法只保留了核心部分，传统写法比较繁琐，具体可以参考底部链接。

# 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/builder>

# 参考资料

1. [4. 建造者模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/creational_patterns/builder.html)
2. [为什么一定要弄一个Builder内部类？ - 知乎](https://www.zhihu.com/question/326142180/answer/697172067)
3. [秒懂设计模式之建造者模式（Builder pattern） - 知乎](https://zhuanlan.zhihu.com/p/58093669)
