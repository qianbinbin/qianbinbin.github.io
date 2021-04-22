---
title: 适配器模式 Adapter Pattern
date: 2020-03-04 15:59:07
tags:
- Java
- 设计模式
---

假设现有一个旧接口，客户端希望使用一种新接口的形式调用它，此时可以利用适配器模式，将旧接口以新接口的形式进行封装。不过如果适配器使用过多，则整个系统会显得混乱，因此最好只在希望避免修改原有代码时使用。

适配器模式一般包括如下角色：

- Adaptee
  原接口

- Target
  客户端希望调用的接口

- Adapter
  实现 Target 接口的适配器

适配器模式可分为对象适配器模式和类适配器模式。

<!-- more -->

## 实例

### 对象适配器模式

对象适配器实际上是将旧接口的实例封装起来并实现新接口。

![](/images/adapter-pattern/object-adapter.png)

原接口：

```java
public class Adaptee {
    public void specificRequest() {
        new Throwable().printStackTrace();
    }
}
```

其中使用 `new Throwable().printStackTrace()` 打印调用栈。

目标接口：

```java
public interface Target {
    void request();
}
```

适配器：

```java
public class ObjectAdapter implements Target {
    private final Adaptee mAdaptee;

    public ObjectAdapter(Adaptee adaptee) {
        mAdaptee = adaptee;
    }

    @Override
    public void request() {
        mAdaptee.specificRequest();
    }
}
```

#### 测试

```java
Target target = new ObjectAdapter(new Adaptee());
target.request();
```

```shell
java.lang.Throwable
	at io.binac.designpattern.adapter.Adaptee.specificRequest(Adaptee.java:5)
	at io.binac.designpattern.adapter.ObjectAdapter.request(ObjectAdapter.java:12)
```

#### 与装饰器模式的区别

对象适配器是将原接口封装为客户端期望的接口，而{% post_link decorator-pattern 装饰器模式 %}则希望让客户端以与原接口相同的形式调用，且更侧重于在运行时动态地增加功能。

### 类适配器模式

类适配器模式是直接继承 `Adaptee` 并实现 `Target`，而不封装实例。不过对于 Java 等不支持多重继承的语言，`Target` 必须为接口而不能是抽象类。

![](/images/adapter-pattern/class-adapter.png)

适配器实现：

```java
public class ClassAdapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();
    }
}
```

#### 测试

```java
Target target = new ClassAdapter();
target.request();
```

```shell
java.lang.Throwable
	at io.binac.designpattern.adapter.Adaptee.specificRequest(Adaptee.java:5)
	at io.binac.designpattern.adapter.ClassAdapter.request(ClassAdapter.java:6)
```

## 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/adapter>

## 参考资料

1. [1. 适配器模式 — Graphic Design Patterns](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/adapter.html)
2. [Java编程设计模式，第 2 部分: 适配器模式原理及实例介绍](https://www.ibm.com/developerworks/cn/java/j-lo-adapter-pattern/index.html)
