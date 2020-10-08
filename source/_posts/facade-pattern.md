---
title: 外观模式 Facade Pattern
date: 2020-03-07 23:18:24
tags:
- Java
- 设计模式
---

将一个系统划分为若干子系统，有利于降低系统的复杂性，但是会使客户端的调用复杂化。

外观模式将这些子系统封装到一个类中，对客户端屏蔽这些子系统，提供一个高层、简单的接口。这也是我们不自觉就使用过的设计模式，只不过以前可能不知道名字而已。

<!-- more -->

## 实例

```java
public class SystemA {
    public void operationA() {
        System.out.println("operationA");
    }
}
```

```java
public class SystemB {
    public void operationB() {
        System.out.println("operationB");
    }
}
```

```java
public class SystemC {
    public void operationC() {
        System.out.println("operationC");
    }
}
```

```java
public class Facade {
    private SystemA mSystemA = new SystemA();
    private SystemB mSystemB = new SystemB();
    private SystemC mSystemC = new SystemC();

    public void operation() {
        mSystemA.operationA();
        mSystemB.operationB();
        mSystemC.operationC();
    }
}
```

### 测试

```java
Facade facade = new Facade();
facade.operation();
```

```shell
operationA
operationB
operationC
```

## 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/facade>

## 参考资料

1. [4. 外观模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/facade.html)
