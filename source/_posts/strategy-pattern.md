---
title: 策略模式 Strategy Pattern
date: 2020-01-27 01:05:48
tags:
- Java
- 设计模式
---

在软件开发中，某一功能可以有多种实现，如果把这些实现都放在一个类中，则不利于增加、修改和替换，此时可以把可能频繁变化的逻辑抽象出来，`Runnable` 就采用了类似的思想。这种设计模式叫策略模式：

1. 定义了一族算法（业务规则）
2. 封装了每个算法
3. 这族的算法可互换代替

策略模式相当于在面向对象层面实现函数式编程，于是对于 Python 等可以把方法作为参数的语言，策略模式就显得没必要了。Java 8 也引入了函数式编程的设计，例如 `java.util.function` 下的各种接口以及 lambda 表达式。

<!-- more -->

# 实例

## 抽象策略

```java
public interface Strategy {
    void execute();
}
```

## 具体策略

```java
public class ConcreteStrategyA implements Strategy {
    @Override
    public void execute() {
        System.out.println("executing " + this);
    }
}
```

```java
public class ConcreteStrategyB implements Strategy {
    @Override
    public void execute() {
        System.out.println("executing " + this);
    }
}
```

## 环境类

```java
public class Context {
    private Strategy mStrategy;

    public Context(Strategy strategy) {
        this.mStrategy = strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.mStrategy = strategy;
    }

    public void execute() {
        mStrategy.execute();
    }
}
```

## 测试

```java
Context context = new Context(new ConcreteStrategyA());
context.execute();
context.setStrategy(new ConcreteStrategyB());
context.execute();
context.setStrategy(() -> System.out.println("executing lambda expression"));
context.execute();
```

```shell
executing io.binac.designpattern.strategy.ConcreteStrategyA@6325a3ee
executing io.binac.designpattern.strategy.ConcreteStrategyB@1d16f93d
executing lambda expression
```

# 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/strategy>

# 参考资料

1. [策略模式 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/%E7%AD%96%E7%95%A5%E6%A8%A1%E5%BC%8F)
2. [5. 策略模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/behavioral_patterns/strategy.html)
